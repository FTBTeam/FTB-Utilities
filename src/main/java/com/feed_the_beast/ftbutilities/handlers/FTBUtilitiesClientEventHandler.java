package com.feed_the_beast.ftbutilities.handlers;

import com.feed_the_beast.ftblib.lib.client.ClientUtils;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.client.FTBUtilitiesClient;
import com.feed_the_beast.ftbutilities.client.FTBUtilitiesClientConfig;
import com.feed_the_beast.ftbutilities.net.MessageRequestBadge;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = FTBUtilities.MOD_ID, value = Side.CLIENT)
public class FTBUtilitiesClientEventHandler
{
	private static final Map<UUID, Icon> BADGE_CACHE = new HashMap<>();
	public static int currentBackupFile = 0;
	public static int totalBackupFiles = 0;
	public static long shutdownTime = 0L;

	public static void readSyncData(NBTTagCompound nbt)
	{
		shutdownTime = System.currentTimeMillis() + nbt.getLong("ShutdownTime");
	}

	public static Icon getBadge(UUID id)
	{
		Icon tex = BADGE_CACHE.get(id);

		if (tex == null)
		{
			tex = Icon.EMPTY;
			BADGE_CACHE.put(id, tex);
			new MessageRequestBadge(id).sendToServer();
		}

		return tex;
	}

	public static void setBadge(UUID id, String url)
	{
		BADGE_CACHE.put(id, Icon.getIcon(url));
	}

	@SubscribeEvent
	public static void onClientDisconnected(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
	{
		BADGE_CACHE.clear();
		currentBackupFile = 0;
		totalBackupFiles = 0;
		shutdownTime = 0L;
	}

	@SubscribeEvent
	public static void onDebugInfoEvent(RenderGameOverlayEvent.Text event)
	{
		if (shutdownTime > 0L && FTBUtilitiesClientConfig.general.show_shutdown_timer)
		{
			long timeLeft = Math.max(0L, shutdownTime - System.currentTimeMillis());

			if (timeLeft > 0L && timeLeft <= FTBUtilitiesClientConfig.general.getShowShutdownTimer())
			{
				event.getLeft().add(TextFormatting.DARK_RED + I18n.format("ftbutilities.lang.timer.shutdown", StringUtils.getTimeString(timeLeft)));
			}
		}

		if (totalBackupFiles > 0 && totalBackupFiles > currentBackupFile && FTBUtilitiesClientConfig.general.show_backup_progress)
		{
			event.getLeft().add(TextFormatting.LIGHT_PURPLE + I18n.format("ftbutilities.lang.timer.backup_progress", currentBackupFile, totalBackupFiles, StringUtils.formatDouble((currentBackupFile / (double) totalBackupFiles) * 100D)));
		}
	}

	@SubscribeEvent
	public static void onKeyEvent(InputEvent.KeyInputEvent event)
	{
		if (FTBUtilitiesClient.KEY_WARP.isPressed())
		{
			ClientUtils.MC.player.sendStatusMessage(new TextComponentString("Feature disabled for now!"), true); //LANG
			//GuiWarps.INSTANCE = new GuiWarps();
			//GuiWarps.INSTANCE.openGui();
			//ClientUtils.execClientCommand("/ftb warp gui");
		}
	}
}