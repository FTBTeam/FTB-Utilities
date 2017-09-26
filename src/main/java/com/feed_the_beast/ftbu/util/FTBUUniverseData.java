package com.feed_the_beast.ftbu.util;

import com.feed_the_beast.ftbl.api.EventHandler;
import com.feed_the_beast.ftbl.api.universe.ForgeUniverseClosedEvent;
import com.feed_the_beast.ftbl.api.universe.ForgeUniverseLoadedEvent;
import com.feed_the_beast.ftbl.api.universe.ForgeUniverseSavedEvent;
import com.feed_the_beast.ftbl.lib.ChatHistory;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbl.lib.math.MathUtils;
import com.feed_the_beast.ftbl.lib.util.CommonUtils;
import com.feed_the_beast.ftbl.lib.util.ServerUtils;
import com.feed_the_beast.ftbl.lib.util.StringUtils;
import com.feed_the_beast.ftbu.FTBUConfig;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.api.FTBULang;
import com.feed_the_beast.ftbu.api.chunks.ChunkUpgrade;
import com.feed_the_beast.ftbu.api.chunks.RegisterChunkUpgradesEvent;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunks;
import com.feed_the_beast.ftbu.handlers.FTBLibIntegration;
import com.feed_the_beast.ftbu.util.backups.Backups;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author LatvianModder
 */
@EventHandler
public class FTBUUniverseData
{
	public static long shutdownTime;
	public static final BlockDimPosStorage WARPS = new BlockDimPosStorage();
	public static final ChatHistory GENERAL_CHAT = new ChatHistory(() -> FTBUConfig.chat.general_history_limit);
	public static final Map<String, ChunkUpgrade> CHUNK_UPGRADES = new HashMap<>();
	public static final Map<Integer, ChunkUpgrade> ID_TO_UPGRADE = new HashMap<>();
	public static final Map<ChunkUpgrade, Integer> UPGRADE_TO_ID = new HashMap<>();

	public static final BiConsumer<ChunkUpgrade, Integer> SET_UPGRADE_ID = (upgrade, id) ->
	{
		UPGRADE_TO_ID.put(upgrade, id);
		ID_TO_UPGRADE.put(id, upgrade);
	};

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

	public static int getUpgradeId(ChunkUpgrade upgrade)
	{
		Integer id = UPGRADE_TO_ID.get(upgrade);

		if (id == null)
		{
			id = 0;

			for (Integer id0 : UPGRADE_TO_ID.values())
			{
				id = Math.max(id, id0);
			}

			id++;
			SET_UPGRADE_ID.accept(upgrade, id);
		}

		return id;
	}

	@Nullable
	public static ChunkUpgrade getUpgradeFromId(int id)
	{
		return id == 0 ? null : ID_TO_UPGRADE.get(id);
	}

	private static void registerChunkUpgrade(ChunkUpgrade upgrade)
	{
		CHUNK_UPGRADES.put(upgrade.getName(), upgrade);
	}

	@SubscribeEvent
	public static void onUniversePreLoaded(ForgeUniverseLoadedEvent.Pre event)
	{
		new RegisterChunkUpgradesEvent(FTBUUniverseData::registerChunkUpgrade).post();
	}

	@SubscribeEvent
	public static void onUniversePostLoaded(ForgeUniverseLoadedEvent.Post event)
	{
		NBTTagCompound nbt = event.getData(FTBLibIntegration.FTBU_DATA);
		FTBUUniverseData.WARPS.deserializeNBT(nbt.getCompoundTag("Warps"));

		ID_TO_UPGRADE.clear();
		UPGRADE_TO_ID.clear();

		NBTTagCompound upgrades = nbt.getCompoundTag("ChunkUpgrades");

		for (String name : upgrades.getKeySet())
		{
			int id = upgrades.getInteger(name);
			ChunkUpgrade upgrade = CHUNK_UPGRADES.get(name);

			if (upgrade != null)
			{
				ID_TO_UPGRADE.put(id, upgrade);
				UPGRADE_TO_ID.put(upgrade, id);
			}
		}

		for (ChunkUpgrade upgrade : CHUNK_UPGRADES.values())
		{
			getUpgradeId(upgrade);
		}
	}

	@SubscribeEvent
	public static void onUniverseLoaded(ForgeUniverseLoadedEvent.Finished event)
	{
		long start = event.getUniverse().getOverworld().getTotalWorldTime();
		Backups.INSTANCE.nextBackup = start + FTBUConfig.backups.ticks();

		if (FTBUConfig.auto_shutdown.enabled && FTBUConfig.auto_shutdown.times.length > 0 && event.getUniverse().getServer().isDedicatedServer())
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

		ClaimedChunks.INSTANCE.nextChunkloaderUpdate = start + 20L;
		Badges.LOCAL_BADGES.clear();
	}

	@SubscribeEvent
	public static void onUniverseSaved(ForgeUniverseSavedEvent event)
	{
		ClaimedChunks.INSTANCE.processQueue();

		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("Warps", FTBUUniverseData.WARPS.serializeNBT());

		NBTTagCompound upgrades = new NBTTagCompound();

		for (Map.Entry<ChunkUpgrade, Integer> entry : UPGRADE_TO_ID.entrySet())
		{
			upgrades.setInteger(entry.getKey().getName(), entry.getValue());
		}

		nbt.setTag("ChunkUpgrades", upgrades);

		//TODO: Save chat as json

		event.setData(FTBLibIntegration.FTBU_DATA, nbt);
	}

	@SubscribeEvent
	public static void onUniverseClosed(ForgeUniverseClosedEvent event)
	{
		CHUNK_UPGRADES.clear();
		ClaimedChunks.INSTANCE.clear();
		Badges.BADGE_CACHE.clear();
		Badges.LOCAL_BADGES.clear();
	}
}