package latmod.ftbu.core;
import java.lang.annotation.*;
import java.lang.reflect.Field;

import latmod.ftbu.core.block.IBlockLM;
import latmod.ftbu.core.item.IItemLM;
import latmod.ftbu.core.recipes.LMRecipes;
import latmod.ftbu.core.tile.TileLM;
import latmod.ftbu.core.util.*;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.*;
import net.minecraft.util.*;

import org.apache.logging.log4j.*;

import cpw.mods.fml.common.*;
import cpw.mods.fml.relauncher.*;

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
	
	public static void init(Object o, LMConfig c, LMRecipes r)
	{
		LMMod mod = getLMMod(o);
		if(mod == null) { LatCoreMC.logger.warn("LMMod failed to load from " + o); return; }
		mod.setConfig(c);
		mod.setRecipes(r);
		modsMap.put(mod.modID, mod);
		if(LatCoreMC.isDevEnv) LatCoreMC.logger.info("LMMod '" + mod.toString() + "' loaded");
	}
	
	// End of static //
	
	public final String modID;
	public final ModContainer modContainer;
	public final String assets;
	public final FastList<IBlockLM> blocks;
	public final FastList<IItemLM> items;
	
	public Logger logger;
	public LMRecipes recipes;
	public LMConfig config;
	
	public LMMod(String id)
	{
		modID = id;
		modContainer = Loader.instance().getIndexedModList().get(modID);
		assets = modID.toLowerCase() + ":";
		blocks = new FastList<IBlockLM>();
		items = new FastList<IItemLM>();
		
		logger = LogManager.getLogger(modID);
		recipes = new LMRecipes();
		config = null;
	}
	
	public void setRecipes(LMRecipes r)
	{ recipes = (r == null) ? new LMRecipes() : r; }
	
	public void setConfig(LMConfig c)
	{
		config = c;
		
		if(config != null)
		{
			config.setMod(this);
			
			if(config instanceof IServerConfig)
				IServerConfig.Registry.add((IServerConfig)config);
		}
	}
	
	public String toFullString()
	{ return modID + '-' + LatCoreMC.MC_VERSION + '-' + modContainer.getDisplayVersion(); }
	
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
	
	public String translate(String s, Object... args)
	{ if(args == null || args.length == 0) return StatCollector.translateToLocal(assets + s);
	else return StatCollector.translateToLocalFormatted(assets + s, args); }
	
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