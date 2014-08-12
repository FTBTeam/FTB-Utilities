package latmod.core.mod.item;

import latmod.core.mod.LC;
import net.minecraft.creativetab.CreativeTabs;
import cpw.mods.fml.relauncher.*;

public class ItemLC extends ItemLM
{
	public ItemLC(String s)
	{
		super(LC.mod, s);
	}
	
	@SideOnly(Side.CLIENT)
	public CreativeTabs getCreativeTab()
	{ return LC.tab; }
}