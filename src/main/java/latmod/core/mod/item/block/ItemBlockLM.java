package latmod.core.mod.item.block;
import java.util.List;

import latmod.core.mod.block.BlockLM;
import latmod.core.util.FastList;
import cpw.mods.fml.relauncher.*;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;

public class ItemBlockLM extends ItemBlock
{
	public BlockLM blockLM;
	
	public ItemBlockLM(Block b)
	{
		super(b);
		setHasSubtypes(true);
		setMaxDamage(0);
		
		blockLM = (BlockLM)b;
	}

	public int getMetadata(int m)
	{ return m; }
	
	public String getUnlocalizedName(ItemStack is)
	{ return blockLM.getUnlocalizedName(is.getItemDamage()); }
	
	@SuppressWarnings("all")
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item j, CreativeTabs c, List l)
	{ blockLM.getSubBlocks(j, c, l); }
	
	@SideOnly(Side.CLIENT)
	private FastList<String> infoList;
	
	@SuppressWarnings("all") @SideOnly(Side.CLIENT)
    public final void addInformation(ItemStack is, EntityPlayer ep, List l, boolean b)
	{
		if(infoList == null) infoList = new FastList<String>();
		infoList.clear();
		blockLM.addInfo(is, ep, infoList);
		l.addAll(infoList);
	}
}