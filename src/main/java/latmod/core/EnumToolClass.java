package latmod.core;

import net.minecraft.block.Block;

public enum EnumToolClass
{
	PICKAXE("pickaxe"),
	SHOVEL("shovel"),
	AXE("axe");
	
	public final String toolClass;
	
	EnumToolClass(String s)
	{ toolClass = s; }
	
	public void setHarvestLevel(Block b, int lvl, int m)
	{ b.setHarvestLevel(toolClass, lvl, m); }
}