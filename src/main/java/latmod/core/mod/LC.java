package latmod.core.mod;
import net.minecraftforge.common.MinecraftForge;
import latmod.core.ODItems;
import latmod.core.base.*;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.*;

@Mod(modid = LC.MODID, name = LC.MODNAME, version = LC.MODVERSION)
public class LC
{
	protected static final String MODID = "LatCore";
	protected static final String MODNAME = "LatCore";
	protected static final String MODVERSION = "1.3.1";
	
	public static final String getModID()
	{ return MODID; }
	
	@Mod.Instance(LC.MODID)
	public static LC inst;
	
	@SidedProxy(clientSide = "latmod.core.mod.LCClient", serverSide = "latmod.core.mod.LCCommon")
	public static LCCommon proxy;
	
	public static LMMod mod;
	
	public LC()
	{
		MinecraftForge.EVENT_BUS.register(new LCEventHandler());	
	}
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
		mod = new LMMod(MODID);
		ODItems.preInit();
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
	}
}