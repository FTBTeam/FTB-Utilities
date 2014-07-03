package latmod.core.base;
import java.util.List;

import latmod.core.FastList;
import cpw.mods.fml.relauncher.*;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;

public class ItemBlockLM extends ItemBlock
{
	public ItemBlockLM(int b)
	{
		super(b);
		setHasSubtypes(true);
		setMaxDamage(0);
	}

	public int getMetadata(int m)
	{ return m; }
	
	public BlockLM getBlock()
	{ return (BlockLM)Block.blocksList[getBlockID()]; }

	public String getUnlocalizedName(ItemStack is)
	{ return getBlock().getUnlocalizedName(is.getItemDamage()); }
	
	@SuppressWarnings("all")
	@SideOnly(Side.CLIENT)
	public void getSubItems(int j, CreativeTabs c, List l)
	{ getBlock().getSubBlocks(j, c, l); }
	
	private FastList<String> infoList = new FastList<String>();
	
	@SuppressWarnings("all") @SideOnly(Side.CLIENT)
    public final void addInformation(ItemStack is, EntityPlayer ep, List l, boolean b)
	{
		infoList.clear();
		getBlock().addInfo(is, ep, infoList);
		l.addAll(infoList);
	}
}