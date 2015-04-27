package latmod.core;
import latmod.core.block.IBlockLM;
import latmod.core.item.IItemLM;
import latmod.core.recipes.LMRecipes;
import latmod.core.tile.TileLM;
import latmod.core.util.*;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.*;
import net.minecraft.util.*;

import org.apache.logging.log4j.*;

import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.*;

public final class LMMod
{
	public static final FastMap<String, LMMod> modsMap = new FastMap<String, LMMod>();
	
	public final ModMetadata modMetadata;
	public final String modID;
	public final String displayName;
	public final String version;
	public final String assets;
	
	public final FastList<IBlockLM> blocks;
	public final FastList<IItemLM> items;
	
	public Logger logger;
	public LMRecipes recipes;
	public LMConfig config;
	
	public LMMod(FMLPreInitializationEvent e, LMConfig c, LMRecipes r)
	{
		modMetadata = e.getModMetadata();
		
		modID = modMetadata.modId;
		displayName = modMetadata.name;
		version = modMetadata.version;
		assets = modID.toLowerCase() + ":";
		
		blocks = new FastList<IBlockLM>();
		items = new FastList<IItemLM>();
		
		logger = LogManager.getLogger(modID);
		
		if(LatCoreMC.isDevEnv) logger.info("LMMod '" + displayName + "' v" + version + " created");
		
		config = c; if(config != null)
		{
			config.setMod(this);
			
			if(config instanceof IServerConfig)
				IServerConfig.Registry.add((IServerConfig)config);
		}
		
		recipes = (r == null) ? new LMRecipes() : r;
		
		modsMap.put(modID, this);
	}
	
	public String toFullString()
	{ return modID + '-' + LatCoreMC.MC_VERSION + '-' + version; }
	
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