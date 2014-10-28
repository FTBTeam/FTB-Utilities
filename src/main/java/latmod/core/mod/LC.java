package latmod.core.mod;
import latmod.core.*;
import latmod.core.apis.WailaHelper;
import latmod.core.mod.cmd.CommandBaseLC;
import latmod.core.mod.net.LMNetHandler;
import latmod.core.mod.recipes.LMRecipes;
import latmod.core.mod.tile.*;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.*;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.*;

@Mod(modid = LC.MOD_ID, name = "LatCoreMC", version = LC.VERSION, dependencies = "required-after:Forge@[10.13.0.1207,)")
public class LC
{
	protected static final String MOD_ID = "LatCoreMC";
	public static final String VERSION = "@VERSION@";
	
	@Mod.Instance(LC.MOD_ID)
	public static LC inst;
	
	@SidedProxy(clientSide = "latmod.core.mod.LCClient", serverSide = "latmod.core.mod.LCCommon")
	public static LCCommon proxy;
	
	public static LMMod<LCConfig, LMRecipes> mod;
	public static Logger logger = LogManager.getLogger("LatCoreMC");
	
	public LC()
	{
		LCEventHandler e = new LCEventHandler();
		MinecraftForge.EVENT_BUS.register(e);
		FMLCommonHandler.instance().bus().register(e);
	}
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
		if(LatCoreMC.isDevEnv)
			logger.info("Loading LatCoreMC, Dev Build");
		else
			logger.info("Loading LatCoreMC, Build #" + VERSION);
		
		mod = new LMMod<LCConfig, LMRecipes>(MOD_ID, new LCConfig(e), new LMRecipes(false));
		ODItems.preInit();
		
		mod.onPostLoaded();
		
		LatCoreMC.addGuiHandler(this, proxy);
		
		if(mod.config().general.checkTeamLatMod)
			ThreadCheckTeamLatMod.init();
		
		proxy.preInit();
	}
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent e)
	{
		LMNetHandler.init();
		proxy.init();
	}
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent e)
	{
		mod.loadRecipes();
		
		proxy.postInit();
		
		try
		{ WailaHelper.registerDataProvider(IWailaTile.class, new WailaLMTile()); }
		catch(Exception ex) { ex.printStackTrace(); }
	}
	
	@Mod.EventHandler
	public void registerCommands(FMLServerStartingEvent e)
	{ CommandBaseLC.registerCommands(e); }
}