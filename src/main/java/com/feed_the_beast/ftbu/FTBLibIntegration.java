package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.FTBLibPlugin;
import com.feed_the_beast.ftbl.api.IFTBLibClientRegistry;
import com.feed_the_beast.ftbl.api.IFTBLibPlugin;
import com.feed_the_beast.ftbl.api.IFTBLibRegistry;
import com.feed_the_beast.ftbl.api.events.ConfigLoadedEvent;
import com.feed_the_beast.ftbl.api.events.FTBLibClientRegistryEvent;
import com.feed_the_beast.ftbl.api.events.FTBLibRegistryEvent;
import com.feed_the_beast.ftbl.api.events.LoadWorldDataEvent;
import com.feed_the_beast.ftbl.api.events.ReloadEvent;
import com.feed_the_beast.ftbl.lib.util.LMUtils;
import com.feed_the_beast.ftbu.api_impl.FTBUtilitiesAPI_Impl;
import com.feed_the_beast.ftbu.api_impl.LoadedChunkStorage;
import com.feed_the_beast.ftbu.client.FTBUClientConfig;
import com.feed_the_beast.ftbu.cmd.FTBUCommands;
import com.feed_the_beast.ftbu.config.FTBUConfigBackups;
import com.feed_the_beast.ftbu.config.FTBUConfigCommands;
import com.feed_the_beast.ftbu.config.FTBUConfigGeneral;
import com.feed_the_beast.ftbu.config.FTBUConfigLogin;
import com.feed_the_beast.ftbu.config.FTBUConfigRanks;
import com.feed_the_beast.ftbu.config.FTBUConfigWebAPI;
import com.feed_the_beast.ftbu.config.FTBUConfigWorld;
import com.feed_the_beast.ftbu.config.PropertyChatSubstitute;
import com.feed_the_beast.ftbu.integration.TiCIntegration;
import com.feed_the_beast.ftbu.ranks.Ranks;
import com.feed_the_beast.ftbu.world.FTBUPlayerData;
import com.feed_the_beast.ftbu.world.FTBUTeamData;
import com.feed_the_beast.ftbu.world.FTBUUniverseData;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.server.permission.PermissionAPI;

import java.io.File;

public enum FTBLibIntegration implements IFTBLibPlugin
{
    @FTBLibPlugin
    INSTANCE;

    /**
     * @author LatvianModder
     */
    public static FTBLibAPI API;
    public static final ResourceLocation FTBU_DATA = FTBUFinals.get("data");

    @Override
    public void init(FTBLibAPI api)
    {
        API = api;
        MinecraftForge.EVENT_BUS.register(FTBLibIntegration.class);
        MinecraftForge.EVENT_BUS.register(FTBUCommands.class);

        if(Loader.isModLoaded("tconstruct"))
        {
            MinecraftForge.EVENT_BUS.register(TiCIntegration.class);
        }
    }

    @SubscribeEvent
    public static void onReload(ReloadEvent event)
    {
        if(event.getSide().isServer())
        {
            if(event.getType().command())
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

        FTBUNotifications.init(reg);

        reg.addUniverseDataProvider(FTBU_DATA, owner -> new FTBUUniverseData());
        reg.addPlayerDataProvider(FTBU_DATA, FTBUPlayerData::new);
        reg.addTeamDataProvider(FTBU_DATA, owner -> new FTBUTeamData());

        FTBUPermissions.addConfigs(reg);
    }

    @SubscribeEvent
    public static void configLoaded(ConfigLoadedEvent event)
    {
        if(event.getState() == LoaderState.ModState.PREINITIALIZED && FTBUConfigRanks.ENABLED.getBoolean())
        {
            PermissionAPI.setPermissionHandler(FTBUtilitiesAPI_Impl.INSTANCE);
        }
    }

    @SubscribeEvent
    public static void registerClient(FTBLibClientRegistryEvent event)
    {
        IFTBLibClientRegistry reg = event.getRegistry();
        reg.addClientConfig(FTBUFinals.MOD_ID, "render_badges", FTBUClientConfig.RENDER_BADGES);
        reg.addClientConfig(FTBUFinals.MOD_ID, "journeymap_overlay", FTBUClientConfig.JOURNEYMAP_OVERLAY);
    }

    @SubscribeEvent
    public static void loadWorldData(LoadWorldDataEvent event)
    {
        Ranks.reload();
    }
}