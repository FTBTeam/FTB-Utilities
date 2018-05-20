package com.feed_the_beast.ftbutilities.handlers;

import com.feed_the_beast.ftblib.lib.EnumMessageLocation;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftblib.lib.util.CommonUtils;
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
import com.feed_the_beast.ftbutilities.ranks.Rank;
import com.feed_the_beast.ftbutilities.ranks.Ranks;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

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
		String msg = event.getMessage().trim();

		if (FTBUtilitiesConfig.ranks.override_chat)
		{
			Rank rank = Ranks.INSTANCE.getRank(event.getPlayer());

			ITextComponent main = new TextComponentString("");
			ITextComponent name = new TextComponentString(rank.getFormattedName(event.getPlayer().getDisplayNameString()));

			name.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + event.getPlayer().getName() + " "));

			NBTTagCompound hoverNBT = new NBTTagCompound();
			String s = EntityList.getEntityString(event.getPlayer());
			hoverNBT.setString("id", event.getPlayer().getCachedUniqueIdString());

			if (s != null)
			{
				hoverNBT.setString("type", s);
			}

			hoverNBT.setString("name", event.getPlayer().getName());

			name.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new TextComponentString(hoverNBT.toString())));
			name.getStyle().setInsertion(event.getPlayer().getName());

			main.appendSibling(name);
			main.appendSibling(ForgeHooks.newChatWithLinks(msg));

			event.setComponent(main);
		}
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

					boolean prevIsAfk = data.afkTime >= FTBUtilitiesConfig.afk.notification_seconds;
					data.afkTime = (int) ((System.currentTimeMillis() - player.getLastActiveTime()) / 1000L);
					boolean isAFK = data.afkTime >= FTBUtilitiesConfig.afk.notification_seconds;

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
					}

					if (isAFK && playerToKickForAfk == null)
					{
						int maxSeconds = data.player.getRankConfig(FTBUtilitiesPermissions.AFK_TIMER).getInt() * 60;

						if (maxSeconds > 0 && data.afkTime >= maxSeconds)
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
				else if ((t <= CommonUtils.TICKS_SECOND * 10L && t % CommonUtils.TICKS_SECOND == CommonUtils.TICKS_SECOND - 1L) || t == CommonUtils.TICKS_MINUTE || t == CommonUtils.TICKS_MINUTE * 5L || t == CommonUtils.TICKS_MINUTE * 10L || t == CommonUtils.TICKS_MINUTE * 30L)
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