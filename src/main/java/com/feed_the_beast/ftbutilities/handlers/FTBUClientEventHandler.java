package com.feed_the_beast.ftbutilities.handlers;

import com.feed_the_beast.ftblib.lib.client.ClientUtils;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.client.FTBUtilitiesClient;
import com.feed_the_beast.ftbutilities.gui.GuiWarps;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
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
	public static int currentBackupFile = 0;
	public static int totalBackupFiles = 0;

	@SubscribeEvent
	public static void onDebugInfoEvent(RenderGameOverlayEvent.Text event)
	{
		if (totalBackupFiles > 0 && totalBackupFiles > currentBackupFile)
		{
			event.getLeft().add(TextFormatting.LIGHT_PURPLE + "[" + currentBackupFile + " / " + totalBackupFiles + " | " + StringUtils.formatDouble((currentBackupFile / (double) totalBackupFiles) * 100D) + "%]");
		}
	}

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