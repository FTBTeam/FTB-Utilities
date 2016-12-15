package com.feed_the_beast.ftbu.client;

import com.feed_the_beast.ftbu.gui.GuiWarps;
import com.feed_the_beast.ftbu.gui.guide.Guides;
import com.feed_the_beast.ftbu.net.MessageRequestWarpList;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FTBUClientEventHandler
{
    /*
    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent e)
    {
        if(FTBUConfigGeneral.isItemBanned(item, e.itemStack.getItemDamage()))
        {
            e.toolTip.add(EnumChatFormatting.RED + "Banned item");
        }
    }
    */

    @SubscribeEvent
    public void onKeyEvent(InputEvent.KeyInputEvent event)
    {
        if(FTBUClient.KEY_GUIDE.isPressed())
        {
            Guides.openGui();
        }

        if(FTBUClient.KEY_WARP.isPressed())
        {
            GuiWarps.INSTANCE = new GuiWarps();
            GuiWarps.INSTANCE.openGui();
            new MessageRequestWarpList().sendToServer();
        }
    }
}