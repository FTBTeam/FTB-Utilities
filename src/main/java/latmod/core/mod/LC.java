package latmod.core.mod;
import java.util.UUID;

import latmod.core.*;
import latmod.core.mod.item.ItemLinkCard;
import latmod.core.mod.net.LMNetHandler;
import latmod.core.mod.recipes.LMRecipes;
import latmod.core.util.*;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.*;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.*;

@Mod(modid = LC.MODID, name = "LatCoreMC", version = LC.MODVERSION) //dependencies = "required-after:Waila"
public class LC
{
	protected static final String MODID = "LatCoreMC";
	protected static final String MODVERSION = "1.3.3";
	
	@Mod.Instance(LC.MODID)
	public static LC inst;
	
	@SidedProxy(clientSide = "latmod.core.mod.LCClient", serverSide = "latmod.core.mod.LCCommon")
	public static LCCommon proxy;
	
	public static LMMod mod;
	public static CreativeTabs tab;
	public static LMRecipes recipes;
	public static LCConfig config;
	public static Logger logger = LogManager.getLogger("LatCoreMC");
	
	public static FastMap<String, String> latmodVersions;
	public static FastMap<String, String> versionsToCheck;
	public static boolean hasDisplayedUpdates = false;
	
	public static FastList<String> teamLatModNames;
	public static FastList<UUID> teamLatModUUIDs;
	
	public LC()
	{
		LCEventHandler e = new LCEventHandler();
		MinecraftForge.EVENT_BUS.register(e);
		FMLCommonHandler.instance().bus().register(e);
	}
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
		mod = new LMMod(MODID);
		ODItems.preInit();
		recipes = new LMRecipes(false);
		config = new LCConfig(e);
		
		mod.addItem(LCItems.i_link_card = new ItemLinkCard("linkCard"));
		
		mod.onPostLoaded();
		
		tab = LatCore.createTab(mod.assets + "tab", new ItemStack(LCItems.i_link_card));
		
		LatCore.addGuiHandler(this, proxy);
		
		latmodVersions = new FastMap<String, String>();
		versionsToCheck = new FastMap<String, String>();
		
		teamLatModNames = new FastList<String>();
		teamLatModUUIDs = new FastList<UUID>();
		
		if(config.general.checkUpdates)
			ThreadCheckVersions.init();
		
		if(config.general.checkTeamLatMod)
			ThreadCheckTeamLatMod.init();
		
		proxy.preInit();
		config.save();
	}
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent e)
	{
		versionsToCheck.put(MODID, MODVERSION);
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
	
	public static boolean isUpdated(String modID, String version)
	{
		String s = latmodVersions.get(modID);
		if(s == null || s.length() == 0) return true;
		
		return false;
	}
}