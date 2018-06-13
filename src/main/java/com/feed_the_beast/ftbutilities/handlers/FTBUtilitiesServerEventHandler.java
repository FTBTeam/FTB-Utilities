package com.feed_the_beast.ftbutilities.handlers;

import com.feed_the_beast.ftblib.lib.EnumMessageLocation;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftblib.lib.util.text_components.Notification;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.FTBUtilitiesConfig;
import com.feed_the_beast.ftbutilities.FTBUtilitiesPermissions;
import com.feed_the_beast.ftbutilities.command.CmdShutdown;
import com.feed_the_beast.ftbutilities.data.ClaimedChunks;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesPlayerData;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesUniverseData;
import com.feed_the_beast.ftbutilities.data.backups.Backups;
import com.feed_the_beast.ftbutilities.ranks.Rank;
import com.feed_the_beast.ftbutilities.ranks.Ranks;
import net.minecraft.entity.player.EntityPlayerMP;
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
import net.minecraftforge.server.permission.context.PlayerContext;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = FTBUtilities.MOD_ID)
public class FTBUtilitiesServerEventHandler
{
	private static final ResourceLocation AFK_ID = new ResourceLocation(FTBUtilities.MOD_ID, "afk");

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onServerChatEvent(ServerChatEvent event)
	{
		if (!FTBUtilitiesConfig.ranks.override_chat || !Ranks.isActive())
		{
			return;
		}

		EntityPlayerMP player = event.getPlayer();
		Rank rank = Ranks.INSTANCE.getRank(player.mcServer, player.getGameProfile(), new PlayerContext(player));

		if (rank.isNone())
		{
			return;
		}

		ITextComponent main = new TextComponentString("");
		FTBUtilitiesPlayerData data = FTBUtilitiesPlayerData.get(Universe.get().getPlayer(player));
		main.appendSibling(data.getNameForChat(rank));
		main.appendSibling(FTBUtilitiesPermissions.CHAT_TEXT.format(rank, ForgeHooks.newChatWithLinks(event.getMessage().trim()), null));
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
					}

					if (playerToKickForAfk == null)
					{
						long maxTime = data.player.getRankConfig(FTBUtilitiesPermissions.AFK_TIMER).getTimer().millis();

						if (maxTime > 0L && data.afkTime >= maxTime)
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

			Backups.INSTANCE.tick(universe, now);

			if (FTBUtilitiesUniverseData.shutdownTime > 0L && FTBUtilitiesUniverseData.shutdownTime - now <= 0 && Backups.INSTANCE.doingBackup == 0)
			{
				CmdShutdown.shutdown(universe.server);
			}
		}
	}
}
