package latmod.core.base;
import latmod.core.*;
import net.minecraft.entity.Entity;
import net.minecraft.util.*;

public class LMMod
{
	public final String modID;
	public final String assets;
	
	public FastList<BlockLM> blocks;
	public FastList<ItemLM> items;
	
	public LMMod(String s)
	{
		modID = s;
		assets = s.toLowerCase() + ":";
		
		blocks = new FastList<BlockLM>();
		items = new FastList<ItemLM>();
	}
	
	public final ResourceLocation getLocation(String s)
	{ return new ResourceLocation(modID, s); }
	
	public final String getBlockName(String s)
	{ return assets + "tile." + s; }
	
	public final String getItemName(String s)
	{ return assets + "item." + s; }
	
	public final String translate(String s, Object... args)
	{ if(args == null || args.length == 0) return StatCollector.translateToLocal(assets + s);
	else return StatCollector.translateToLocalFormatted(assets + s, args); }
	
	public void addItem(ItemLM i)
	{ LatCore.addItem(i, i.itemName, modID); items.add(i); }

	public void addBlock(BlockLM b, Class<? extends ItemBlockLM> c)
	{ LatCore.addBlock(b, c, b.blockName, modID); blocks.add(b); }

	public void addBlock(BlockLM b)
	{ addBlock(b, ItemBlockLM.class); }

	public void addTile(Class<? extends TileLM> c, String s)
	{ LatCore.addTileEntity(c, modID + '.' + s); }
	
	public void addEntity(Class<? extends Entity> c, String s, int id)
	{ LatCore.addEntity(c, s, id, modID); }
	
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
	}
}