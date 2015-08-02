package latmod.ftbu.mod;
import java.io.File;
import java.lang.reflect.Method;

import latmod.ftbu.core.*;
import latmod.ftbu.core.event.FTBUReadmeEvent;
import latmod.ftbu.core.inv.ODItems;
import latmod.ftbu.core.net.LMNetHelper;
import latmod.ftbu.core.util.LatCore;
import latmod.ftbu.core.world.LMWorldServer;
import latmod.ftbu.mod.backups.Backups;
import latmod.ftbu.mod.cmd.*;
import latmod.ftbu.mod.config.FTBUConfig;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumChatFormatting;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.*;

@Mod
(
		modid = FTBUFinals.MOD_ID,
		version = FTBUFinals.VERSION,
		name = FTBUFinals.MOD_NAME,
		dependencies = FTBUFinals.DEPENDENCIES
)
public class FTBU
{
	@Mod.Instance(FTBUFinals.MOD_ID)
	public static FTBU inst;
	
	@SidedProxy(clientSide = "latmod.ftbu.mod.client.FTBUClient", serverSide = "latmod.ftbu.mod.FTBUCommon")
	public static FTBUCommon proxy;
	
	@LMMod.Instance(FTBUFinals.MOD_ID)
	public static LMMod mod;
	
	public FTBU()
	{
		LatCoreMC.BusType.FORGE.register(FTBUEventHandler.instance);
		LatCoreMC.BusType.FML.register(FTBUEventHandler.instance);
		LatCoreMC.BusType.LATMOD.register(FTBUEventHandler.instance);
		LatCoreMC.BusType.FORGE.register(FTBUTickHandler.instance);
		LatCoreMC.BusType.FML.register(FTBUTickHandler.instance);
	}
	
	private ModMetadata modMeta;
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
		if(LatCoreMC.isDevEnv)
			LatCoreMC.logger.info("Loading FTBUtilities, Dev Build");
		else
			LatCoreMC.logger.info("Loading FTBUtilities, Build #" + FTBUFinals.VERSION);
		
		LatCoreMC.logger.info("OS: " + LatCore.OS.get());
		
		modMeta = e.getModMetadata();
		
		LatCoreMC.configFolder = e.getModConfigurationDirectory();
		LatCoreMC.latmodFolder = new File(LatCoreMC.configFolder.getParentFile(), "latmod/");
		if(!LatCoreMC.latmodFolder.exists()) LatCoreMC.latmodFolder.mkdirs();
		
		LMMod.init(this, null, null);
		mod.logger = LatCoreMC.logger;
		LatCore.updateGson();
		IServerConfig.Registry.add(FTBUConfig.instance);
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
		FMLInterModComms.sendMessage("Waila", "register", "latmod.ftbu.core.event.RegisterWailaEvent.registerHandlers");
	}
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent e)
	{
		ODItems.postInit();
		mod.loadRecipes();
		proxy.postInit();
		
		if(modMeta != null && LMMod.modsMap.values.size() >= 2)
		{
			modMeta.description += EnumChatFormatting.GREEN + "\n\nMods using FTBUtilities:";
			
			for(LMMod m : LMMod.modsMap.values)
			{ if(m != mod) modMeta.description += "\n" + m.modID; }
		}
		
		Thread readmeThread = new Thread("LM_Readme")
		{
			public void run()
			{ try { FTBUReadmeEvent.saveReadme(); } catch(Exception ex) { ex.printStackTrace(); } }
		};
		
		readmeThread.start();
	}
	
	@Mod.EventHandler
	public void registerCommands(FMLServerStartingEvent e)
	{
		LatCore.updateGson();
		FTBUTickHandler.resetTimer(true);
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
		if(LatCoreMC.hasOnlinePlayers()) for(EntityPlayerMP ep : LatCoreMC.getAllOnlinePlayers())
			FTBUEventHandler.instance.playerLoggedOut(new cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent(ep));
		
		/*if(FTBUConfig.backups.backupOnShutdown)
		{
			Backups.shouldRun = true;
			Backups.run();
		}*/
	}
	
	@Mod.EventHandler
	public void serverStopped(FMLServerStoppedEvent e)
	{
		FTBUTickHandler.resetTimer(false);
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