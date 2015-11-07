package latmod.ftbu.mod;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.*;
import ftb.lib.*;
import ftb.lib.item.ODItems;
import latmod.ftbu.mod.cmd.*;
import latmod.ftbu.mod.config.*;
import latmod.ftbu.mod.handlers.*;
import latmod.ftbu.net.FTBUNetHandler;
import latmod.ftbu.notification.*;
import latmod.ftbu.util.LMMod;
import latmod.ftbu.world.*;
import latmod.lib.LMJsonUtils;
import net.minecraft.entity.player.EntityPlayerMP;

@Mod
(
		modid = FTBUFinals.MOD_ID,
		version = FTBUFinals.MOD_VERSION,
		name = FTBUFinals.MOD_NAME,
		dependencies = FTBUFinals.MOD_DEP
)
public class FTBU
{
	@Mod.Instance(FTBUFinals.MOD_ID)
	public static FTBU inst;
	
	@SidedProxy(clientSide = "latmod.ftbu.mod.client.FTBUClient", serverSide = "latmod.ftbu.mod.FTBUCommon")
	public static FTBUCommon proxy;
	
	@LMMod.Instance(FTBUFinals.MOD_ID)
	public static LMMod mod;
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
		LMMod.init(this);
		
		LMJsonUtils.register(Notification.class, new Notification.Serializer());
		LMJsonUtils.register(MouseAction.class, new MouseAction.Serializer());
		
		EventBusHelper.register(new FTBULibEventHandler());
		EventBusHelper.register(new FTBUPlayerEventHandler());
		EventBusHelper.register(new FTBUWorldEventHandler());
		EventBusHelper.register(new FTBUChatEventHandler());
		FTBUConfig.load();
		FTBUNetHandler.init();
		ODItems.preInit();
		Backups.init();
		mod.onPostLoaded();
		proxy.preInit();
	}
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent e)
	{
		FMLInterModComms.sendMessage("Waila", "register", "latmod.ftbu.core.api.RegisterWailaEvent.registerHandlers");
	}
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent e)
	{
		ODItems.postInit();
		mod.loadRecipes();
		proxy.postInit();
	}
	
	@Mod.EventHandler
	public void registerCommands(FMLServerStartingEvent e)
	{
		FTBUTicks.serverStarted();
		e.registerServerCommand(new CmdAdmin());
		e.registerServerCommand(new CmdListOverride());
		if(FTBUConfigCmd.back.get()) e.registerServerCommand(new CmdBack());
		if(FTBUConfigCmd.spawn.get()) e.registerServerCommand(new CmdSpawn());
		if(FTBUConfigCmd.tplast.get()) e.registerServerCommand(new CmdTplast());
		if(FTBUConfigCmd.warp.get()) e.registerServerCommand(new CmdWarp());
		if(FTBUConfigCmd.home.get()) e.registerServerCommand(new CmdHome());
	}
	
	@Mod.EventHandler
	public void serverStopping(FMLServerStoppingEvent e)
	{
		if(FTBLib.hasOnlinePlayers())
		{
			for(EntityPlayerMP ep : FTBLib.getAllOnlinePlayers(null))
				FTBUPlayerEventHandler.playerLoggedOut(ep);
		}
	}
	
	@Mod.EventHandler
	public void serverStopped(FMLServerStoppedEvent e)
	{
		FTBUTicks.serverStopped();
		LMWorldServer.inst = null;
	}
	
	/*
	@NetworkCheckHandler
	public boolean checkNetwork(Map<String, String> m, Side side)
	{
		String s = m.get(MOD_ID);
		return s == null || s.equals(VERSION) || VERSION.equals(LatCoreMC.DEV_VERSION);
	}
	*/
}