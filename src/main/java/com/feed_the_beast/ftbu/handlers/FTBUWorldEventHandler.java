package com.feed_the_beast.ftbu.handlers;

import com.feed_the_beast.ftbl.api.EventHandler;
import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.events.universe.ForgeUniverseClosedEvent;
import com.feed_the_beast.ftbl.api.events.universe.ForgeUniverseLoadedEvent;
import com.feed_the_beast.ftbl.api.events.universe.ForgeUniverseSavedEvent;
import com.feed_the_beast.ftbl.lib.Notification;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbl.lib.util.CommonUtils;
import com.feed_the_beast.ftbl.lib.util.ServerUtils;
import com.feed_the_beast.ftbl.lib.util.StringUtils;
import com.feed_the_beast.ftbu.FTBUConfig;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.api.FTBULang;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunk;
import com.feed_the_beast.ftbu.api_impl.ChunkUpgrade;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunk;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunkStorage;
import com.feed_the_beast.ftbu.api_impl.FTBUChunkManager;
import com.feed_the_beast.ftbu.cmd.CmdShutdown;
import com.feed_the_beast.ftbu.util.Badges;
import com.feed_the_beast.ftbu.util.FTBUUniverseData;
import com.feed_the_beast.ftbu.util.backups.Backups;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author LatvianModder
 */
@EventHandler
public class FTBUWorldEventHandler
{
	private static final ResourceLocation RESTART_TIMER_ID = FTBUFinals.get("restart_timer");
	public static final Function<ChunkDimPos, Boolean> ALLOW_EXPLOSION = pos ->
	{
		if (pos.dim == 0 && FTBUConfig.world.safe_spawn && FTBUUniverseData.isInSpawn(pos))
		{
			return false;
		}
		else
		{
			IClaimedChunk chunk = ClaimedChunkStorage.INSTANCE.getChunk(pos);
			return chunk == null || !chunk.hasUpgrade(ChunkUpgrade.NO_EXPLOSIONS);
		}
	};

	@SubscribeEvent
	public static void onUniverseLoaded(ForgeUniverseLoadedEvent.Finished event)
	{
		ClaimedChunkStorage.INSTANCE.init();

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

		FTBUUniverseData.nextChunkloaderUpdate = start + 20L;
		Badges.LOCAL_BADGES.clear();
	}

	@SubscribeEvent
	public static void onUniversePreLoaded(ForgeUniverseLoadedEvent.Pre event)
	{
		ClaimedChunkStorage.INSTANCE.clear();
	}

