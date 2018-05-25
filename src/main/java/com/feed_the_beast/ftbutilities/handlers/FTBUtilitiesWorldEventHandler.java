package com.feed_the_beast.ftbutilities.handlers;

import com.feed_the_beast.ftblib.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.FTBUtilitiesConfig;
import com.feed_the_beast.ftbutilities.data.ClaimedChunk;
import com.feed_the_beast.ftbutilities.data.ClaimedChunks;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesUniverseData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = FTBUtilities.MOD_ID)
public class FTBUtilitiesWorldEventHandler
{
	@SubscribeEvent
	public static void onMobSpawned(EntityJoinWorldEvent event) //FIXME: LivingSpawnEvent.CheckSpawn
	{
		if (!event.getWorld().isRemote && !isEntityAllowed(event.getEntity()))
		{
			event.getEntity().setDead();
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onDimensionUnload(WorldEvent.Unload event)
	{
		if (ClaimedChunks.isActive() && event.getWorld().provider.getDimension() != 0)
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

		if (FTBUtilitiesConfig.world.safe_spawn && FTBUtilitiesUniverseData.isInSpawn(entity.getServer(), new ChunkDimPos(entity)))
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
			if (pos.dim == 0 && FTBUtilitiesConfig.world.safe_spawn && FTBUtilitiesUniverseData.isInSpawn(server, pos))
			{
				return false;
			}
			else
			{
				if (FTBUtilitiesConfig.world.enable_explosions.isDefault())
				{
					ClaimedChunk chunk = ClaimedChunks.isActive() ? ClaimedChunks.instance.getChunk(pos) : null;
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