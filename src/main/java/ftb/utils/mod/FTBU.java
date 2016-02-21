package ftb.utils.mod;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.relauncher.*;
import ftb.lib.*;
import ftb.lib.api.permission.ForgePermissionRegistry;
import ftb.utils.mod.cmd.*;
import ftb.utils.mod.cmd.admin.*;
import ftb.utils.mod.config.*;
import ftb.utils.mod.handlers.*;
import ftb.utils.mod.handlers.ftbl.FTBLIntegration;
import ftb.utils.net.FTBUNetHandler;
import ftb.utils.ranks.*;
import ftb.utils.world.Backups;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import org.apache.logging.log4j.*;

import java.util.*;

@Mod(modid = FTBUFinals.MOD_ID, version = FTBUFinals.MOD_VERSION, name = FTBUFinals.MOD_NAME, dependencies = FTBUFinals.MOD_DEP, acceptedMinecraftVersions = "1.7.10")
public class FTBU
{
	@Mod.Instance(FTBUFinals.MOD_ID)
	public static FTBU inst;
	
	@SidedProxy(clientSide = "ftb.utils.mod.client.FTBUClient", serverSide = "ftb.utils.mod.FTBUCommon")
	public static FTBUCommon proxy;
	
	@SidedProxy(clientSide = "ftb.utils.mod.handlers.ftbl.FTBLIntegrationClient", serverSide = "ftb.utils.mod.handlers.ftbl.FTBLIntegration")
	public static FTBLIntegration ftbl_int;
	
	public static LMMod mod;
	
	public static Logger logger;
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
		mod = LMMod.create(FTBUFinals.MOD_ID);
		FTBLib.ftbu = ftbl_int;
		logger = LogManager.getLogger(FTBUFinals.MOD_ID);
		
		FTBUConfig.load();
		
		EventBusHelper.register(new FTBUPlayerEventHandler());
		EventBusHelper.register(new FTBUWorldEventHandler());
		EventBusHelper.register(new FTBUChatEventHandler());
		FTBUChunkEventHandler.instance.init();
		
		FTBUNetHandler.init();
		Backups.init();
		proxy.preInit();
		
		ForgePermissionRegistry.register(FTBUPermissions.class);
		ForgePermissionRegistry.setHandler(Ranks.instance());
	}
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent e)
	{
		proxy.postInit();
	}
	
	@Mod.EventHandler
	public void registerCommands(FMLServerStartingEvent e)
	{
		FTBLib.addCommand(e, new CmdAdmin());
		FTBLib.addCommand(e, new CmdTplast());
		FTBLib.addCommand(e, new CmdLMPlayerSettings());
		
		if(FTBUConfigCmd.back.get()) FTBLib.addCommand(e, new CmdBack());
		if(FTBUConfigCmd.spawn.get()) FTBLib.addCommand(e, new CmdSpawn());
		if(FTBUConfigCmd.warp.get()) FTBLib.addCommand(e, new CmdWarp());
		if(FTBUConfigCmd.player_spawnpoint.get()) FTBLib.addCommand(e, new CmdSpawnpointOverride());
		
		if(FTBUConfigCmd.home.get())
		{
			FTBLib.addCommand(e, new CmdHome());
			FTBLib.addCommand(e, new CmdSetHome());
			FTBLib.addCommand(e, new CmdDelHome());
		}
		
		if(FTBUConfigGeneral.ranks_enabled.get())
		{
			FTBLib.addCommand(e, new CmdGetRank());
			FTBLib.addCommand(e, new CmdSetRank());
		}
	}
	
	@Mod.EventHandler
	public void serverStarted(FMLServerStartedEvent e)
	{
		Ranks.instance().generateExampleFiles();
		
		if(FTBUConfigGeneral.ranks_enabled.get() && FTBUConfigGeneral.ranks_override_commands.get())
		{
			ICommandManager icm = FTBLib.getServer().getCommandManager();
			
			if(icm != null && icm instanceof CommandHandler)
			{
				try
				{
					CommandHandler ch = (CommandHandler) icm;
					
					Map map = ReflectionHelper.getPrivateValue(CommandHandler.class, ch, "commandMap", "field_71562_a");
					Set set = ReflectionHelper.getPrivateValue(CommandHandler.class, ch, "commandSet", "field_71561_b");
					
					List<CmdOverride> commands = new ArrayList<>();
					
					for(Object o : map.values())
						commands.add(new CmdOverride((ICommand) o));
					
					map.clear();
					set.clear();
					
					ReflectionHelper.setPrivateValue(CommandHandler.class, ch, map, "commandMap", "field_71562_a");
					ReflectionHelper.setPrivateValue(CommandHandler.class, ch, set, "commandSet", "field_71561_b");
					
					for(CmdOverride c : commands)
						ch.registerCommand(c);
					
					logger.info("Loaded " + commands.size() + " command overrides");
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}
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
	
	@NetworkCheckHandler
	public boolean checkNetwork(Map<String, String> m, Side side)
	{
		String s = m.get(FTBUFinals.MOD_ID);
		return s == null || s.equals(FTBUFinals.MOD_VERSION);
	}
}