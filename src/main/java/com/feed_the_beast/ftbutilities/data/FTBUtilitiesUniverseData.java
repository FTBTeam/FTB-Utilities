package com.feed_the_beast.ftbutilities.data;

import com.feed_the_beast.ftblib.events.universe.UniverseClosedEvent;
import com.feed_the_beast.ftblib.events.universe.UniverseLoadedEvent;
import com.feed_the_beast.ftblib.events.universe.UniverseSavedEvent;
import com.feed_the_beast.ftblib.lib.EventHandler;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftblib.lib.math.ChunkDimPos;
import com.feed_the_beast.ftblib.lib.math.MathUtils;
import com.feed_the_beast.ftblib.lib.util.CommonUtils;
import com.feed_the_beast.ftblib.lib.util.FileUtils;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.FTBUtilitiesConfig;
import com.feed_the_beast.ftbutilities.data.backups.Backups;
import com.feed_the_beast.ftbutilities.ranks.Ranks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.ThreadedFileIOBase;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author LatvianModder
 */
@EventHandler
public class FTBUtilitiesUniverseData
{
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
		Backups.INSTANCE.nextBackup = now + FTBUtilitiesConfig.backups.time();

		if (FTBUtilitiesConfig.auto_shutdown.enabled && FTBUtilitiesConfig.auto_shutdown.times.length > 0 && event.getUniverse().server.isDedicatedServer())
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

			FTBUtilities.LOGGER.info("Server will shut down in " + StringUtils.getTimeString(shutdownTime - now));
		}

		if (ClaimedChunks.isActive())
		{
			ClaimedChunks.instance.nextChunkloaderUpdate = now + 1000L;
		}

		Badges.LOCAL_BADGES.clear();
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
				try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(FileUtils.newFile(new File(CommonUtils.folderMinecraft, "logs/world.log")), true))))
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

		Badges.BADGE_CACHE.clear();
		Badges.LOCAL_BADGES.clear();
	}
}