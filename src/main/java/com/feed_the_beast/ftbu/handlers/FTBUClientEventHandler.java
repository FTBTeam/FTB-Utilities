package com.feed_the_beast.ftbu.handlers;

import com.feed_the_beast.ftblib.lib.client.ClientUtils;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.client.FTBUClient;
import com.feed_the_beast.ftbu.gui.GuiWarps;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = FTBUFinals.MOD_ID, value = Side.CLIENT)
public class FTBUClientEventHandler
{
	/*
	@SubscribeEvent
    public static void onTooltip(ItemTooltipEvent e)
    {
        if(FTBUConfigGeneral.isItemBanned(item, e.itemStack.getItemDamage()))
        {
            e.toolTip.add(EnumChatFormatting.RED + "Banned item");
        }
    }
    */

	@SubscribeEvent
	public static void onKeyEvent(InputEvent.KeyInputEvent event)
	{
		if (FTBUClient.KEY_WARP.isPressed())
		{
			GuiWarps.INSTANCE = new GuiWarps();
			GuiWarps.INSTANCE.openGui();
			ClientUtils.execClientCommand("/ftb warp gui");
		}
	}
}