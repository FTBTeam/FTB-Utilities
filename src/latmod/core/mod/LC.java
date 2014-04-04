package latmod.core.mod;
import latmod.core.LatCore;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.*;

@Mod(modid = LCFinals.MODID, name = LCFinals.MODNAME, version = LCFinals.VERSION)
public class LC
{
	@Mod.Instance(LCFinals.MODID)
	public static LC inst;
	
	@SidedProxy(clientSide = LCFinals.SIDE_CLIENT, serverSide = LCFinals.SIDE_SERVER)
	public static LCCommon proxy;
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
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
	}
	
	@Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent e)
	{
		//e.registerServerCommand(new CmdDebug());
	}
}