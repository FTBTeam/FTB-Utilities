package com.feed_the_beast.ftbu.client;

import com.feed_the_beast.ftbl.api.FTBLibCapabilities;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FTBUClientEventHandler
{
    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent e)
    {
        if(e.getItemStack().hasCapability(FTBLibCapabilities.PAINTER_ITEM, null))
        {
            IBlockState paint = e.getItemStack().getCapability(FTBLibCapabilities.PAINTER_ITEM, null).getPaint();

            if(paint != null)
            {
                e.getToolTip().add(String.valueOf(TextFormatting.WHITE) + TextFormatting.BOLD + new ItemStack(paint.getBlock(), 1, paint.getBlock().getMetaFromState(paint)).getDisplayName() + TextFormatting.RESET);
            }
        }

        /*
        if(FTBUConfigGeneral.isItemBanned(item, e.itemStack.getItemDamage()))
        {
            e.toolTip.add(EnumChatFormatting.RED + "Banned item");
        }
        */
    }

    /*
    @SubscribeEvent
    public void renderWorld(RenderWorldLastEvent e)
    {
    }
    */
}