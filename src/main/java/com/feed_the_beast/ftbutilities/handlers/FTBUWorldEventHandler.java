package com.feed_the_beast.ftbutilities.handlers;

import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftblib.lib.math.ChunkDimPos;
import com.feed_the_beast.ftblib.lib.util.CommonUtils;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftblib.lib.util.text_components.Notification;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.FTBUtilitiesConfig;
import com.feed_the_beast.ftbutilities.FTBUtilitiesLang;
import com.feed_the_beast.ftbutilities.cmd.CmdShutdown;
import com.feed_the_beast.ftbutilities.data.ClaimedChunk;
import com.feed_the_beast.ftbutilities.data.ClaimedChunks;
import com.feed_the_beast.ftbutilities.data.FTBUPlayerData;
import com.feed_the_beast.ftbutilities.data.FTBUUniverseData;
import com.feed_the_beast.ftbutilities.data.backups.Backups;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
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
@Mod.EventBusSubscriber(modid = FTBUtilities.MOD_ID)
public class FTBUWorldEventHandler
{
	private static final ResourceLocation RESTART_TIMER_ID = new ResourceLocation(FTBUtilities.MOD_ID, "restart_timer");

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
		if (!Universe.loaded())
		{
			return;
		}

		Universe universe = Universe.get();

		long now = universe.server.getWorld(0).getTotalWorldTime();

		if (event.phase == TickEvent.Phase.START)
		{
			if (ClaimedChunks.instance != null)
			{
				ClaimedChunks.instance.update(universe.server, now);
			}
		}
		else
		{
			for (EntityPlayerMP player : universe.server.getPlayerList().getPlayers())
			{
				if (!player.capabilities.isCreativeMode && FTBUPlayerData.get(universe.getPlayer(player)).fly)
				{
					boolean fly = player.capabilities.allowFlying;
					player.capabilities.allowFlying = true;

					if (!fly)
					{
						player.sendPlayerAbilities();
					}
				}
			}

			if (FTBUUniverseData.shutdownTime > 0L)
			{
				long t = FTBUUniverseData.shutdownTime - now;

				if (t <= 0)
				{
					CmdShutdown.shutdown(universe.server);
					return;
				}
				else if ((t <= CommonUtils.TICKS_SECOND * 10L && t % CommonUtils.TICKS_SECOND == CommonUtils.TICKS_SECOND - 1L) || t == CommonUtils.TICKS_MINUTE || t == CommonUtils.TICKS_MINUTE * 5L || t == CommonUtils.TICKS_MINUTE * 10L || t == CommonUtils.TICKS_MINUTE * 30L)
				{
					for (EntityPlayerMP player : universe.server.getPlayerList().getPlayers())
					{
						Notification.of(RESTART_TIMER_ID, StringUtils.color(FTBUtilitiesLang.TIMER_SHUTDOWN.textComponent(player, StringUtils.getTimeStringTicks(t)), TextFormatting.LIGHT_PURPLE)).send(universe.server, player);
					}
				}
			}

			Backups.INSTANCE.tick(universe, now);

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
		if (ClaimedChunks.instance != null && event.getWorld().provider.getDimension() != 0)
		{
			ClaimedChunks.instance.markDirty();
		}
	}

	private static boolean isEntityAllowed(Entity entity)
	{
		if (entity instanceof EntityPlayer)
		{
			return true;
		}

		if (FTBUtilitiesConfig.world.safe_spawn && FTBUUniverseData.isInSpawn(entity.getServer(), new ChunkDimPos(entity)))
		{
			if (entity instanceof IMob)
			{
				return false;
			}
			else
			{
				return !(entity instanceof EntityChicken) || entity.getPassengers().isEmpty();
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
		final MinecraftServer server = event.getWorld().getMinecraftServer();

		Function<ChunkDimPos, Boolean> func = pos ->
		{
			if (pos.dim == 0 && FTBUtilitiesConfig.world.safe_spawn && FTBUUniverseData.isInSpawn(server, pos))
			{
				return false;
			}
			else
			{
				if (FTBUtilitiesConfig.world.enable_explosions.isDefault())
				{
					ClaimedChunk chunk = ClaimedChunks.instance == null ? null : ClaimedChunks.instance.getChunk(pos);
					return chunk == null || chunk.hasExplosions();
				}

				return FTBUtilitiesConfig.world.enable_explosions.isTrue();
			}
		};

		for (BlockPos pos : list)
		{
			if (map.computeIfAbsent(new ChunkDimPos(pos, world.provider.getDimension()), func))
			{
				explosion.getAffectedBlockPositions().add(pos);
			}
		}
	}
}