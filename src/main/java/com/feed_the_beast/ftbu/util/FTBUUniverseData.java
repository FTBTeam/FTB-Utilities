package com.feed_the_beast.ftbu.util;

import com.feed_the_beast.ftblib.events.universe.UniverseClosedEvent;
import com.feed_the_beast.ftblib.events.universe.UniverseLoadedEvent;
import com.feed_the_beast.ftblib.events.universe.UniverseSavedEvent;
import com.feed_the_beast.ftblib.lib.EventHandler;
import com.feed_the_beast.ftblib.lib.math.ChunkDimPos;
import com.feed_the_beast.ftblib.lib.math.MathUtils;
import com.feed_the_beast.ftblib.lib.util.CommonUtils;
import com.feed_the_beast.ftblib.lib.util.ServerUtils;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftbu.FTBUConfig;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.FTBULang;
import com.feed_the_beast.ftbu.data.ChunkUpgrade;
import com.feed_the_beast.ftbu.data.ClaimedChunks;
import com.feed_the_beast.ftbu.handlers.FTBLibIntegration;
import com.feed_the_beast.ftbu.util.backups.Backups;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
@EventHandler
public class FTBUUniverseData
{
	public static long shutdownTime;
	public static final BlockDimPosStorage WARPS = new BlockDimPosStorage();
	//public static final ChatHistory GENERAL_CHAT = new ChatHistory(() -> FTBUConfig.chat.general_history_limit);
	public static final Map<String, ChunkUpgrade> CHUNK_UPGRADES = new HashMap<>();

	public static boolean isInSpawn(ChunkDimPos pos)
	{
		MinecraftServer server = ServerUtils.getServer();

		if (pos.dim != 0 || (!server.isDedicatedServer() && !FTBUConfig.world.spawn_area_in_sp))
		{
			return false;
		}

		int radius = server.getSpawnProtectionSize();
		if (radius <= 0)
		{
			return false;
		}

		BlockPos c = server.getEntityWorld().getSpawnPoint();
		int minX = MathUtils.chunk(c.getX() - radius);
		int minZ = MathUtils.chunk(c.getZ() - radius);
		int maxX = MathUtils.chunk(c.getX() + radius);
		int maxZ = MathUtils.chunk(c.getZ() + radius);
		return pos.posX >= minX && pos.posX <= maxX && pos.posZ >= minZ && pos.posZ <= maxZ;
	}

	@SubscribeEvent
	public static void onUniversePreLoaded(UniverseLoadedEvent.Pre event)
	{
		ClaimedChunks.close();
		ClaimedChunks.instance = new ClaimedChunks();
	}

	@SubscribeEvent
	public static void onUniversePostLoaded(UniverseLoadedEvent.Post event)
	{
		NBTTagCompound nbt = event.getData(FTBLibIntegration.FTBU_DATA);
		FTBUUniverseData.WARPS.deserializeNBT(nbt.getCompoundTag("Warps"));
	}

	@SubscribeEvent
	public static void onUniverseLoaded(UniverseLoadedEvent.Finished event)
	{
		long start = event.getWorld().getTotalWorldTime();
		Backups.INSTANCE.nextBackup = start + FTBUConfig.backups.ticks();

		if (FTBUConfig.auto_shutdown.enabled && FTBUConfig.auto_shutdown.times.length > 0 && ServerUtils.getServer().isDedicatedServer())
		{
			Calendar calendar = Calendar.getInstance();
			int currentTime = calendar.get(Calendar.HOUR_OF_DAY) * 3600 + calendar.get(Calendar.MINUTE) * 60 + calendar.get(Calendar.SECOND);
			int[] times = new int[FTBUConfig.auto_shutdown.times.length];

			for (int i = 0; i < times.length; i++)
			{
				String[] s = FTBUConfig.auto_shutdown.times[i].split(":", 2);

				times[i] = Integer.parseInt(s[0]) * 3600 + Integer.parseInt(s[1]) * 60;

				if (times[i] <= currentTime)
				{
					times[i] += 24 * 3600;
				}
			}

			Arrays.sort(times);

			for (int time : times)
			{
				if (time > currentTime)
				{
					FTBUUniverseData.shutdownTime = start + (time - currentTime) * CommonUtils.TICKS_SECOND;
					FTBUFinals.LOGGER.info(FTBULang.TIMER_SHUTDOWN.translate(StringUtils.getTimeStringTicks(FTBUUniverseData.shutdownTime)));
					break;
				}
			}
		}

		ClaimedChunks.get().nextChunkloaderUpdate = start + 20L;
		Badges.LOCAL_BADGES.clear();
	}

	@SubscribeEvent
	public static void onUniverseSaved(UniverseSavedEvent event)
	{
		if (ClaimedChunks.instance != null)
		{
			ClaimedChunks.get().processQueue();
		}

		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("Warps", FTBUUniverseData.WARPS.serializeNBT());

		//TODO: Save chat as json

		event.setData(FTBLibIntegration.FTBU_DATA, nbt);
	}

	@SubscribeEvent
	public static void onUniverseClosed(UniverseClosedEvent event)
	{
		CHUNK_UPGRADES.clear();
		ClaimedChunks.close();
		Badges.BADGE_CACHE.clear();
		Badges.LOCAL_BADGES.clear();
	}
}