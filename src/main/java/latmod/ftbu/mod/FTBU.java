package latmod.ftbu.mod;
import java.io.File;
import java.lang.reflect.Method;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.*;
import latmod.core.util.*;
import latmod.ftbu.api.ServerConfigRegistry;
import latmod.ftbu.api.readme.ReadmeSaveHandler;
import latmod.ftbu.backups.Backups;
import latmod.ftbu.inv.ODItems;
import latmod.ftbu.mod.cmd.*;
import latmod.ftbu.mod.config.FTBUConfig;
import latmod.ftbu.net.LMNetHelper;
import latmod.ftbu.util.*;
import latmod.ftbu.world.LMWorldServer;
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
		if(FTBUFinals.DEV)
			LatCoreMC.logger.info("Loading " + FTBUFinals.MOD_NAME + ", Dev Build");
		else
			LatCoreMC.logger.info("Loading " + FTBUFinals.MOD_NAME + ", Build #" + FTBUFinals.MOD_VERSION);
		
		LatCoreMC.logger.info("OS: " + OS.get());
		
		LatCoreMC.configFolder = e.getModConfigurationDirectory();
		LatCoreMC.latmodFolder = new File(LatCoreMC.configFolder.getParentFile(), "latmod/");
		if(!LatCoreMC.latmodFolder.exists()) LatCoreMC.latmodFolder.mkdirs();
		
		LMMod.init(this, null, null);
		mod.logger = LatCoreMC.logger;
		EventBusHelper.register(new FTBUEventHandler());
		EventBusHelper.register(new FTBUTickHandler());
		LMJsonUtils.updateGson();
		ServerConfigRegistry.add(FTBUConfig.instance);
		FTBUConfig.instance.load();
		
		ODItems.preInit();
		Backups.init();
		
		mod.onPostLoaded();
		proxy.preInit();
	}
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent e)
	{
		LMNetHelper.init();
		FMLInterModComms.sendMessage("Waila", "register", "latmod.ftbu.core.api.RegisterWailaEvent.registerHandlers");
	}
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent e)
	{
		ODItems.postInit();
		mod.loadRecipes();
		proxy.postInit();
		
		Thread readmeThread = new Thread("LM_Save_Readme")
		{
			public void run()
			{
				try { ReadmeSaveHandler.saveReadme(); }
				catch(Exception ex) { ex.printStackTrace(); }
			}
		};
		
		readmeThread.start();
	}
	
	@Mod.EventHandler
	public void registerCommands(FMLServerStartingEvent e)
	{
		LMJsonUtils.updateGson();
		FTBUTickHandler.serverStarted();
		e.registerServerCommand(new CmdAdmin());
		e.registerServerCommand(new CmdBack());
		e.registerServerCommand(new CmdFTBU());
		e.registerServerCommand(new CmdMotd());
		e.registerServerCommand(new CmdRules());
		e.registerServerCommand(new CmdSpawn());
		e.registerServerCommand(new CmdTplast());
		e.registerServerCommand(new CmdWarp());
		e.registerServerCommand(new CmdListOverride());
	}
	
	@Mod.EventHandler
	public void serverStopping(FMLServerStoppingEvent e)
	{
		if(LatCoreMC.hasOnlinePlayers())
		{
			for(EntityPlayerMP ep : LatCoreMC.getAllOnlinePlayers(null))
				FTBUEventHandler.playerLoggedOut(ep);
		}
	}
	
	@Mod.EventHandler
	public void serverStopped(FMLServerStoppedEvent e)
	{
		FTBUTickHandler.serverStopped();
		LMWorldServer.inst = null;
	}
	
	@Mod.EventHandler
	public void onIMC(FMLInterModComms.IMCEvent e)
	{
		for(FMLInterModComms.IMCMessage m : e.getMessages())
		{
			String s = m.getStringValue();
			if(s != null && !s.isEmpty() && s.indexOf(':') != -1)
			{
				try
				{
					String[] s1 = s.split(":");
					if(s1 != null && s1.length == 2)
					{
						Class<?> c = Class.forName(s1[0]);
						Method m1 = c.getDeclaredMethod(s1[1]);
						m1.invoke(null);
						LatCoreMC.logger.info("Loaded IMC registry " + s + " from " + m.getSender());
					}
				}
				catch(Exception ex)
				{ LatCoreMC.logger.info("Failed to load IMC registry " + s + " from " + m.getSender()); }
			}
		}
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