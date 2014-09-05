package latmod.core.mod.block;

import latmod.core.mod.*;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import cpw.mods.fml.relauncher.*;

public abstract class BlockLC extends BlockLM
{
	public BlockLC(String s, Material m)
	{ super(s, m); }
	
	public LMMod<?, ?> getMod()
	{ return LC.mod; }
	
	@SideOnly(Side.CLIENT)
	public CreativeTabs getCreativeTabToDisplayOn()
	{ return LC.tab; }
}