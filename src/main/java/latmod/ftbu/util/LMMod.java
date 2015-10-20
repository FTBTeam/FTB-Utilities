package latmod.ftbu.util;
import java.lang.annotation.*;
import java.lang.reflect.Field;

import org.apache.logging.log4j.*;

import cpw.mods.fml.common.*;
import cpw.mods.fml.relauncher.*;
import ftb.lib.FTBLib;
import ftb.lib.mod.FTBLibFinals;
import latmod.ftbu.api.item.IItemLM;
import latmod.ftbu.block.IBlockLM;
import latmod.ftbu.recipes.LMRecipes;
import latmod.ftbu.tile.TileLM;
import latmod.lib.*;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;

public class LMMod
{
	public static final FastMap<String, LMMod> modsMap = new FastMap<String, LMMod>();
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public static @interface Instance
	{
		public String value();
	}
	
	private static LMMod getLMMod(Object o)
	{
		if(o == null) return null;
		
		try
		{
			Field[] fields = o.getClass().getDeclaredFields();
			
			for(Field f : fields)
			{
				if(f.isAnnotationPresent(LMMod.Instance.class))
				{
					LMMod.Instance m = f.getAnnotation(LMMod.Instance.class);
					
					if(m.value() != null)
					{
						LMMod mod = new LMMod(m.value());
						f.set(o, mod);
						return mod;
					}
				}
			}
		}
		catch(Exception e)
		{ e.printStackTrace(); }
		
		return null;
	}
	
	public static void init(Object o)
	{
		LMMod mod = getLMMod(o);
		if(mod == null) { FTBLib.logger.warn("LMMod failed to load from " + o); return; }
		modsMap.put(mod.modID, mod);
		if(FTBLibFinals.DEV) FTBLib.logger.info("LMMod '" + mod.toString() + "' loaded");
	}
	
	// End of static //
	
	public final String modID;
	public final ModContainer modContainer;
	public final String assets;
	public final FastList<IBlockLM> blocks;
	public final FastList<IItemLM> items;
	
	public Logger logger;
	public LMRecipes recipes;
	
	public LMMod(String id)
	{
		modID = id;
		modContainer = Loader.instance().getIndexedModList().get(modID);
		assets = modID.toLowerCase() + ":";
		blocks = new FastList<IBlockLM>();
		items = new FastList<IItemLM>();
		
		logger = LogManager.getLogger(modID);
		recipes = new LMRecipes();
	}
	
	public void setRecipes(LMRecipes r)
	{ recipes = (r == null) ? new LMRecipes() : r; }
	
	public String toFullString()
	{ return modID + '-' + FTBLibFinals.MC_VERSION + '-' + modContainer.getDisplayVersion(); }
	
	public String toString()
	{ return modID; }
	
	public ResourceLocation getLocation(String s)
	{ return new ResourceLocation(assets + s); }
	
	public CreativeTabs createTab(final String s, final ItemStack icon)
	{
		CreativeTabs tab = new CreativeTabs(assets + s)
		{
			@SideOnly(Side.CLIENT)
			public ItemStack getIconItemStack()
			{ return icon; }
			
			@SideOnly(Side.CLIENT)
			public Item getTabIconItem()
			{ return getIconItemStack().getItem(); }
		};
		
		return tab;
	}
	
	public String getBlockName(String s)
	{ return assets + "tile." + s; }
	
	public String getItemName(String s)
	{ return assets + "item." + s; }
	
	@SideOnly(Side.CLIENT)
	public String translateClient(String s, Object... args)
	{ return I18n.format(assets + s, args); }
	
	public void addItem(IItemLM i)
	{ LatCoreMC.addItem((Item)i, i.getItemID()); items.add(i); }

	public void addBlock(IBlockLM b)
	{ LatCoreMC.addBlock((Block)b, b.getItemBlock(), b.getItemID()); blocks.add(b); }

	public void addTile(Class<? extends TileLM> c, String s, String... alt)
	{ LatCoreMC.addTileEntity(c, modID + '.' + s, alt); }
	
	public void addEntity(Class<? extends Entity> c, String s, int id)
	{ LatCoreMC.addEntity(c, s, id, modID); }
	
	public void onPostLoaded()
	{
		for(int i = 0; i < items.size(); i++)
			items.get(i).onPostLoaded();
		
		for(int i = 0; i < blocks.size(); i++)
			blocks.get(i).onPostLoaded();
	}
	
	public void loadRecipes()
	{
		for(int i = 0; i < items.size(); i++)
			items.get(i).loadRecipes();
		
		for(int i = 0; i < blocks.size(); i++)
			blocks.get(i).loadRecipes();
		
		if(recipes != null) recipes.loadRecipes();
	}
}