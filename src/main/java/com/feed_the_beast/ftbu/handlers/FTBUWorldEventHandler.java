package com.feed_the_beast.ftbu.handlers;

import com.feed_the_beast.ftbl.api.EventHandler;
import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.lib.Notification;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbl.lib.util.CommonUtils;
import com.feed_the_beast.ftbl.lib.util.ServerUtils;
import com.feed_the_beast.ftbl.lib.util.StringUtils;
import com.feed_the_beast.ftbu.FTBUConfig;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.api.FTBULang;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunk;
import com.feed_the_beast.ftbu.api_impl.ChunkUpgrades;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunks;
import com.feed_the_beast.ftbu.cmd.CmdShutdown;
import com.feed_the_beast.ftbu.util.FTBUUniverseData;
import com.feed_the_beast.ftbu.util.backups.Backups;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
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
			IClaimedChunk chunk = ClaimedChunks.INSTANCE.getChunk(pos);
			return chunk == null || !chunk.hasUpgrade(ChunkUpgrades.NO_EXPLOSIONS);
		}
	};

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
	public static void onServerTickEvent(TickEvent.ServerTickEvent event)
	{
		if (!FTBLibAPI.API.hasUniverse())
		{
			return;
		}

		MinecraftServer server = ServerUtils.getServer();
		long now = server.getWorld(0).getTotalWorldTime();

		if (event.phase == TickEvent.Phase.START)
		{
			ClaimedChunks.INSTANCE.update(server, now);
		}
		else
		{
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
				Backups.INSTANCE.run(server, server, "");
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

	@SubscribeEvent
	public static void onDimensionUnload(WorldEvent.Unload event)
	{
		if (ClaimedChunks.INSTANCE != null)
		{
			ClaimedChunks.INSTANCE.markDirty();
		}
	}

	private static boolean isEntityAllowed(Entity entity)
	{
		if (entity instanceof EntityPlayer)
		{
			return true;
		}

		if (FTBUConfig.world.safe_spawn && FTBUUniverseData.isInSpawn(new ChunkDimPos(entity)))
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