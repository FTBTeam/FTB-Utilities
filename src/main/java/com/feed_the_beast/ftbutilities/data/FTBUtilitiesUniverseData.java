package com.feed_the_beast.ftbutilities.data;

import com.feed_the_beast.ftblib.FTBLibConfig;
import com.feed_the_beast.ftblib.events.universe.UniverseClosedEvent;
import com.feed_the_beast.ftblib.events.universe.UniverseLoadedEvent;
import com.feed_the_beast.ftblib.events.universe.UniverseSavedEvent;
import com.feed_the_beast.ftblib.lib.config.ConfigValue;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftblib.lib.io.DataReader;
import com.feed_the_beast.ftblib.lib.math.ChunkDimPos;
import com.feed_the_beast.ftblib.lib.math.MathUtils;
import com.feed_the_beast.ftblib.lib.math.Ticks;
import com.feed_the_beast.ftblib.lib.util.FileUtils;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftblib.lib.util.misc.TimeType;
import com.feed_the_beast.ftblib.lib.util.text_components.Notification;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.FTBUtilitiesConfig;
import com.feed_the_beast.ftbutilities.FTBUtilitiesPermissions;
import com.feed_the_beast.ftbutilities.ranks.Ranks;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.storage.ThreadedFileIOBase;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = FTBUtilities.MOD_ID)
public class FTBUtilitiesUniverseData
{
	private static final String BADGE_URL = "https://badges.latmod.com/get?id=";
	private static final ResourceLocation RESTART_TIMER_ID = new ResourceLocation(FTBUtilities.MOD_ID, "restart_timer");

	private static final Map<UUID, String> BADGE_CACHE = new HashMap<>();

	public static long shutdownTime;
	public static final BlockDimPosStorage WARPS = new BlockDimPosStorage();
	//public static final ChatHistory GENERAL_CHAT = new ChatHistory(() -> FTBUtilitiesConfig.chat.general_history_limit);
	private static final List<String> worldLog = new ArrayList<>();

	public static boolean isInSpawn(MinecraftServer server, ChunkDimPos pos)
	{
		if (pos.dim != 0 || (!server.isDedicatedServer() && !FTBUtilitiesConfig.world.spawn_area_in_sp))
		{
			return false;
		}

		int radius = FTBUtilitiesConfig.world.spawn_radius;
		if (radius <= 0)
		{
			return false;
		}

		BlockPos c = server.getWorld(0).getSpawnPoint();
		int minX = MathUtils.chunk(c.getX() - radius);
		int minZ = MathUtils.chunk(c.getZ() - radius);
		int maxX = MathUtils.chunk(c.getX() + radius);
		int maxZ = MathUtils.chunk(c.getZ() + radius);
		return pos.posX >= minX && pos.posX <= maxX && pos.posZ >= minZ && pos.posZ <= maxZ;
	}

	@SubscribeEvent
	public static void onUniversePreLoaded(UniverseLoadedEvent.Pre event)
	{
		if (FTBUtilitiesConfig.world.chunk_claiming)
		{
			ClaimedChunks.instance = new ClaimedChunks(event.getUniverse());
		}

		Ranks.INSTANCE = new Ranks(event.getUniverse());
	}

	@SubscribeEvent
	public static void onUniversePostLoaded(UniverseLoadedEvent.Post event)
	{
		NBTTagCompound nbt = event.getData(FTBUtilities.MOD_ID);
		WARPS.deserializeNBT(nbt.getCompoundTag("Warps"));
	}

