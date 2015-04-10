package latmod.core.item;
import java.util.List;

import latmod.core.block.BlockLM;
import latmod.core.util.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.*;

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
	
	public boolean allowMinus1()
	{ return false; }
	
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
	{
		if(metadata == -1 && !allowMinus1()) return false;
		return super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
	}
	
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World w, int x, int y, int z, int s, float hitX, float hitY, float hitZ)
	{
		if(!canPlace(w, x, y, z, s, is)) return false;
		
		Block block = w.getBlock(x, y, z);
		
		if (block == Blocks.snow_layer && (w.getBlockMetadata(x, y, z) & 7) < 1) s = 1;
		else if (block != Blocks.vine && block != Blocks.tallgrass && block != Blocks.deadbush && !block.isReplaceable(w, x, y, z))
		{
			x += ForgeDirection.VALID_DIRECTIONS[s].offsetX;
			y += ForgeDirection.VALID_DIRECTIONS[s].offsetY;
			z += ForgeDirection.VALID_DIRECTIONS[s].offsetZ;
		}
		
		if (is.stackSize == 0) return false;
		else if (!ep.canPlayerEdit(x, y, z, s, is)) return false;
		else if (y == 255 && blockLM.getMaterial().isSolid()) return false;
		else if (w.canPlaceEntityOnSide(blockLM, x, y, z, false, s, null, is.copy()))
		{
			int j1 = blockLM.onBlockPlaced(w, ep, MathHelperLM.getMOPFrom(x, y, z, s, hitX, hitY, hitZ), getMetadata(is.getItemDamage()));
			if (placeBlockAt(is, ep, w, x, y, z, s, hitX, hitY, hitZ, j1))
			{
				w.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, blockLM.stepSound.func_150496_b(), (blockLM.stepSound.getVolume() + 1F) / 2F, blockLM.stepSound.getPitch() * 0.8F);
				is.stackSize--;
			}

			return true;
		}
		
		return false;
	}
	
	public boolean canPlace(World w, int x, int y, int z, int s, ItemStack is)
	{
		Block b = w.getBlock(x, y, z);
		
        if (b == Blocks.snow_layer && (w.getBlockMetadata(x, y, z) & 7) < 1) s = 1;
        else if (b != Blocks.vine && b != Blocks.tallgrass && b != Blocks.deadbush && !b.isReplaceable(w, x, y, z))
        {
        	x += ForgeDirection.VALID_DIRECTIONS[s].offsetX;
			y += ForgeDirection.VALID_DIRECTIONS[s].offsetY;
			z += ForgeDirection.VALID_DIRECTIONS[s].offsetZ;
        }
        
		return b.getMaterial() != Material.air && w.canPlaceEntityOnSide(b, x, y, z, false, s, null, is.copy());
	}
}