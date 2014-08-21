package latmod.core.mod;
import latmod.core.*;
import latmod.core.mod.block.BlockLM;
import latmod.core.mod.item.IItemLM;
import latmod.core.mod.item.block.ItemBlockLM;
import latmod.core.mod.tile.TileLM;
import latmod.core.util.*;
import net.minecraft.entity.Entity;
import net.minecraft.util.*;

public class LMMod
{
	public final String modID;
	public final String assets;
	
	public FastList<BlockLM> blocks;
	public FastList<IItemLM> items;
	
	public LMMod(String s)
	{
		modID = s;
		
		assets = s.toLowerCase() + ":";
		
		blocks = new FastList<BlockLM>();
		items = new FastList<IItemLM>();
	}
	
	public final String getBlockName(String s)
	{ return assets + "tile." + s; }
	
	public final String getItemName(String s)
	{ return assets + "item." + s; }
	
	public final String translate(String s, Object... args)
	{ if(args == null || args.length == 0) return StatCollector.translateToLocal(assets + s);
	else return StatCollector.translateToLocalFormatted(assets + s, args); }
	
	public void addItem(IItemLM i)
	{ LatCoreMC.addItem(i.getItem(), i.getItemID()); items.add(i); }

	public void addBlock(BlockLM b, Class<? extends ItemBlockLM> c)
	{ LatCoreMC.addBlock(b, c, b.blockName); blocks.add(b); }

	public void addBlock(BlockLM b)
	{ addBlock(b, ItemBlockLM.class); }

	public void addTile(Class<? extends TileLM> c, String s)
	{ LatCoreMC.addTileEntity(c, modID + '.' + s); }
	
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
	}
}