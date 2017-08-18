package com.feed_the_beast.ftbu.handlers;

import com.feed_the_beast.ftbl.api.EventHandler;
import com.feed_the_beast.ftbl.api.events.ConfigLoadedEvent;
import com.feed_the_beast.ftbl.api.events.LoadWorldDataEvent;
import com.feed_the_beast.ftbl.api.events.ReloadEvent;
import com.feed_the_beast.ftbl.api.events.registry.RegisterDataProvidersEvent;
import com.feed_the_beast.ftbl.api.events.registry.RegisterOptionalServerModsEvent;
import com.feed_the_beast.ftbu.FTBU;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.ServerInfoPage;
import com.feed_the_beast.ftbu.api_impl.FTBUChunkManager;
import com.feed_the_beast.ftbu.config.FTBUConfigRanks;
import com.feed_the_beast.ftbu.ranks.FTBUPermissionHandler;
import com.feed_the_beast.ftbu.ranks.Ranks;
import com.feed_the_beast.ftbu.world.FTBUPlayerData;
import com.feed_the_beast.ftbu.world.FTBUTeamData;
import com.feed_the_beast.ftbu.world.FTBUUniverseData;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.server.permission.PermissionAPI;

/**
 * @author LatvianModder
 */
@EventHandler
public class FTBLibIntegration
{
	public static final ResourceLocation FTBU_DATA = FTBUFinals.get("data");

	@SubscribeEvent
	public static void onReload(ReloadEvent event)
	{
		if (event.getSide().isServer())
		{
			if (event.getType().command())
			{
				Ranks.reload();
			}

			ServerInfoPage.reloadCachedInfo();
			FTBUUniverseData.reloadServerBadges();
			FTBUChunkManager.INSTANCE.checkAll();
		}
		else
		{
			FTBU.PROXY.onReloadedClient();
		}
	}

	@SubscribeEvent
	public static void registerOptionalServerMod(RegisterOptionalServerModsEvent event)
	{
		event.register(FTBUFinals.MOD_ID);
	}

	@SubscribeEvent
	public static void registerUniverseDataProvider(RegisterDataProvidersEvent.Universe event)
	{
		event.register(FTBU_DATA, owner -> new FTBUUniverseData());
	}

	@SubscribeEvent
	public static void registerPlayerDataProvider(RegisterDataProvidersEvent.Player event)
	{
		event.register(FTBU_DATA, FTBUPlayerData::new);
	}

	@SubscribeEvent
	public static void registerTeamDataProvider(RegisterDataProvidersEvent.Team event)
	{
		event.register(FTBU_DATA, owner -> new FTBUTeamData());
	}

	@SubscribeEvent
	public static void configLoaded(ConfigLoadedEvent event)
	{
		if (event.getState() == LoaderState.ModState.PREINITIALIZED && FTBUConfigRanks.ENABLED.getBoolean())
		{
			PermissionAPI.setPermissionHandler(FTBUPermissionHandler.INSTANCE);
		}
	}

	@SubscribeEvent
	public static void loadWorldData(LoadWorldDataEvent event)
	{
		Ranks.reload();
	}
}