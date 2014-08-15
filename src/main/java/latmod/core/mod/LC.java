package latmod.core.mod;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

import latmod.core.*;
import latmod.core.mod.item.ItemLinkCard;
import latmod.core.mod.net.LMNetHandler;
import latmod.core.mod.recipes.LMRecipes;
import latmod.core.util.FastList;
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
	protected static final String MODVERSION = "1.3.2";
	
	@Mod.Instance(LC.MODID)
	public static LC inst;
	
	@SidedProxy(clientSide = "latmod.core.mod.LCClient", serverSide = "latmod.core.mod.LCCommon")
	public static LCCommon proxy;
	
	public static LMMod mod;
	public static CreativeTabs tab;
	public static LMRecipes recipes;
	public static Logger logger = LogManager.getLogger("LatCoreMC");
	
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
		
		mod.addItem(LCItems.i_link_card = new ItemLinkCard("linkCard"));
		
		mod.onPostLoaded();
		
		tab = LatCore.createTab(mod.assets + "tab", new ItemStack(LCItems.i_link_card));
		
		LatCore.addGuiHandler(this, proxy);
		
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
		
		teamLatModNames = new FastList<String>();
		teamLatModUUIDs = new FastList<UUID>();
		
		LC.logger.info("Loading TeamLatMod.json...");
		
		try
		{
			InputStream is = new URL("http://pastebin.com/raw.php?i=ihHF9uta").openStream();
			byte[] b = new byte[is.available()];
			is.read(b);
			String s = new String(b);
			
			if(s.length() > 0 && s.startsWith("[") && s.endsWith("]"))
			{
				List<String> list = LMUtils.fromJson(s, LMUtils.getListType(String.class));
				
				if(list.size() >= 2 && list.size() % 2 == 0)
				for(int i = 0; i < list.size(); i += 2)
				{
					teamLatModNames.add(list.get(i));
					teamLatModUUIDs.add(UUID.fromString(list.get(i + 1)));
				}
				else logger.info("Invalid LatMod Team file");
				
				logger.info("LatMod Team loaded");
			}
			else logger.info("LatMod Team failed to load");
		}
		catch(Exception ex)
		{ ex.printStackTrace(); }
		
		proxy.postInit();
	}
	
	@Mod.EventHandler()
	public void registerCommands(FMLServerStartingEvent e)
	{ e.registerServerCommand(new LCCommand()); }
}