package com.feed_the_beast.ftbutilities.handlers;

import com.feed_the_beast.ftblib.events.universe.UniverseClearCacheEvent;
import com.feed_the_beast.ftblib.lib.EnumMessageLocation;
import com.feed_the_beast.ftblib.lib.config.ConfigEnum;
import com.feed_the_beast.ftblib.lib.config.RankConfigAPI;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftblib.lib.util.NBTUtils;
import com.feed_the_beast.ftblib.lib.util.ServerUtils;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftblib.lib.util.text_components.Notification;
import com.feed_the_beast.ftblib.lib.util.text_components.TextComponentParser;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.FTBUtilitiesCommon;
import com.feed_the_beast.ftbutilities.FTBUtilitiesConfig;
import com.feed_the_beast.ftbutilities.FTBUtilitiesPermissions;
import com.feed_the_beast.ftbutilities.command.CmdShutdown;
import com.feed_the_beast.ftbutilities.data.ClaimedChunks;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesPlayerData;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesUniverseData;
import com.feed_the_beast.ftbutilities.net.MessageUpdatePlayTime;
import com.feed_the_beast.ftbutilities.net.MessageUpdateTabName;
import com.feed_the_beast.ftbutilities.ranks.Ranks;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.StatList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.server.permission.PermissionAPI;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = FTBUtilities.MOD_ID)
public class FTBUtilitiesServerEventHandler
{
	private static final ResourceLocation AFK_ID = new ResourceLocation(FTBUtilities.MOD_ID, "afk");
	private static final Pattern STRIKETHROUGH_PATTERN = Pattern.compile("\\~\\~(.*?)\\~\\~");
	private static final String STRIKETHROUGH_REPLACE = "&m$1&m";
	private static final Pattern BOLD_PATTERN = Pattern.compile("\\*\\*(.*?)\\*\\*|__(.*?)__");
	private static final String BOLD_REPLACE = "&l$1$2&l";
	private static final Pattern ITALIC_PATTERN = Pattern.compile("\\*(.*?)\\*|_(.*?)_");
	private static final String ITALIC_REPLACE = "&o$1$2&o";

