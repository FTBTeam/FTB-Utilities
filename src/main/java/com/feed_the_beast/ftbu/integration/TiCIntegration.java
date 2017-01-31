package com.feed_the_beast.ftbu.integration;

import com.feed_the_beast.ftbl.lib.info.InfoExtendedTextLine;
import com.feed_the_beast.ftbl.lib.info.InfoPage;
import com.feed_the_beast.ftbl.lib.info.ItemListLine;
import com.feed_the_beast.ftbl.lib.info.ItemPageIconRenderer;
import com.feed_the_beast.ftbl.lib.util.LMJsonUtils;
import com.feed_the_beast.ftbl.lib.util.LMStringUtils;
import com.feed_the_beast.ftbu.api.guide.ClientGuideEvent;
import com.feed_the_beast.ftbu.api.guide.IGuide;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.resources.IResource;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.IMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.tools.TinkerMaterials;

import javax.annotation.Nullable;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LatvianModder on 25.01.2017.
 */
public class TiCIntegration
{
    private static final TiCIntegration INSTANCE = new TiCIntegration();

    public static void init()
    {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onGuideEvent(ClientGuideEvent event)
    {
        IGuide guide = event.getModGuide("tconstruct");
        InfoPage pageGuide = guide.getPage();
        pageGuide.setIcon(new ItemPageIconRenderer(Item.getByNameOrId("tconstruct:toolforge")));

        InfoPage pageIntro = loadPage(event, "intro");

        if(pageIntro != null)
        {
            pageIntro.setTitle(new TextComponentString("Introduction"));
            pageIntro.setIcon(new ItemPageIconRenderer(new ItemStack(Item.getByNameOrId("tconstruct:tooltables"), 1, 0)));
            pageGuide.addSub(pageIntro);
        }

        InfoPage toolMaterials = pageGuide.getSub("materials");
        toolMaterials.setTitle(new TextComponentString("Materials"));
        toolMaterials.setIcon(new ItemPageIconRenderer(new ItemStack(Items.IRON_PICKAXE)));

        ImmutableList mats = ImmutableList.of(TinkerMaterials.wood, TinkerMaterials.cobalt, TinkerMaterials.ardite, TinkerMaterials.manyullyn);

        for(Material material : TinkerRegistry.getAllMaterials())
        {
            if(material.isHidden() || !material.hasItems())
            {
                continue;
            }

            InfoPage page = toolMaterials.getSub(material.getIdentifier());
            page.setIcon(new ItemPageIconRenderer(material.getRepresentativeItem()));
            page.setTitle(new TextComponentString(material.getLocalizedName()));

            for(IMaterialStats stats : material.getAllStats())
            {
                ITextComponent component = new TextComponentString(stats.getLocalizedName());
                component.getStyle().setUnderlined(true);
                page.println(component);

                //List<ITrait> traits = material.getAllTraitsForStats(stats.getIdentifier());
                //allTraits.addAll(traits);
                List<ItemStack> parts = new ArrayList<>();

                for(IToolPart part : TinkerRegistry.getToolParts())
                {
                    if(part.hasUseForStat(stats.getIdentifier()))
                    {
                        parts.add(part.getItemstackWithMaterial(material));
                    }
                }

                if(parts.size() > 0)
                {
                    page.println(new ItemListLine(parts, 8));
                }

                for(int i = 0; i < stats.getLocalizedInfo().size(); i++)
                {
                    ITextComponent component1 = new TextComponentString(transformString(stats.getLocalizedInfo().get(i)));
                    component1.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(transformString(stats.getLocalizedDesc().get(i)))));
                    page.println(new InfoExtendedTextLine(component1));
                }

                page.println(null);
            }
        }

        InfoPage modifiers = pageGuide.getSub("modifiers");
        modifiers.setTitle(new TextComponentString("Modifiers"));
        modifiers.setIcon(new ItemPageIconRenderer(new ItemStack(Items.REDSTONE)));

        for(IModifier modifier : TinkerRegistry.getAllModifiers())
        {
            if(modifier.isHidden() || !modifier.hasItemsToApplyWith())
            {
                continue;
            }

            try
            {
                IResource resource = event.getResourceManager().getResource(new ResourceLocation("tconstruct", "book/en_US/modifiers/" + modifier.getIdentifier() + ".json"));
                JsonElement json = LMJsonUtils.fromJson(new InputStreamReader(resource.getInputStream()));

                if(json.isJsonObject())
                {
                    JsonObject o = json.getAsJsonObject();
                    InfoPage page = modifiers.getSub(modifier.getIdentifier());
                    page.setTitle(new TextComponentString(modifier.getLocalizedName()));
                    page.println(transformString(modifier.getLocalizedDesc()));
                    List<ItemStack> displayItems = new ArrayList<>();

                    if(o.has("text"))
                    {
                        page.println(null);
                        for(JsonElement e : o.get("text").getAsJsonArray())
                        {
                            page.println(LMJsonUtils.deserializeTextComponent(e));
                        }
                    }

                    if(o.has("effects"))
                    {
                        page.println(null);
                        page.println("Effects:");
                        for(JsonElement e : o.get("effects").getAsJsonArray())
                        {
                            page.println(LMJsonUtils.deserializeTextComponent(e));
                        }
                    }

                    if(o.has("demoTool"))
                    {
                        for(JsonElement e : o.get("demoTool").getAsJsonArray())
                        {
                            Item item = Item.getByNameOrId(e.getAsString());

                            if(item instanceof ToolCore)
                            {
                                displayItems.add(((ToolCore) item).buildItemForRendering(mats.subList(0, ((ToolCore) item).getRequiredComponents().size())));
                            }
                        }

                        if(!displayItems.isEmpty())
                        {
                            page.println(null);
                            page.println(new ItemListLine(displayItems, 8));
                        }
                    }
                }
            }
            catch(Exception ex)
            {
            }
        }

        InfoPage pageSmeltry = loadPage(event, "smeltery");

        if(pageSmeltry != null)
        {
            pageSmeltry.setTitle(new TextComponentString("Smeltry"));
            pageSmeltry.setIcon(new ItemPageIconRenderer(new ItemStack(Item.getByNameOrId("tconstruct:toolstation"), 1, 0)));
            pageGuide.addSub(pageSmeltry);
        }

        /*
        InfoPage searedFurnace = pagePage.getSub("seared_furnace");
        searedFurnace.println("Seared Furnace");

        InfoPage tinkerTank = pagePage.getSub("tinker_tank");
        tinkerTank.println("Tinker Tank");
        */

        event.add(guide);
    }

    private static String transformString(String s)
    {
        return LMStringUtils.trimAllWhitespace(s.replace("\\n", "\n"));
    }

    @Nullable
    private static InfoPage loadPage(ClientGuideEvent event, String id)
    {
        try
        {
            IResource resource = event.getResourceManager().getResource(new ResourceLocation("tconstruct", "book/en_US/sections/" + id + ".json"));
            JsonElement json = LMJsonUtils.fromJson(new InputStreamReader(resource.getInputStream()));

            if(json.isJsonArray())
            {
                for(JsonElement e : json.getAsJsonArray())
                {

                }

                InfoPage page = new InfoPage(id);

                return page;
            }
        }
        catch(Exception ex)
        {
        }

        return null;
    }
}