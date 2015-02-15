package latmod.core.mod;

import java.util.List;

import latmod.core.*;
import net.minecraft.block.Block;

import com.cricketcraft.chisel.api.carving.*;

public class ChiselHelper
{
	private static Boolean isInstalled = null;
	
	public static final boolean isInstalled()
	{
		if(isInstalled == null)
			isInstalled = LatCoreMC.isModInstalled("chisel");
		return isInstalled.booleanValue();
	}
	
	public static void register(ICarvingGroup g)
	{ if(isInstalled()) CarvingUtils.getChiselRegistry().addGroup(g); }
	
	public static class Group implements ICarvingGroup
	{
		public final Block block;
		public String sound = null;
		public String oreName = null;
		public final FastList<ICarvingVariation> variations = new FastList<ICarvingVariation>();
		
		public Group(Block b)
		{ block = b; }
		
		public void addVariation(Block b, int itemMeta, int blockMeta)
		{ variations.add(new Variation(b, blockMeta, itemMeta, variations.size())); }
		
		public void addVariation(Block b, int meta)
		{ addVariation(b, meta, meta); }
		
		public String getName()
		{ return "Glowium Blocks"; }
		
		public String getSound()
		{ return sound; }
		
		public void setSound(String s)
		{ sound = s; }
		
		public String getOreName()
		{ return oreName; }
		
		public void setOreName(String s)
		{ }
		
		public List<ICarvingVariation> getVariations()
		{ return variations; }
		
		public void addVariation(ICarvingVariation v)
		{  }
		
		public boolean removeVariation(ICarvingVariation v)
		{ return false; }
	}
	
	public static class Variation implements ICarvingVariation
	{
		public final Block block;
		public final int blockMeta;
		public final int itemMeta;
		public final int order;
		
		public Variation(Block b, int bm, int im, int o)
		{
			block = b;
			blockMeta = bm;
			itemMeta = im;
			order = o;
		}
		
		public Block getBlock()
		{ return block; }
		
		public int getBlockMeta()
		{ return blockMeta; }
		
		public int getItemMeta()
		{ return itemMeta; }
		
		public int getOrder()
		{ return order; }
	}
}