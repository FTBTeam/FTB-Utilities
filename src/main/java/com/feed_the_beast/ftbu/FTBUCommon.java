package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftblib.lib.util.CommonUtils;
import com.feed_the_beast.ftblib.lib.util.FileUtils;
import com.feed_the_beast.ftbu.data.ClaimedChunks;
import com.feed_the_beast.ftbu.data.Leaderboard;
import com.feed_the_beast.ftbu.data.NodeEntry;
import com.feed_the_beast.ftbu.events.RegisterCustomPermissionPrefixesEvent;
import com.feed_the_beast.ftbu.events.chunks.RegisterChunkUpgradesEvent;
import com.feed_the_beast.ftbu.net.FTBUNetHandler;
import com.feed_the_beast.ftbu.ranks.FTBUPermissionHandler;
import com.feed_the_beast.ftbu.util.FTBUUniverseData;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.server.permission.PermissionAPI;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class FTBUCommon implements ForgeChunkManager.LoadingCallback
{
	public static final Collection<NodeEntry> CUSTOM_PERM_PREFIX_REGISTRY = new HashSet<>();
	public static IForgeRegistry<Leaderboard> LEADERBOARDS;

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

		new RegisterCustomPermissionPrefixesEvent(CUSTOM_PERM_PREFIX_REGISTRY::add).post();
	}

	public void init()
	{
	}

	public void postInit()
	{
		new RegisterChunkUpgradesEvent(upgrade -> FTBUUniverseData.CHUNK_UPGRADES.put(upgrade.getName(), upgrade)).post();
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