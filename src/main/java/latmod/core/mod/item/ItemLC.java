package latmod.core.mod.item;

import latmod.core.mod.*;
import net.minecraft.creativetab.CreativeTabs;
import cpw.mods.fml.relauncher.*;

public class ItemLC extends ItemLM
{
	public ItemLC(String s)
	{ super(s); }
	
	public LMMod getMod()
	{ return LC.mod; }
	
	@SideOnly(Side.CLIENT)
	public CreativeTabs getCreativeTab()
	{ return LC.tab; }
}