package com.feed_the_beast.ftbutilities;

import com.feed_the_beast.ftblib.lib.OtherMods;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesLoadedChunkManager;
import com.feed_the_beast.ftbutilities.data.Leaderboard;
import com.feed_the_beast.ftbutilities.data.NodeEntry;
import com.feed_the_beast.ftbutilities.events.CustomPermissionPrefixesRegistryEvent;
import com.feed_the_beast.ftbutilities.events.LeaderboardRegistryEvent;
import com.feed_the_beast.ftbutilities.integration.ChiselsAndBitsIntegration;
import com.feed_the_beast.ftbutilities.net.FTBUtilitiesNetHandler;
import com.feed_the_beast.ftbutilities.ranks.FTBUtilitiesPermissionHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.server.permission.PermissionAPI;

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

		if (FTBUtilitiesConfig.ranks.enabled)
		{
			PermissionAPI.setPermissionHandler(FTBUtilitiesPermissionHandler.INSTANCE);
		}

		FTBUtilitiesNetHandler.init();

		if (!ForgeChunkManager.getConfig().hasCategory(FTBUtilities.MOD_ID))
		{
			ForgeChunkManager.getConfig().get(FTBUtilities.MOD_ID, "maximumChunksPerTicket", 1000000).setMinValue(0);
			ForgeChunkManager.getConfig().get(FTBUtilities.MOD_ID, "maximumTicketCount", 1000000).setMinValue(0);
			ForgeChunkManager.getConfig().save();
		}

		ForgeChunkManager.setForcedChunkLoadingCallback(FTBUtilities.INST, FTBUtilitiesLoadedChunkManager.INSTANCE);
		new CustomPermissionPrefixesRegistryEvent(CUSTOM_PERM_PREFIX_REGISTRY::add).post();

		if (Loader.isModLoaded(OtherMods.CHISELS_AND_BITS))
		{
			ChiselsAndBitsIntegration.init();
		}
	}

	public void init()
	{
		new LeaderboardRegistryEvent(leaderboard -> LEADERBOARDS.put(leaderboard.id, leaderboard)).post();
		FTBUtilitiesPermissions.registerPermissions();
	}

	public void postInit()
	{
	}

	public void imc(FMLInterModComms.IMCMessage message)
	{
	}
}