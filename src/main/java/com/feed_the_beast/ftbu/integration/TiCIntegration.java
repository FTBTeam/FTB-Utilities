package com.feed_the_beast.ftbu.integration;

import com.feed_the_beast.ftbl.api.events.ClientGuideEvent;
import com.feed_the_beast.ftbl.lib.client.DrawableItem;
import com.feed_the_beast.ftbl.lib.client.DrawableObjectList;
import com.feed_the_beast.ftbl.lib.guide.DrawableObjectListLine;
import com.feed_the_beast.ftbl.lib.guide.GuideExtendedTextLine;
import com.feed_the_beast.ftbl.lib.guide.GuidePage;
import com.feed_the_beast.ftbl.lib.guide.GuideTitlePage;
import com.feed_the_beast.ftbl.lib.util.JsonUtils;
import com.feed_the_beast.ftbl.lib.util.StringUtils;
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
import java.util.Collections;

/**
 * Created by LatvianModder on 25.01.2017.
 */
public class TiCIntegration
{
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onGuideEvent(ClientGuideEvent event)
    {
        GuideTitlePage guide = event.getModGuide("tconstruct");
        guide.setIcon(new DrawableItem(new ItemStack(Item.getByNameOrId("tconstruct:toolforge"), 1, 0)));

        GuidePage pageIntro = loadPage(event, "intro");

        if(pageIntro != null)
        {
            pageIntro.setTitle(new TextComponentString("Introduction"));
            pageIntro.setIcon(new DrawableItem(new ItemStack(Item.getByNameOrId("tconstruct:tooltables"), 1, 0)));
            guide.addSub(pageIntro);
        }

        GuidePage toolMaterials = guide.getSub("materials");
        toolMaterials.setTitle(new TextComponentString("Materials"));
        toolMaterials.setIcon(new DrawableItem(new ItemStack(Items.IRON_PICKAXE)));

        ImmutableList mats = ImmutableList.of(TinkerMaterials.wood, TinkerMaterials.cobalt, TinkerMaterials.ardite, TinkerMaterials.manyullyn);

        for(Material material : TinkerRegistry.getAllMaterials())
        {
            if(material.isHidden() || !material.hasItems())
            {
                continue;
            }

            GuidePage page = toolMaterials.getSub(material.getIdentifier());
            page.setIcon(new DrawableItem(material.getRepresentativeItem()));
            page.setTitle(new TextComponentString(material.getLocalizedName()));

            for(IMaterialStats stats : material.getAllStats())
            {
                ITextComponent component = new TextComponentString(stats.getLocalizedName());
                component.getStyle().setUnderlined(true);
                page.println(component);

                //List<ITrait> traits = material.getAllTraitsForStats(stats.getIdentifier());
                //allTraits.addAll(traits);
                DrawableObjectList parts = new DrawableObjectList(Collections.emptyList());

                for(IToolPart part : TinkerRegistry.getToolParts())
                {
                    if(part.hasUseForStat(stats.getIdentifier()))
                    {
                        parts.list.add(new DrawableItem(part.getItemstackWithMaterial(material)));
                    }
                }

                if(parts.list.size() > 0)
                {
                    page.println(new DrawableObjectListLine(parts, 8));
                }

                for(int i = 0; i < stats.getLocalizedInfo().size(); i++)
                {
                    ITextComponent component1 = new TextComponentString(transformString(stats.getLocalizedInfo().get(i)));
                    component1.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(transformString(stats.getLocalizedDesc().get(i)))));
                    page.println(new GuideExtendedTextLine(component1));
                }

                page.println(null);
            }
        }

        GuidePage modifiers = guide.getSub("modifiers");
        modifiers.setTitle(new TextComponentString("Modifiers"));
        modifiers.setIcon(new DrawableItem(new ItemStack(Items.REDSTONE)));

        for(IModifier modifier : TinkerRegistry.getAllModifiers())
        {
            if(modifier.isHidden() || !modifier.hasItemsToApplyWith())
            {
                continue;
            }

            try
            {
                IResource resource = event.getResourceManager().getResource(new ResourceLocation("tconstruct", "book/en_US/modifiers/" + modifier.getIdentifier() + ".json"));
                JsonElement json = JsonUtils.fromJson(new InputStreamReader(resource.getInputStream()));

                if(json.isJsonObject())
                {
                    JsonObject o = json.getAsJsonObject();
                    GuidePage page = modifiers.getSub(modifier.getIdentifier());
                    page.setTitle(new TextComponentString(modifier.getLocalizedName()));
                    page.println(transformString(modifier.getLocalizedDesc()));
                    DrawableObjectList displayItems = new DrawableObjectList(Collections.emptyList());

                    if(o.has("text"))
                    {
                        page.println(null);
                        for(JsonElement e : o.get("text").getAsJsonArray())
                        {
                            page.println(JsonUtils.deserializeTextComponent(e));
                        }
                    }

                    if(o.has("effects"))
                    {
                        page.println(null);
                        page.println("Effects:");
                        for(JsonElement e : o.get("effects").getAsJsonArray())
                        {
                            page.println(JsonUtils.deserializeTextComponent(e));
                        }
                    }

                    if(o.has("demoTool"))
                    {
                        for(JsonElement e : o.get("demoTool").getAsJsonArray())
                        {
                            Item item = Item.getByNameOrId(e.getAsString());

                            if(item instanceof ToolCore)
                            {
                                displayItems.list.add(new DrawableItem(((ToolCore) item).buildItemForRendering(mats.subList(0, ((ToolCore) item).getRequiredComponents().size()))));
                            }
                        }

                        if(!displayItems.list.isEmpty())
                        {
                            page.println(null);
                            page.println(new DrawableObjectListLine(displayItems, 8));
                        }
                    }
                }
            }
            catch(Exception ex)
            {
            }
        }

        modifiers.sort(false);

        GuidePage pageSmeltry = loadPage(event, "smeltery");

        if(pageSmeltry != null)
        {
            pageSmeltry.setTitle(new TextComponentString("Smeltry"));
            pageSmeltry.setIcon(new DrawableItem(new ItemStack(Item.getByNameOrId("tconstruct:toolstation"), 1, 0)));
            guide.addSub(pageSmeltry);
        }

        /*
        GuidePage searedFurnace = pagePage.getSub("seared_furnace");
        searedFurnace.println("Seared Furnace");

        GuidePage tinkerTank = pagePage.getSub("tinker_tank");
        tinkerTank.println("Tinker Tank");
        */
    }

    private static String transformString(String s)
    {
        return StringUtils.trimAllWhitespace(s.replace("\\n", "\n"));
    }

    @Nullable
    private static GuidePage loadPage(ClientGuideEvent event, String id)
    {
        try
        {
            //FIXME
            IResource resource = event.getResourceManager().getResource(new ResourceLocation("tconstruct", "book/en_US/sections/" + id + ".json"));
            JsonElement json = JsonUtils.fromJson(new InputStreamReader(resource.getInputStream()));

            if(json.isJsonArray())
            {
                GuidePage page = new GuidePage(id);

                for(JsonElement e : json.getAsJsonArray())
                {

                }

                return page;
            }
        }
        catch(Exception ex)
        {
        }

        return null;
    }
}