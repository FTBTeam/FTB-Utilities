package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbl.util.EventBusHelper;
import com.feed_the_beast.ftbl.util.FTBLib;
import com.feed_the_beast.ftbl.util.LMMod;
import com.feed_the_beast.ftbu.cmd.CmdBack;
import com.feed_the_beast.ftbu.cmd.CmdDelHome;
import com.feed_the_beast.ftbu.cmd.CmdHome;
import com.feed_the_beast.ftbu.cmd.CmdLMPlayerSettings;
import com.feed_the_beast.ftbu.cmd.CmdSetHome;
import com.feed_the_beast.ftbu.cmd.CmdSpawn;
import com.feed_the_beast.ftbu.cmd.CmdTplast;
import com.feed_the_beast.ftbu.cmd.CmdTrashCan;
import com.feed_the_beast.ftbu.cmd.CmdWarp;
import com.feed_the_beast.ftbu.cmd.admin.CmdAdmin;
import com.feed_the_beast.ftbu.cmd.admin.CmdGetRank;
import com.feed_the_beast.ftbu.cmd.admin.CmdSetRank;
import com.feed_the_beast.ftbu.config.FTBUConfig;
import com.feed_the_beast.ftbu.config.FTBUConfigCmd;
import com.feed_the_beast.ftbu.config.FTBUConfigGeneral;
import com.feed_the_beast.ftbu.handlers.FTBLIntegration;
import com.feed_the_beast.ftbu.handlers.FTBUChatEventHandler;
import com.feed_the_beast.ftbu.handlers.FTBUChunkEventHandler;
import com.feed_the_beast.ftbu.handlers.FTBUForgePlayerEventHandler;
import com.feed_the_beast.ftbu.handlers.FTBUPlayerEventHandler;
import com.feed_the_beast.ftbu.handlers.FTBUWorldEventHandler;
import com.feed_the_beast.ftbu.net.FTBUNetHandler;
import com.feed_the_beast.ftbu.ranks.Ranks;
import com.feed_the_beast.ftbu.world.Backups;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

@Mod(modid = FTBUFinals.MOD_ID, version = FTBUFinals.MOD_VERSION, name = FTBUFinals.MOD_NAME, dependencies = FTBUFinals.MOD_DEP, acceptedMinecraftVersions = "[1.9,1.10)")
public class FTBU
{
    @Mod.Instance(FTBUFinals.MOD_ID)
    public static FTBU inst;
    
    @SidedProxy(serverSide = "com.feed_the_beast.ftbu.FTBUCommon", clientSide = "com.feed_the_beast.ftbu.client.FTBUClient")
    public static FTBUCommon proxy;
    
    @SidedProxy(serverSide = "com.feed_the_beast.ftbu.handlers.FTBLIntegration", clientSide = "com.feed_the_beast.ftbu.handlers.FTBLIntegrationClient")
    public static FTBLIntegration ftbl_int;
    
    public static final Logger logger = LogManager.getLogger("FTBUtilities");
    public static LMMod mod;
    
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e)
    {
        mod = LMMod.create(FTBUFinals.MOD_ID);
        FTBLib.ftbu = ftbl_int;
        FTBUConfig.load();
        
        EventBusHelper.register(new FTBUPlayerEventHandler());
        EventBusHelper.register(new FTBUWorldEventHandler());
        EventBusHelper.register(new FTBUChatEventHandler());
        EventBusHelper.register(new FTBUForgePlayerEventHandler());
        FTBUChunkEventHandler.instance.init();
        FTBUPermissions.init();
        FTBUCapabilities.enable();
        
        FTBUNetHandler.init();
        Backups.init();
        mod.onPostLoaded();
        proxy.preInit();
    }
    
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e)
    {
        mod.loadRecipes();
        proxy.postInit();
        ForgeChunkManager.setForcedChunkLoadingCallback(inst, FTBUChunkEventHandler.instance);
    }
    
    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent e)
    {
        FTBLib.addCommand(e, new CmdAdmin());
        FTBLib.addCommand(e, new CmdTplast());
        FTBLib.addCommand(e, new CmdLMPlayerSettings());
        
        if(FTBUConfigCmd.trash_can.getAsBoolean()) { FTBLib.addCommand(e, new CmdTrashCan()); }
        if(FTBUConfigCmd.back.getAsBoolean()) { FTBLib.addCommand(e, new CmdBack()); }
        if(FTBUConfigCmd.spawn.getAsBoolean()) { FTBLib.addCommand(e, new CmdSpawn()); }
        if(FTBUConfigCmd.warp.getAsBoolean()) { FTBLib.addCommand(e, new CmdWarp()); }
        
        if(FTBUConfigCmd.home.getAsBoolean())
        {
            FTBLib.addCommand(e, new CmdHome());
            FTBLib.addCommand(e, new CmdSetHome());
            FTBLib.addCommand(e, new CmdDelHome());
        }
        
        if(FTBUConfigGeneral.ranks_enabled.getAsBoolean())
        {
            FTBLib.addCommand(e, new CmdGetRank());
            FTBLib.addCommand(e, new CmdSetRank());
        }
    }
    
    @Mod.EventHandler
    public void serverStarted(FMLServerStartedEvent e)
    {
        Ranks.instance().generateExampleFiles();
    }
    
    @NetworkCheckHandler
    public boolean checkNetwork(Map<String, String> m, Side side)
    {
        String s = m.get(FTBUFinals.MOD_ID);
        return s == null || s.equals(FTBUFinals.MOD_VERSION);
    }
}