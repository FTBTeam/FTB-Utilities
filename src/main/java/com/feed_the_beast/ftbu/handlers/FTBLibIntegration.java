package com.feed_the_beast.ftbu.handlers;

import com.feed_the_beast.ftbl.api.EventHandler;
import com.feed_the_beast.ftbl.api.IFTBLibRegistry;
import com.feed_the_beast.ftbl.api.events.ConfigLoadedEvent;
import com.feed_the_beast.ftbl.api.events.FTBLibRegistryEvent;
import com.feed_the_beast.ftbl.api.events.LoadWorldDataEvent;
import com.feed_the_beast.ftbl.api.events.ReloadEvent;
import com.feed_the_beast.ftbl.lib.util.LMUtils;
import com.feed_the_beast.ftbu.FTBU;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.ServerInfoPage;
import com.feed_the_beast.ftbu.api_impl.LoadedChunkStorage;
import com.feed_the_beast.ftbu.config.FTBUConfigBackups;
import com.feed_the_beast.ftbu.config.FTBUConfigCommands;
import com.feed_the_beast.ftbu.config.FTBUConfigGeneral;
import com.feed_the_beast.ftbu.config.FTBUConfigLogin;
import com.feed_the_beast.ftbu.config.FTBUConfigRanks;
import com.feed_the_beast.ftbu.config.FTBUConfigWebAPI;
import com.feed_the_beast.ftbu.config.FTBUConfigWorld;
import com.feed_the_beast.ftbu.config.PropertyChatSubstitute;
import com.feed_the_beast.ftbu.ranks.FTBUPermissionHandler;
import com.feed_the_beast.ftbu.ranks.Ranks;
import com.feed_the_beast.ftbu.world.FTBUPlayerData;
import com.feed_the_beast.ftbu.world.FTBUTeamData;
import com.feed_the_beast.ftbu.world.FTBUUniverseData;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.server.permission.PermissionAPI;

import java.io.File;

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
			LoadedChunkStorage.INSTANCE.checkAll();
		}
		else
		{
			FTBU.PROXY.onReloadedClient();
		}
	}

	@SubscribeEvent
	public static void registerCommon(FTBLibRegistryEvent event)
	{
		IFTBLibRegistry reg = event.getRegistry();
		reg.addOptionalServerMod(FTBUFinals.MOD_ID);
		reg.addConfigFileProvider(FTBUFinals.MOD_ID, () -> new File(LMUtils.folderLocal, "ftbu/config.json"));
		reg.addConfigValueProvider(PropertyChatSubstitute.ID, PropertyChatSubstitute::new);

		FTBUConfigBackups.init(reg);
		FTBUConfigCommands.init(reg);
		FTBUConfigGeneral.init(reg);
		FTBUConfigLogin.init(reg);
		FTBUConfigWebAPI.init(reg);
		FTBUConfigWorld.init(reg);
		FTBUConfigRanks.init(reg);

		reg.addUniverseDataProvider(FTBU_DATA, owner -> new FTBUUniverseData());
		reg.addPlayerDataProvider(FTBU_DATA, FTBUPlayerData::new);
		reg.addTeamDataProvider(FTBU_DATA, owner -> new FTBUTeamData());

		FTBUPermissions.addConfigs(reg);
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