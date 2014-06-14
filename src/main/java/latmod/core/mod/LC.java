package latmod.core.mod;
import net.minecraft.tileentity.TileEntity;
import latmod.core.*;
import latmod.core.base.*;
import latmod.core.tile.*;
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
	
	public static LMMod finals;
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
		finals = new LCFinals();
		
		LatCore.addGuiHandler(inst, proxy);
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
		OreHelper.load();
		new LC_TooltipHandler();
	}
}