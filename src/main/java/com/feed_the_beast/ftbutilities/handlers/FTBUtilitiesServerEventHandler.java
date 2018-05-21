package com.feed_the_beast.ftbutilities.handlers;

import com.feed_the_beast.ftblib.lib.EnumMessageLocation;
import com.feed_the_beast.ftblib.lib.config.RankConfigAPI;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftblib.lib.math.Ticks;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftblib.lib.util.text_components.Notification;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.FTBUtilitiesConfig;
import com.feed_the_beast.ftbutilities.FTBUtilitiesPermissions;
import com.feed_the_beast.ftbutilities.cmd.CmdShutdown;
import com.feed_the_beast.ftbutilities.data.ClaimedChunks;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesPlayerData;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesUniverseData;
import com.feed_the_beast.ftbutilities.data.backups.Backups;
import com.feed_the_beast.ftbutilities.ranks.Ranks;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.server.permission.context.IContext;
import net.minecraftforge.server.permission.context.PlayerContext;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = FTBUtilities.MOD_ID)
public class FTBUtilitiesServerEventHandler
{
	private static final ResourceLocation RESTART_TIMER_ID = new ResourceLocation(FTBUtilities.MOD_ID, "restart_timer");
	private static final ResourceLocation AFK_ID = new ResourceLocation(FTBUtilities.MOD_ID, "afk");

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onServerChatEvent(ServerChatEvent event)
	{
		if (!FTBUtilitiesConfig.ranks.override_chat || Ranks.INSTANCE == null)
		{
			return;
		}

		EntityPlayerMP player = event.getPlayer();
		MinecraftServer server = player.mcServer;
		IContext context = new PlayerContext(player);
		GameProfile profile = player.getGameProfile();
		ITextComponent main = new TextComponentString("");
		main.appendSibling(FTBUtilitiesPermissions.CHAT_PREFIX.format(server, profile, context, new TextComponentString(RankConfigAPI.get(server, profile, FTBUtilitiesPermissions.CHAT_PREFIX_TEXT, context).getString())));
		main.appendSibling(FTBUtilitiesPermissions.CHAT_NAME.format(server, profile, context, player.getDisplayName()));
		main.appendSibling(FTBUtilitiesPermissions.CHAT_SUFFIX.format(server, profile, context, new TextComponentString(RankConfigAPI.get(server, profile, FTBUtilitiesPermissions.CHAT_SUFFIX_TEXT, context).getString())));
		main.appendSibling(FTBUtilitiesPermissions.CHAT_TEXT.format(server, profile, context, ForgeHooks.newChatWithLinks(event.getMessage().trim())));
		event.setComponent(main);
	}

	@SubscribeEvent
	public static void onServerTickEvent(TickEvent.ServerTickEvent event)
	{
		if (!Universe.loaded())
		{
			return;
		}

		Universe universe = Universe.get();

		long now = universe.world.getTotalWorldTime();

		if (event.phase == TickEvent.Phase.START)
		{
			if (ClaimedChunks.instance != null)
			{
				ClaimedChunks.instance.update(universe.server, now);
			}
		}
		else
		{
			if (FTBUtilitiesConfig.afk.isEnabled(universe.server))
			{
				EntityPlayerMP playerToKickForAfk = null; //Do one at time, easier

				for (EntityPlayerMP player : universe.server.getPlayerList().getPlayers())
				{
					FTBUtilitiesPlayerData data = FTBUtilitiesPlayerData.get(universe.getPlayer(player));

					if (!player.capabilities.isCreativeMode && data.getFly())
					{
						boolean fly = player.capabilities.allowFlying;
						player.capabilities.allowFlying = true;

						if (!fly)
						{
							player.sendPlayerAbilities();
						}
					}

					boolean prevIsAfk = data.afkTicks >= Ticks.st(FTBUtilitiesConfig.afk.notification_seconds);
					data.afkTicks = (int) ((System.currentTimeMillis() - player.getLastActiveTime()) * Ticks.SECOND / 1000L);
					boolean isAFK = data.afkTicks >= Ticks.st(FTBUtilitiesConfig.afk.notification_seconds);

					if (prevIsAfk != isAFK)
					{
						for (EntityPlayerMP player1 : universe.server.getPlayerList().getPlayers())
						{
							EnumMessageLocation location = FTBUtilitiesPlayerData.get(universe.getPlayer(player1)).getAFKMessageLocation();

							if (location != EnumMessageLocation.OFF)
							{
								ITextComponent component = FTBUtilities.lang(player1, isAFK ? "rank_config.ftbutilities.afk.timer.is_afk" : "rank_config.ftbutilities.afk.timer.isnt_afk", player.getDisplayName());
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

						FTBUtilities.LOGGER.info(player.getName() + (isAFK ? "is now AFK" : " is no longer AFK"));
					}

					if (playerToKickForAfk == null)
					{
						long maxTicks = data.player.getRankConfig(FTBUtilitiesPermissions.AFK_TIMER).getLong();

						if (maxTicks > 0L && data.afkTicks >= maxTicks)
						{
							playerToKickForAfk = player;
						}
					}
				}

				if (playerToKickForAfk != null && playerToKickForAfk.connection != null)
				{
					playerToKickForAfk.connection.disconnect(new TextComponentTranslation("multiplayer.disconnect.idling"));
				}
			}

			if (FTBUtilitiesUniverseData.shutdownTime > 0L)
			{
				long t = FTBUtilitiesUniverseData.shutdownTime - now;

				if (t <= 0)
				{
					CmdShutdown.shutdown(universe.server);
					return;
				}
				else if ((t <= Ticks.st(10L) && t % Ticks.SECOND == Ticks.SECOND - 1L) || t == Ticks.mt(1L) || t == Ticks.mt(5L) || t == Ticks.mt(10L) || t == Ticks.mt(30L))
				{
					for (EntityPlayerMP player : universe.server.getPlayerList().getPlayers())
					{
						Notification.of(RESTART_TIMER_ID, StringUtils.color(FTBUtilities.lang(player, "ftbutilities.lang.timer.shutdown", StringUtils.getTimeStringTicks(t)), TextFormatting.LIGHT_PURPLE)).send(universe.server, player);
					}
				}
			}

			Backups.INSTANCE.tick(universe, now);
		}
	}
}