package com.feed_the_beast.ftbutilities;

import com.feed_the_beast.ftblib.lib.config.RankConfigAPI;
import com.feed_the_beast.ftblib.lib.util.CommonUtils;
import com.feed_the_beast.ftblib.lib.util.FileUtils;
import com.feed_the_beast.ftbutilities.data.FTBULoadedChunkManager;
import com.feed_the_beast.ftbutilities.data.Leaderboard;
import com.feed_the_beast.ftbutilities.data.NodeEntry;
import com.feed_the_beast.ftbutilities.events.CustomPermissionPrefixesRegistryEvent;
import com.feed_the_beast.ftbutilities.events.LeaderboardRegistryEvent;
import com.feed_the_beast.ftbutilities.net.FTBUNetHandler;
import com.feed_the_beast.ftbutilities.ranks.FTBUPermissionHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.server.permission.PermissionAPI;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class FTBUtilitiesCommon
{
	public static final Collection<NodeEntry> CUSTOM_PERM_PREFIX_REGISTRY = new HashSet<>();
	public static final Map<ResourceLocation, Leaderboard> LEADERBOARDS = new HashMap<>();

	public void preInit()
	{
		FTBUtilitiesConfig.sync();

		File oldConfig = new File(CommonUtils.folderLocal, "ftbu");

		if (oldConfig.exists())
		{
			FileUtils.delete(oldConfig);
		}

		if (FTBUtilitiesConfig.ranks.enabled)
		{
			PermissionAPI.setPermissionHandler(FTBUPermissionHandler.INSTANCE);
			RankConfigAPI.setHandler(FTBUPermissionHandler.INSTANCE);
		}

		FTBUNetHandler.init();

		if (!ForgeChunkManager.getConfig().hasCategory(FTBUtilities.MOD_ID))
		{
			ForgeChunkManager.getConfig().get(FTBUtilities.MOD_ID, "maximumChunksPerTicket", 1000000).setMinValue(0);
			ForgeChunkManager.getConfig().get(FTBUtilities.MOD_ID, "maximumTicketCount", 1000000).setMinValue(0);
			ForgeChunkManager.getConfig().save();
		}

		ForgeChunkManager.setForcedChunkLoadingCallback(FTBUtilities.INST, FTBULoadedChunkManager.INSTANCE);
		new CustomPermissionPrefixesRegistryEvent(CUSTOM_PERM_PREFIX_REGISTRY::add).post();
	}

	public void init()
	{
	}

	public void postInit()
	{
		new LeaderboardRegistryEvent(leaderboard -> LEADERBOARDS.put(leaderboard.id, leaderboard)).post();
	}
}