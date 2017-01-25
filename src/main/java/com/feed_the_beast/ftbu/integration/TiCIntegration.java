package com.feed_the_beast.ftbu.integration;

import com.feed_the_beast.ftbl.lib.info.InfoExtendedTextLine;
import com.feed_the_beast.ftbl.lib.info.InfoPage;
import com.feed_the_beast.ftbl.lib.info.ItemListLine;
import com.feed_the_beast.ftbl.lib.info.ItemPageIconRenderer;
import com.feed_the_beast.ftbl.lib.util.LMStringUtils;
import com.feed_the_beast.ftbu.api.guide.ClientGuideEvent;
import com.feed_the_beast.ftbu.gui.guide.InfoPageGuide;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.IMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tools.IToolPart;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LatvianModder on 25.01.2017.
 */
public class TiCIntegration
{
    public static final TiCIntegration INSTANCE = new TiCIntegration();

    public void init()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onGuideEvent(ClientGuideEvent event)
    {
        ModContainer mod = Loader.instance().getIndexedModList().get("tconstruct");
        InfoPageGuide guide = new InfoPageGuide(mod);
        guide.getPage().setIcon(new ItemPageIconRenderer(Item.getByNameOrId("tconstruct:toolforge")));

        //Item.getByNameOrId("tconstruct:tooltables")
        InfoPage introduction = guide.getPage().getSub("introduction");
        introduction.println("Introduction");

        //Items.IRON_PICKAXE
        InfoPage materials = guide.getPage().getSub("materials");

        for(Material material : TinkerRegistry.getAllMaterials())
        {
            if(material.isHidden() || !material.hasItems())
            {
                continue;
            }

            InfoPage page = new InfoPage(material.getIdentifier());
            page.setIcon(new ItemPageIconRenderer(material.getRepresentativeItem()));
            page.setTitle(new TextComponentString(material.getLocalizedNameColored()));

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
                    ITextComponent component1 = new TextComponentString(LMStringUtils.trimAllWhitespace(stats.getLocalizedInfo().get(i).replace("\\n", "\n")));
                    component1.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(LMStringUtils.trimAllWhitespace(stats.getLocalizedDesc().get(i).replace("\\n", "\n")))));
                    page.println(new InfoExtendedTextLine(component1));
                }

                page.println(null);
            }

            materials.addSub(page);
        }
        
        /*

        //Items.IRON_INGOT
        InfoPage toolMaterials = guide.getPage().getSub("tool_materials");
        toolMaterials.println("Tool Materials");

        //Items.REDSTONE
        InfoPage modifiers = guide.getPage().getSub("modifiers");
        modifiers.println("Materials");

        InfoPage smeltry = guide.getPage().getSub("smeltry");
        smeltry.println("Smeltry");

        InfoPage searedFurnace = guide.getPage().getSub("seared_furnace");
        searedFurnace.println("Seared Furnace");

        InfoPage tinkerTank = guide.getPage().getSub("tinker_tank");
        tinkerTank.println("Tinker Tank");

        //Items.BOW
        InfoPage bowMaterials = guide.getPage().getSub("bow_materials");
        bowMaterials.println("Bow Materials");
        
        */

        event.add(guide);
    }
}