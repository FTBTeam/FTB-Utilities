package latmod.ftbu.mod;
import java.io.File;

import latmod.ftbu.core.*;
import latmod.ftbu.core.net.MessageLM;
import latmod.ftbu.mod.cmd.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumChatFormatting;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.*;

@Mod
(
		modid = FTBUFinals.MOD_ID,
		version = FTBUFinals.VERSION,
		name = FTBUFinals.MOD_NAME,
		dependencies = FTBUFinals.DEPENDENCIES,
		guiFactory = FTBUFinals.GUI_FACTORY
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
		LatCoreMC.addEventHandler(FTBUEventHandler.instance, true, true, true);
		LatCoreMC.addEventHandler(FTBUTickHandler.instance, true, true, false);
	}
	
	private ModMetadata modMeta;
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
		if(LatCoreMC.isDevEnv)
			LatCoreMC.logger.info("Loading FTBUtilities, Dev Build");
		else
			LatCoreMC.logger.info("Loading FTBUtilities, Build #" + FTBUFinals.VERSION);
		
		modMeta = e.getModMetadata();
		
		LatCoreMC.configFolder = e.getModConfigurationDirectory();
		LatCoreMC.latmodFolder = new File(LatCoreMC.configFolder.getParentFile(), "latmod/");
		if(!LatCoreMC.latmodFolder.exists()) LatCoreMC.latmodFolder.mkdirs();
		
		LMMod.init(this, null, null);
		mod.logger = LatCoreMC.logger;
		IServerConfig.Registry.add(FTBUConfig.instance);
		FTBUConfig.instance.load();
		
		FTBULang.reload();
		ODItems.preInit();
		
		mod.onPostLoaded();
		proxy.preInit(e);
	}
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent e)
	{
		MessageLM.init();
		proxy.init(e);
		
		FMLInterModComms.sendMessage("Waila", "register", "latmod.ftbu.core.event.RegisterWailaEvent.registerHandlers");
	}
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent e)
	{
		ODItems.postInit();
		mod.loadRecipes();
		proxy.postInit(e);
		
		if(modMeta != null && LMMod.modsMap.values.size() >= 2)
		{
			modMeta.description += EnumChatFormatting.GREEN + "\n\nMods using FTBUtilities:";
			
			for(LMMod m : LMMod.modsMap.values)
			{ if(m != mod) modMeta.description += "\n" + m.modID; }
		}
		
		for(String s : FTBUGuiHandler.IDs) LatCoreMC.addLMGuiHandler(s, FTBUGuiHandler.instance);
		
		Thread readmeThread = new Thread("LM_Readme")
		{
			public void run()
			{ try { FTBUConfig.saveReadme(); } catch(Exception ex) { ex.printStackTrace(); } }
		};
		
		readmeThread.start();
	}
	
	@Mod.EventHandler
	public void registerCommands(FMLServerStartingEvent e)
	{
		FTBUTickHandler.resetTimer(true);
		e.registerServerCommand(new CmdAdmin());
		e.registerServerCommand(new CmdBack());
		e.registerServerCommand(new CmdFTBU());
		e.registerServerCommand(new CmdMotd());
		e.registerServerCommand(new CmdRestartTimer());
		e.registerServerCommand(new CmdRules());
		e.registerServerCommand(new CmdSpawn());
		e.registerServerCommand(new CmdTplast());
		e.registerServerCommand(new CmdWarp());
		//ClientCommandHandler.instance.registerCommand(new CmdWaypoints());
	}
	
	@Mod.EventHandler
	public void serverStopping(FMLServerStoppingEvent e)
	{
		if(LatCoreMC.hasOnlinePlayers()) for(EntityPlayerMP ep : LatCoreMC.getAllOnlinePlayers().values)
			FTBUEventHandler.instance.playerLoggedOut(new cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent(ep));
	}
	
	@Mod.EventHandler
	public void serverStopped(FMLServerStoppedEvent e)
	{
		FTBUTickHandler.resetTimer(false);
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