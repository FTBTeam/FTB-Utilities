package latmod.core.mod.block;

import latmod.core.mod.LC;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import cpw.mods.fml.relauncher.*;

public abstract class BlockLC extends BlockLM
{
	public BlockLC(String s, Material m)
	{
		super(LC.mod, s, m);
	}

	@SideOnly(Side.CLIENT)
	public CreativeTabs getCreativeTabToDisplayOn()
	{ return LC.tab; }
}