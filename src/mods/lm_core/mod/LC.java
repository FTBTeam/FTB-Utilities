package mods.lm_core.mod;
import java.util.logging.*;

import mods.lm_core.LatCore;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.*;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = LCFinals.MODID, name = LCFinals.MODNAME, version = LCFinals.VERSION)
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class LC
{
	public LC() { PlayerID.inst = new PlayerID(); }
	
	@Mod.Instance(LCFinals.MODID)
	public static LC inst;
	
	@SidedProxy(clientSide = LCFinals.SIDE_CLIENT, serverSide = LCFinals.SIDE_SERVER)
	public static LCCommon proxy;
	
	public static Logger logger = Logger.getLogger("LatCore");
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
		logger.setParent(FMLLog.getLogger());
		LatCore.addGuiHandler(this, proxy);
		proxy.preInit();
	}
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent e)
	{
		proxy.init();
	}
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent e)
	{
		proxy.postInit();
		new LC_TooltipHandler();
		GameRegistry.registerPlayerTracker(new LCHandlers());
	}
	
	@Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent e)
	{
		PlayerID.inst.onStarted();
		e.registerServerCommand(new CmdPlayerID());
		e.registerServerCommand(new CmdDebug());
	}
	
	@Mod.EventHandler
	public void serverStopped(FMLServerStoppedEvent e)
	{
		PlayerID.inst.onStopped();
	}
}