	@SubscribeEvent
	public static void onUniverseLoaded(UniverseLoadedEvent.Finished event)
	{
		long now = System.currentTimeMillis();
		shutdownTime = 0L;

		if (FTBUtilitiesConfig.auto_shutdown.enabled && FTBUtilitiesConfig.auto_shutdown.times.length > 0 && (FTBUtilitiesConfig.auto_shutdown.enabled_singleplayer || event.getUniverse().server.isDedicatedServer()))
		{
			Calendar calendar = Calendar.getInstance();
			int currentTime = calendar.get(Calendar.HOUR_OF_DAY) * 3600 + calendar.get(Calendar.MINUTE) * 60 + calendar.get(Calendar.SECOND);
			List<Integer> times = new ArrayList<>(FTBUtilitiesConfig.auto_shutdown.times.length);

			for (String s0 : FTBUtilitiesConfig.auto_shutdown.times)
			{
				try
				{
					String[] s = s0.split(":", 2);

					int t = Integer.parseInt(s[0]) * 3600 + Integer.parseInt(s[1]) * 60;

					if (t <= currentTime)
					{
						t += 24 * 3600;
					}

					times.add(t);
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}

			times.sort(null);

			for (int time : times)
			{
				if (time > currentTime)
				{
					shutdownTime = now + (time - currentTime) * 1000L;
					break;
				}
			}

			if (shutdownTime > 0L)
			{
				FTBUtilities.LOGGER.info("Server will shut down in " + StringUtils.getTimeString(shutdownTime - now));

				Ticks[] ticks = {
						Ticks.MINUTE.x(30),
						Ticks.MINUTE.x(10),
						Ticks.MINUTE.x(5),
						Ticks.MINUTE.x(1),
						Ticks.SECOND.x(10),
						Ticks.SECOND.x(9),
						Ticks.SECOND.x(8),
						Ticks.SECOND.x(7),
						Ticks.SECOND.x(6),
						Ticks.SECOND.x(5),
						Ticks.SECOND.x(4),
						Ticks.SECOND.x(3),
						Ticks.SECOND.x(2),
						Ticks.SECOND.x(1)
				};

				for (Ticks t : ticks)
				{
					event.getUniverse().scheduleTask(TimeType.MILLIS, shutdownTime - t.millis(), universe -> {
						String timeString = t.toTimeString();

						for (EntityPlayerMP player : universe.server.getPlayerList().getPlayers())
						{
							Notification.of(RESTART_TIMER_ID, StringUtils.color(FTBUtilities.lang(player, "ftbutilities.lang.timer.shutdown", timeString), TextFormatting.LIGHT_PURPLE)).send(universe.server, player);
						}
					});
				}
			}
		}

		if (ClaimedChunks.isActive())
		{
			ClaimedChunks.instance.nextChunkloaderUpdate = now + 1000L;
		}
	}

	public static void worldLog(String s)
	{
		StringBuilder out = new StringBuilder();
		Calendar time = Calendar.getInstance();
		appendNum(out, time.get(Calendar.YEAR), '-');
		appendNum(out, time.get(Calendar.MONTH) + 1, '-');
		appendNum(out, time.get(Calendar.DAY_OF_MONTH), ' ');
		appendNum(out, time.get(Calendar.HOUR_OF_DAY), ':');
		appendNum(out, time.get(Calendar.MINUTE), ':');
		appendNum(out, time.get(Calendar.SECOND), ' ');
		out.append(':');
		out.append(' ');
		out.append(s);
		worldLog.add(out.toString());
		Universe.get().markDirty();
	}

	private static void appendNum(StringBuilder sb, int num, char c)
	{
		if (num < 10)
		{
			sb.append('0');
		}
		sb.append(num);
		if (c != '\0')
		{
			sb.append(c);
		}
	}

	@SubscribeEvent
	public static void onUniverseSaved(UniverseSavedEvent event)
	{
		if (ClaimedChunks.isActive())
		{
			ClaimedChunks.instance.processQueue();
		}

		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("Warps", WARPS.serializeNBT());

		//TODO: Save chat as json

		event.setData(FTBUtilities.MOD_ID, nbt);

		if (!worldLog.isEmpty())
		{
			List<String> worldLogCopy = new ArrayList<>(worldLog);
			worldLog.clear();

			ThreadedFileIOBase.getThreadedIOInstance().queueIO(() ->
			{
				try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(FileUtils.newFile(new File(event.getUniverse().server.getDataDirectory(), "logs/world.log")), true))))
				{
					for (String s : worldLogCopy)
					{
						out.println(s);
					}
				}
				catch (IOException ex)
				{
					ex.printStackTrace();
				}

				return false;
			});
		}
	}

	@SubscribeEvent
	public static void onUniverseClosed(UniverseClosedEvent event)
	{
		if (ClaimedChunks.isActive())
		{
			ClaimedChunks.instance.clear();
			ClaimedChunks.instance = null;
		}

		FTBUtilitiesLoadedChunkManager.INSTANCE.clear();

		BADGE_CACHE.clear();
	}

	public static void updateBadge(UUID playerId)
	{
		BADGE_CACHE.remove(playerId);
	}

	public static String getBadge(Universe universe, UUID playerId)
	{
		String badge = BADGE_CACHE.get(playerId);

		if (badge != null)
		{
			return badge;
		}

		badge = getRawBadge(universe, playerId);
		BADGE_CACHE.put(playerId, badge);
		return badge;
	}

	private static String getRawBadge(Universe universe, UUID playerId)
	{
		ForgePlayer player = universe.getPlayer(playerId);

		if (player == null || player.isFake())
		{
			return "";
		}

		FTBUtilitiesPlayerData data = FTBUtilitiesPlayerData.get(player);

		if (!data.renderBadge())
		{
			return "";
		}
		else if (FTBUtilitiesConfig.login.enable_global_badges && !data.disableGlobalBadge())
		{
			try
			{
				String badge = DataReader.get(new URL(BADGE_URL + StringUtils.fromUUID(playerId)), DataReader.TEXT, universe.server.getServerProxy()).string(32);

				if (!badge.isEmpty())// && (FTBUtilitiesConfig.login.enable_event_badges || !response.getHeaderField("Event-Badge").equals("true")))
				{
					return badge;
				}
			}
			catch (Exception ex)
			{
				if (FTBLibConfig.debugging.print_more_errors)
				{
					FTBUtilities.LOGGER.warn("Badge API errored! " + ex);
				}
			}
		}

		if (Ranks.isActive())
		{
			ConfigValue value = Ranks.INSTANCE.getPermission(player.getProfile(), FTBUtilitiesPermissions.BADGE, true);

			if (!value.isNull() && !value.isEmpty())
			{
				return value.getString();
			}
		}

		return "";
	}

	public static boolean clearBadgeCache()
	{
		BADGE_CACHE.clear();
		return true;
	}
}