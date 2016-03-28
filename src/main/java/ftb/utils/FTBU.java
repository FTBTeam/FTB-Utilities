package ftb.utils;

import ftb.lib.*;
import ftb.lib.api.permissions.ForgePermissionRegistry;
import ftb.utils.cmd.*;
import ftb.utils.cmd.admin.*;
import ftb.utils.config.*;
import ftb.utils.handlers.*;
import ftb.utils.net.FTBUNetHandler;
import ftb.utils.ranks.Ranks;
import ftb.utils.world.Backups;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.*;

import java.util.Map;

@Mod(modid = FTBUFinals.MOD_ID, version = FTBUFinals.MOD_VERSION, name = FTBUFinals.MOD_NAME, dependencies = FTBUFinals.MOD_DEP, acceptedMinecraftVersions = "[1.8.8,1.9)")
public class FTBU
{
	@Mod.Instance(FTBUFinals.MOD_ID)
	public static FTBU inst;
	
	@SidedProxy(clientSide = "ftb.utils.client.FTBUClient", serverSide = "ftb.utils.FTBUCommon")
	public static FTBUCommon proxy;
	
	@SidedProxy(clientSide = "ftb.utils.handlers.FTBLIntegrationClient", serverSide = "ftb.utils.handlers.FTBLIntegration")
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
		FTBUChunkEventHandler.instance.init();
		ForgePermissionRegistry.register(FTBUPermissions.class);
		
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
		
		if(FTBUConfigCmd.trash_can.getAsBoolean()) FTBLib.addCommand(e, new CmdTrashCan());
		if(FTBUConfigCmd.back.getAsBoolean()) FTBLib.addCommand(e, new CmdBack());
		if(FTBUConfigCmd.spawn.getAsBoolean()) FTBLib.addCommand(e, new CmdSpawn());
		if(FTBUConfigCmd.warp.getAsBoolean()) FTBLib.addCommand(e, new CmdWarp());
		
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