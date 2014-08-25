package latmod.core.mod;
import latmod.core.*;
import latmod.core.mod.net.LMNetHandler;
import latmod.core.mod.recipes.LMRecipes;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.*;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.*;

@Mod(modid = LC.MOD_ID, name = "LatCoreMC", version = LC.MOD_VERSION) //dependencies = "required-after:Waila"
public class LC
{
	public static final String MOD_ID = "LatCoreMC";
	public static final String MOD_VERSION = "@VERSION@";
	
	@Mod.Instance(LC.MOD_ID)
	public static LC inst;
	
	@SidedProxy(clientSide = "latmod.core.mod.LCClient", serverSide = "latmod.core.mod.LCCommon")
	public static LCCommon proxy;
	
	public static LMMod mod;
	public static CreativeTabs tab;
	public static LMRecipes recipes;
	public static LCConfig config;
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
		logger.info("Loading LatCoreMC, Build #" + MOD_VERSION);
		
		mod = new LMMod(MOD_ID);
		ODItems.preInit();
		recipes = new LMRecipes(false);
		config = new LCConfig(e);
		
		LCItems.init(mod);
		mod.onPostLoaded();
		
		tab = LatCoreMC.createTab(mod.assets + "tab", new ItemStack(LCItems.i_link_card));
		
		LatCoreMC.addGuiHandler(this, proxy);
		
		if(config.general.checkTeamLatMod)
			ThreadCheckTeamLatMod.init();
		
		proxy.preInit();
		config.save();
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
	}
	
	@Mod.EventHandler()
	public void registerCommands(FMLServerStartingEvent e)
	{ e.registerServerCommand(new LCCommand()); }
}