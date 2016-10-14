package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbl.lib.AsmData;
import com.feed_the_beast.ftbu.api_impl.FTBUtilitiesAPI_Impl;
import com.feed_the_beast.ftbu.api_impl.LoadedChunkStorage;
import com.feed_the_beast.ftbu.config.FTBUConfigWebAPI;
import com.feed_the_beast.ftbu.handlers.FTBUPlayerEventHandler;
import com.feed_the_beast.ftbu.handlers.FTBUServerEventHandler;
import com.feed_the_beast.ftbu.handlers.FTBUTeamEventHandler;
import com.feed_the_beast.ftbu.handlers.FTBUWorldEventHandler;
import com.feed_the_beast.ftbu.net.FTBUNetHandler;
import com.feed_the_beast.ftbu.ranks.Ranks;
import com.feed_the_beast.ftbu.webapi.WebAPI;
import com.feed_the_beast.ftbu.world.backups.Backups;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = FTBUFinals.MOD_ID,
        name = FTBUFinals.MOD_ID,
        version = "0.0.0",
        useMetadata = true,
        acceptableRemoteVersions = "*",
        dependencies = "required-after:ftbl"
)
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
        FTBUtilitiesAPI_Impl.INSTANCE.init(new AsmData(event.getAsmData()));

        MinecraftForge.EVENT_BUS.register(new FTBUPlayerEventHandler());
        MinecraftForge.EVENT_BUS.register(new FTBUWorldEventHandler());
        MinecraftForge.EVENT_BUS.register(new FTBUTeamEventHandler());
        MinecraftForge.EVENT_BUS.register(new FTBUServerEventHandler());

        FTBUNetHandler.init();

        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        FTBUPermissions.init();
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
        Backups.INSTANCE.init();
        Ranks.INSTANCE.generateExampleFiles();

        if(FTBUConfigWebAPI.ENABLED.getBoolean() && FTBUConfigWebAPI.AUTOSTART.getBoolean())
        {
            WebAPI.INST.startAPI();
        }
    }
}