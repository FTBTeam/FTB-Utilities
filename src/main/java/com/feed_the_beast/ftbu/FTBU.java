package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbu.api.FTBUtilitiesAPI;
import com.feed_the_beast.ftbu.api_impl.FTBUtilitiesAPI_Impl;
import com.feed_the_beast.ftbu.api_impl.LoadedChunkStorage;
import com.feed_the_beast.ftbu.config.FTBUConfigWebAPI;
import com.feed_the_beast.ftbu.dims.FTBUDimConfig;
import com.feed_the_beast.ftbu.handlers.FTBUPlayerEventHandler;
import com.feed_the_beast.ftbu.handlers.FTBUServerEventHandler;
import com.feed_the_beast.ftbu.handlers.FTBUTeamEventHandler;
import com.feed_the_beast.ftbu.handlers.FTBUWorldEventHandler;
import com.feed_the_beast.ftbu.handlers.sync.SyncBadges;
import com.feed_the_beast.ftbu.net.FTBUNetHandler;
import com.feed_the_beast.ftbu.ranks.Ranks;
import com.feed_the_beast.ftbu.webapi.WebAPI;
import com.feed_the_beast.ftbu.world.backups.Backups;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

@Mod(modid = FTBUFinals.MOD_ID, version = FTBUFinals.MOD_VERSION, name = FTBUFinals.MOD_NAME, dependencies = FTBUFinals.MOD_DEP)
public class FTBU
{
    public static final Logger logger = LogManager.getLogger("FTBUtilities");

    @Mod.Instance(FTBUFinals.MOD_ID)
    public static FTBU inst;

    @SidedProxy(serverSide = "com.feed_the_beast.ftbu.FTBUCommon", clientSide = "com.feed_the_beast.ftbu.client.FTBUClient")
    public static FTBUCommon proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        FTBUtilitiesAPI.setAPI(new FTBUtilitiesAPI_Impl());

        MinecraftForge.EVENT_BUS.register(new FTBUPlayerEventHandler());
        MinecraftForge.EVENT_BUS.register(new FTBUWorldEventHandler());
        MinecraftForge.EVENT_BUS.register(new FTBUTeamEventHandler());
        MinecraftForge.EVENT_BUS.register(new FTBUServerEventHandler());

        FTBUPermissions.init();
        FTBUCapabilities.enable();
        FTBUNetHandler.init();
        Backups.INSTANCE.init();
        FTBUTops.init();
        FTBUNotifications.init();

        //FTBLibAPI.get().getRegistries().syncedData().register(new ResourceLocation(FTBUFinals.MOD_ID, "config"), new SyncConfig());
        FTBLibAPI.get().getRegistries().syncedData().register(new ResourceLocation(FTBUFinals.MOD_ID, "badges"), new SyncBadges());

        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        FTBUDimConfig.load();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit();
        ForgeChunkManager.setForcedChunkLoadingCallback(inst, LoadedChunkStorage.INSTANCE);
    }

    @Mod.EventHandler
    public void serverStarted(FMLServerStartedEvent event)
    {
        Ranks.INSTANCE.generateExampleFiles();

        if(FTBUConfigWebAPI.ENABLED.getBoolean() && FTBUConfigWebAPI.AUTOSTART.getBoolean())
        {
            WebAPI.INST.startAPI();
        }
    }

    @Mod.EventHandler
    public void serverStopped(FMLServerStoppedEvent event)
    {
        WebAPI.INST.stopAPI();
    }

    @NetworkCheckHandler
    public boolean checkNetwork(Map<String, String> map, Side side)
    {
        String s = map.get(FTBUFinals.MOD_ID);
        return s == null || s.equals(FTBUFinals.MOD_VERSION);
    }
}