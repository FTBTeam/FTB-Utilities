package com.feed_the_beast.ftbutilities;

import com.feed_the_beast.ftblib.lib.util.CommonUtils;
import com.feed_the_beast.ftblib.lib.util.FileUtils;
import com.feed_the_beast.ftbutilities.data.ClaimedChunks;
import com.feed_the_beast.ftbutilities.data.Leaderboard;
import com.feed_the_beast.ftbutilities.data.NodeEntry;
import com.feed_the_beast.ftbutilities.events.CustomPermissionPrefixesRegistryEvent;
import com.feed_the_beast.ftbutilities.events.LeaderboardRegistryEvent;
import com.feed_the_beast.ftbutilities.events.chunks.ChunkUpgradeRegisteryEvent;
import com.feed_the_beast.ftbutilities.net.FTBUNetHandler;
import com.feed_the_beast.ftbutilities.ranks.FTBUPermissionHandler;
import com.feed_the_beast.ftbutilities.util.FTBUUniverseData;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.server.permission.PermissionAPI;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class FTBUCommon implements ForgeChunkManager.LoadingCallback
{
	public static final Collection<NodeEntry> CUSTOM_PERM_PREFIX_REGISTRY = new HashSet<>();
	public static final Map<ResourceLocation, Leaderboard> LEADERBOARDS = new HashMap<>();

	public void preInit()
	{
		FTBUConfig.sync();

		File oldConfig = new File(CommonUtils.folderLocal, "ftbu");

		if (oldConfig.exists())
		{
			FileUtils.delete(oldConfig);
		}

		if (FTBUConfig.ranks.enabled)
		{
			PermissionAPI.setPermissionHandler(FTBUPermissionHandler.INSTANCE);
		}

		FTBUNetHandler.init();

		if (!ForgeChunkManager.getConfig().hasCategory(FTBUFinals.MOD_ID))
		{
			ForgeChunkManager.getConfig().get(FTBUFinals.MOD_ID, "maximumChunksPerTicket", 1000000).setMinValue(0);
			ForgeChunkManager.getConfig().get(FTBUFinals.MOD_ID, "maximumTicketCount", 1000000).setMinValue(0);
			ForgeChunkManager.getConfig().save();
		}

		ForgeChunkManager.setForcedChunkLoadingCallback(FTBU.INST, this);

		new CustomPermissionPrefixesRegistryEvent(CUSTOM_PERM_PREFIX_REGISTRY::add).post();
	}

	public void init()
	{
	}

	public void postInit()
	{
		new ChunkUpgradeRegisteryEvent(upgrade -> FTBUUniverseData.CHUNK_UPGRADES.put(upgrade.getName(), upgrade)).post();
		new LeaderboardRegistryEvent(leaderboard -> LEADERBOARDS.put(leaderboard.id, leaderboard)).post();
	}

	@Override
	public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world)
	{
		for (ForgeChunkManager.Ticket ticket : tickets)
		{
			ClaimedChunks.TicketKey key = new ClaimedChunks.TicketKey(world.provider.getDimension(), ticket.getModData().getString("Team"));

			if (!key.teamId.isEmpty())
			{
				ForgeChunkManager.releaseTicket(ticket);
				ForgeChunkManager.Ticket removed = ClaimedChunks.get().ticketMap.remove(key);

				if (removed != null && removed != ticket)
				{
					ForgeChunkManager.releaseTicket(removed);
				}
			}
		}
	}
}