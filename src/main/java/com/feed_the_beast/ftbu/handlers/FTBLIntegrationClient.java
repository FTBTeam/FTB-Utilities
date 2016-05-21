package com.feed_the_beast.ftbu.handlers;

import com.feed_the_beast.ftbl.api.FTBLibCapabilities;
import com.feed_the_beast.ftbl.api.client.FTBLibClient;
import com.feed_the_beast.ftbl.api.events.ReloadEvent;
import com.feed_the_beast.ftbu.badges.ClientBadges;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FTBLIntegrationClient extends FTBLIntegration
{
    @Override
    public void onReloaded(ReloadEvent e)
    {
        super.onReloaded(e);

        if(e.world.getSide().isClient())
        {
            FTBLibClient.clearCachedData();
            ClientBadges.clear();

            //if(e.modeChanged)
            {
                //FIXME: GuideRepoList.reloadFromFolder(e.world.getMode());
            }
        }
    }

    @Override
    public void renderWorld(float pt)
    {
    }

    @Override
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

        //if(FTBUConfigGeneral.isItemBanned(item, e.itemStack.getItemDamage()))
        //	e.toolTip.add(EnumChatFormatting.RED + "Banned item");
    }
}