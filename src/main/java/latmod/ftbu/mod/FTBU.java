package latmod.ftbu.mod;
import java.io.File;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.*;
import latmod.ftbu.api.*;
import latmod.ftbu.api.guide.*;
import latmod.ftbu.backups.Backups;
import latmod.ftbu.inv.ODItems;
import latmod.ftbu.mod.cmd.*;
import latmod.ftbu.mod.config.FTBUConfig;
import latmod.ftbu.mod.handlers.*;
import latmod.ftbu.net.LMNetHelper;
import latmod.ftbu.util.*;
import latmod.ftbu.world.LMWorldServer;
import latmod.lib.*;
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
		
		LatCoreMC.logger.info("OS: " + OS.current + ", 64bit: " + OS.is64);
		
		LatCoreMC.configFolder = e.getModConfigurationDirectory();
		LatCoreMC.localConfigFolder = new File(LatCoreMC.configFolder.getParentFile(), "config_local/");
		if(!LatCoreMC.localConfigFolder.exists()) LatCoreMC.localConfigFolder.mkdirs();
		LMMod.init(this);
		mod.logger = LatCoreMC.logger;
		JsonHelper.init();
		EventBusHelper.register(new FTBUPlayerEventHandler());
		EventBusHelper.register(new FTBUWorldEventHandler());
		EventBusHelper.register(new FTBUChatEventHandler());
		FTBUConfig.load();
		LMNetHelper.init();
		ODItems.preInit();
		Backups.init();
		mod.onPostLoaded();
		proxy.preInit();
		
		new EventFTBUInit(Phase.PRE).post();
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
		
		new EventFTBUInit(Phase.POST).post();
		
		//TODO: Replace with GuiGuide
		try
		{
			GuideFile file = new GuideFile();
			FTBUConfig.saveReadme(file);
			proxy.onReadmeEvent(file);
			
			new EventFTBUGuide(file).post();
			
			StringBuilder sb = new StringBuilder();
			
			for(int j = 0; j < file.categories.size(); j++)
			{
				GuideCategory c = file.categories.get(j);
				
				sb.append('[');
				sb.append(' ');
				sb.append(c.title);
				sb.append(' ');
				sb.append(']');
				sb.append('\n');
				
				for(int i = 0; i < c.text.size(); i++)
				{
					sb.append(c.text.get(i).toString());
					sb.append('\n');
				}
				
				sb.append('\n');
			}
			
			LMFileUtils.save(new File(LatCoreMC.localConfigFolder, "readme.txt"), sb.toString().trim());
		}
		catch(Exception ex)
		{ ex.printStackTrace(); }
	}
	
	@Mod.EventHandler
	public void registerCommands(FMLServerStartingEvent e)
	{
		FTBUTicks.serverStarted();
		e.registerServerCommand(new CmdAdmin());
		e.registerServerCommand(new CmdBack());
		e.registerServerCommand(new CmdFTBU());
		e.registerServerCommand(new CmdMotd());
		e.registerServerCommand(new CmdRules());
		e.registerServerCommand(new CmdSpawn());
		e.registerServerCommand(new CmdTplast());
		e.registerServerCommand(new CmdWarp());
		e.registerServerCommand(new CmdListOverride());
		e.registerServerCommand(new CmdMath());
	}
	
	@Mod.EventHandler
	public void serverStopping(FMLServerStoppingEvent e)
	{
		if(LatCoreMC.hasOnlinePlayers())
		{
			for(EntityPlayerMP ep : LatCoreMC.getAllOnlinePlayers(null))
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