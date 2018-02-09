package com.feed_the_beast.ftbutilities.handlers;

import com.feed_the_beast.ftblib.lib.client.ClientUtils;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.client.FTBUtilitiesClient;
import com.feed_the_beast.ftbutilities.gui.GuiWarps;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = FTBUtilities.MOD_ID, value = Side.CLIENT)
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
		if (FTBUtilitiesClient.KEY_WARP.isPressed())
		{
			GuiWarps.INSTANCE = new GuiWarps();
			GuiWarps.INSTANCE.openGui();
			ClientUtils.execClientCommand("/ftb warp gui");
		}
	}
}