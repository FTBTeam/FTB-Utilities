package latmod.core.base;
import java.util.List;
import cpw.mods.fml.relauncher.*;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
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

	@SideOnly(Side.CLIENT)
	public void getSubItems(Item j, CreativeTabs c, List l)
	{ blockLM.getSubBlocks(j, c, l); }
}