	@SubscribeEvent
	public static void onUniversePostLoaded(ForgeUniverseLoadedEvent.Post event)
	{
		NBTTagCompound nbt = event.getData(FTBLibIntegration.FTBU_DATA);

		FTBUUniverseData.WARPS.deserializeNBT(nbt.getCompoundTag("Warps"));

		if (nbt.hasKey("Chunks", Constants.NBT.TAG_COMPOUND))
		{
			NBTTagCompound nbt1 = nbt.getCompoundTag("Chunks");

			for (String s : nbt1.getKeySet())
			{
				IForgePlayer player = FTBLibAPI.API.getUniverse().getPlayer(StringUtils.fromString(s));

				if (player != null)
				{
					NBTTagList list = nbt1.getTagList(s, Constants.NBT.TAG_INT_ARRAY);

					for (int i = 0; i < list.tagCount(); i++)
					{
						int[] ai = list.getIntArrayAt(i);

						if (ai.length >= 3)
						{
							ClaimedChunk chunk = new ClaimedChunk(new ChunkDimPos(ai[1], ai[2], ai[0]), player, ai.length >= 4 ? ai[3] : 0);
							ClaimedChunkStorage.INSTANCE.setChunk(chunk.getPos(), chunk);
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onUniverseSaved(ForgeUniverseSavedEvent event)
	{
		NBTTagCompound nbt = new NBTTagCompound();

		if (FTBUUniverseData.WARPS.isEmpty())
		{
			nbt.setTag("Warps", FTBUUniverseData.WARPS.serializeNBT());
		}

		event.setData(FTBLibIntegration.FTBU_DATA, nbt);
	}

	@SubscribeEvent
	public static void onUniverseClosed(ForgeUniverseClosedEvent event)
	{
		ClaimedChunkStorage.INSTANCE.clear();
		FTBUChunkManager.INSTANCE.clear();
		Badges.BADGE_CACHE.clear();
		Badges.LOCAL_BADGES.clear();
	}

	@SubscribeEvent
	public static void onMobSpawned(EntityJoinWorldEvent event)
	{
		if (!event.getWorld().isRemote && !isEntityAllowed(event.getEntity()))
		{
			event.getEntity().setDead();
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onTickEvent(TickEvent.WorldTickEvent event)
	{
		if (event.phase == TickEvent.Phase.END && !event.world.isRemote)
		{
			long now = event.world.getTotalWorldTime();

			if (FTBUUniverseData.shutdownTime > 0L)
			{
				long t = FTBUUniverseData.shutdownTime - now;

				if (t <= 0)
				{
					CmdShutdown.shutdown(ServerUtils.getServer());
					return;
				}
				else if ((t == CommonUtils.TICKS_SECOND * 10L && t % CommonUtils.TICKS_SECOND == 0L) || t == CommonUtils.TICKS_MINUTE || t == CommonUtils.TICKS_MINUTE * 5L || t == CommonUtils.TICKS_MINUTE * 10L || t == CommonUtils.TICKS_MINUTE * 30L)
				{
					Notification.of(RESTART_TIMER_ID, StringUtils.color(FTBULang.TIMER_SHUTDOWN.textComponent(StringUtils.getTimeStringTicks(t / CommonUtils.TICKS_SECOND)), TextFormatting.LIGHT_PURPLE)).send(null);
				}
			}

			if (Backups.INSTANCE.nextBackup > 0L && Backups.INSTANCE.nextBackup <= now)
			{
				MinecraftServer server = ServerUtils.getServer();
				Backups.INSTANCE.run(server, server, "");
			}

			if (FTBUUniverseData.nextChunkloaderUpdate <= now)
			{
				FTBUUniverseData.nextChunkloaderUpdate = now + CommonUtils.TICKS_MINUTE;
				FTBUChunkManager.INSTANCE.checkAll();
			}

			if (Backups.INSTANCE.thread != null && Backups.INSTANCE.thread.isDone)
			{
				Backups.INSTANCE.thread = null;
				Backups.INSTANCE.postBackup();
			}

        	/*
			for(int i = teleportRequests.size() - 1; i >= 0; i--)
        	{
        	    if(teleportRequests.get(i).isExpired(now))
        	    {
        	        teleportRequests.remove(i);
        	    }
        	}
        	*/
		}
	}

	private static boolean isEntityAllowed(Entity entity)
	{
		if (entity instanceof EntityPlayer)
		{
			return true;
		}

		if (FTBUConfig.world.safe_spawn && FTBUUniverseData.isInSpawnD(entity.dimension, entity.posX, entity.posZ))
		{
			if (entity instanceof IMob)
			{
				return false;
			}
			else if (entity instanceof EntityChicken && !entity.getPassengers().isEmpty())
			{
				return false;
			}
		}

		return true;
	}

	@SubscribeEvent
	public static void onExplosionDetonate(ExplosionEvent.Detonate event)
	{
		World world = event.getWorld();
		Explosion explosion = event.getExplosion();

		if (world.isRemote || explosion.getAffectedBlockPositions().isEmpty())
		{
			return;
		}

		List<BlockPos> list = new ArrayList<>(explosion.getAffectedBlockPositions());
		explosion.clearAffectedBlockPositions();
		Map<ChunkDimPos, Boolean> map = new HashMap<>();

		for (BlockPos pos : list)
		{
			if (map.computeIfAbsent(new ChunkDimPos(pos, world.provider.getDimension()), ALLOW_EXPLOSION))
			{
				explosion.getAffectedBlockPositions().add(pos);
			}
		}
	}
}