	@SubscribeEvent
	public static void onCacheCleared(UniverseClearCacheEvent event)
	{
		if (Ranks.INSTANCE != null)
		{
			Ranks.INSTANCE.clearCache();
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onServerChatEvent(ServerChatEvent event)
	{
		if (!FTBUtilitiesConfig.ranks.override_chat || !Ranks.isActive())
		{
			return;
		}

		EntityPlayerMP player = event.getPlayer();

		if (!PermissionAPI.hasPermission(player, FTBUtilitiesPermissions.CHAT_SPEAK) || NBTUtils.getPersistedData(player, false).getBoolean(FTBUtilitiesPlayerData.TAG_MUTED))
		{
			player.sendStatusMessage(StringUtils.color(FTBUtilities.lang(player, "commands.mute.muted"), TextFormatting.RED), true);
			event.setCanceled(true);
			return;
		}

		ITextComponent main = new TextComponentString("");
		FTBUtilitiesPlayerData data = FTBUtilitiesPlayerData.get(Universe.get().getPlayer(player));
		main.appendSibling(data.getNameForChat(player));

		String message = event.getMessage().trim();

		boolean b = false;

		if (!message.contains("https://") && !message.contains("http://") && PermissionAPI.hasPermission(player, FTBUtilitiesPermissions.CHAT_FORMATTING))
		{
			for (Map.Entry<String, String> entry : FTBUtilitiesCommon.KAOMOJIS.entrySet())
			{
				message = message.replace(entry.getValue(), "<emoji:" + entry.getKey() + ">");
			}

			b = !message.equals(message = STRIKETHROUGH_PATTERN.matcher(message).replaceAll(STRIKETHROUGH_REPLACE)) | b;
			b = !message.equals(message = BOLD_PATTERN.matcher(message).replaceAll(BOLD_REPLACE)) | b;
			b = !message.equals(message = ITALIC_PATTERN.matcher(message).replaceAll(ITALIC_REPLACE)) | b;

			for (Map.Entry<String, String> entry : FTBUtilitiesCommon.KAOMOJIS.entrySet())
			{
				message = message.replace("<emoji:" + entry.getKey() + ">", entry.getValue());
			}
		}

		ITextComponent text;

		if (b)
		{
			text = TextComponentParser.parse(message, null);
		}
		else
		{
			text = ForgeHooks.newChatWithLinks(message);
		}

		TextFormatting colortf = (TextFormatting) ((ConfigEnum) RankConfigAPI.get(player, FTBUtilitiesPermissions.CHAT_TEXT_COLOR)).getValue();

		if (colortf != TextFormatting.WHITE)
		{
			text.getStyle().setColor(colortf);
		}

		if (Ranks.INSTANCE.getPermissionResult(player, FTBUtilitiesPermissions.CHAT_TEXT_BOLD, false) == Event.Result.ALLOW)
		{
			text.getStyle().setBold(true);
		}

		if (Ranks.INSTANCE.getPermissionResult(player, FTBUtilitiesPermissions.CHAT_TEXT_ITALIC, false) == Event.Result.ALLOW)
		{
			text.getStyle().setItalic(true);
		}

		if (Ranks.INSTANCE.getPermissionResult(player, FTBUtilitiesPermissions.CHAT_TEXT_UNDERLINED, false) == Event.Result.ALLOW)
		{
			text.getStyle().setUnderlined(true);
		}

		if (Ranks.INSTANCE.getPermissionResult(player, FTBUtilitiesPermissions.CHAT_TEXT_STRIKETHROUGH, false) == Event.Result.ALLOW)
		{
			text.getStyle().setStrikethrough(true);
		}

		if (Ranks.INSTANCE.getPermissionResult(player, FTBUtilitiesPermissions.CHAT_TEXT_OBFUSCATED, false) == Event.Result.ALLOW)
		{
			text.getStyle().setObfuscated(true);
		}

		main.appendSibling(text);
		event.setComponent(main);
	}

	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event)
	{
		if (!Universe.loaded())
		{
			return;
		}

		Universe universe = Universe.get();

		long now = System.currentTimeMillis();

		if (event.phase == TickEvent.Phase.START)
		{
			if (ClaimedChunks.isActive())
			{
				ClaimedChunks.instance.update(universe, now);
			}
		}
		else
		{
			EntityPlayerMP playerToKickForAfk = null; //Do one at time, easier
			boolean afkEnabled = FTBUtilitiesConfig.afk.isEnabled(universe.server);

			for (EntityPlayerMP player : universe.server.getPlayerList().getPlayers())
			{
				if (ServerUtils.isFake(player))
				{
					continue;
				}

				boolean fly = player.capabilities.allowFlying;

				if (!player.capabilities.isCreativeMode && NBTUtils.getPersistedData(player, false).getBoolean(FTBUtilitiesPlayerData.TAG_FLY))
				{
					player.capabilities.allowFlying = true;
				}

				if (fly != player.capabilities.allowFlying)
				{
					player.sendPlayerAbilities();
				}

				if (afkEnabled)
				{
					FTBUtilitiesPlayerData data = FTBUtilitiesPlayerData.get(universe.getPlayer(player));
					boolean prevIsAfk = data.afkTime >= FTBUtilitiesConfig.afk.getNotificationTimer();
					data.afkTime = System.currentTimeMillis() - player.getLastActiveTime();
					boolean isAFK = data.afkTime >= FTBUtilitiesConfig.afk.getNotificationTimer();

					if (prevIsAfk != isAFK)
					{
						for (EntityPlayerMP player1 : universe.server.getPlayerList().getPlayers())
						{
							EnumMessageLocation location = FTBUtilitiesPlayerData.get(universe.getPlayer(player1)).getAFKMessageLocation();

							if (location != EnumMessageLocation.OFF)
							{
								ITextComponent component = FTBUtilities.lang(player1, isAFK ? "permission.ftbutilities.afk.timer.is_afk" : "permission.ftbutilities.afk.timer.isnt_afk", player.getDisplayName());
								component.getStyle().setColor(TextFormatting.GRAY);

								if (location == EnumMessageLocation.CHAT)
								{
									player1.sendMessage(component);
								}
								else
								{
									Notification.of(AFK_ID, component).send(universe.server, player1);
								}
							}
						}

						FTBUtilities.LOGGER.info(player.getName() + (isAFK ? " is now AFK" : " is no longer AFK"));
						new MessageUpdateTabName(player).sendToAll();
					}

					if (playerToKickForAfk == null)
					{
						long maxTime = RankConfigAPI.get(player.server, player.getGameProfile(), FTBUtilitiesPermissions.AFK_TIMER).getTimer().millis();

						if (maxTime > 0L && data.afkTime >= maxTime)
						{
							playerToKickForAfk = player;
						}
					}
				}
			}

			if (playerToKickForAfk != null && playerToKickForAfk.connection != null)
			{
				playerToKickForAfk.connection.disconnect(new TextComponentTranslation("multiplayer.disconnect.idling"));
			}

			if (FTBUtilitiesUniverseData.shutdownTime > 0L && FTBUtilitiesUniverseData.shutdownTime - now <= 0)
			{
				CmdShutdown.shutdown(universe.server);
			}
		}
	}

	@SubscribeEvent
	public static void onWorldTick(TickEvent.WorldTickEvent event)
	{
		if (!event.world.isRemote && event.phase == TickEvent.Phase.START && event.world.provider.getDimension() == FTBUtilitiesConfig.world.spawn_dimension)
		{
			if (FTBUtilitiesConfig.world.forced_spawn_dimension_time != -1)
			{
				event.world.setWorldTime(FTBUtilitiesConfig.world.forced_spawn_dimension_time);
			}

			if (FTBUtilitiesConfig.world.forced_spawn_dimension_weather != -1)
			{
				event.world.getWorldInfo().setRaining(FTBUtilitiesConfig.world.forced_spawn_dimension_weather >= 1);
				event.world.getWorldInfo().setThundering(FTBUtilitiesConfig.world.forced_spawn_dimension_weather >= 2);
			}

			if (FTBUtilitiesConfig.world.show_playtime && event.world.getTotalWorldTime() % 20L == 7L)
			{
				for (EntityPlayerMP player : event.world.getMinecraftServer().getPlayerList().getPlayers())
				{
					new MessageUpdatePlayTime(player.getStatFile().readStat(StatList.PLAY_ONE_MINUTE)).sendTo(player);
				}
			}
		}
	}
}
