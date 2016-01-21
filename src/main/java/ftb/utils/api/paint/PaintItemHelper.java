package ftb.utils.api.paint;

import ftb.lib.*;
import ftb.lib.api.item.LMInvUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class PaintItemHelper
{
	public static ItemStack getPaintItem(ItemStack is)
	{
		return (is.hasTagCompound() && is.getTagCompound().hasKey("Paint")) ? ItemStack.loadItemStackFromNBT(is.getTagCompound().getCompoundTag("Paint")) : null;
	}
	
	public static ItemStack onItemRightClick(IPainterItem i, ItemStack is, World w, EntityPlayer ep)
	{
		if(!w.isRemote && ep.isSneaking() && is.hasTagCompound() && is.getTagCompound().hasKey("Paint"))
		{
			is = LMInvUtils.removeTags(is, "Paint");
			FTBLib.printChat(ep, "Paint texture cleared");
		}
		
		return is;
	}
	
	public static boolean onItemUse(IPainterItem i, ItemStack is, EntityPlayer ep, World w, BlockPos pos, EnumFacing s, float x1, float y1, float z1)
	{
		if(w.isRemote) return true;
		
		TileEntity te = w.getTileEntity(pos);
		
		if(te != null && te instanceof IPaintable)
		{
			ItemStack paint = getPaintItem(is);
			
			if(ep.capabilities.isCreativeMode || i.canPaintBlock(is))
			{
				MovingObjectPosition mop = MathHelperMC.rayTrace(ep);
				
				Paint p = null;
				if(paint != null && paint.getItem() != null)
				{
					Block b = Block.getBlockFromItem(paint.getItem());
					
					if(b != Blocks.air) p = new Paint(b, paint.getItemDamage());
				}
				
				if(mop != null && ((IPaintable) te).setPaint(new PaintData(ep, p, pos, x1, y1, z1, s, mop.subHit)))
				{
					if(!ep.capabilities.isCreativeMode) i.damagePainter(is, ep);
				}
			}
		}
		else if(ep.isSneaking())
		{
			IBlockState state = w.getBlockState(pos);
			Block b = state.getBlock();
			
			if(b != Blocks.air)
			{
				if(b.hasTileEntity(state) && !(b instanceof ICustomPaintBlock)) return true;
				
				if(b.getBlockBoundsMinX() == 0D && b.getBlockBoundsMinY() == 0D && b.getBlockBoundsMinZ() == 0D && b.getBlockBoundsMaxX() == 1D && b.getBlockBoundsMaxY() == 1D && b.getBlockBoundsMaxZ() == 1D)
				{
					if(b instanceof INoPaintBlock && !((INoPaintBlock) b).hasPaint(w, pos, state, s)) return true;
					
					ItemStack paint = new ItemStack(b, 1, b.getMetaFromState(state));
					
					if(b instanceof ICustomPaintBlock)
					{
						Paint p = ((ICustomPaintBlock) b).getCustomPaint(w, pos, state);
						if(p != null) paint = new ItemStack(p.block, 1, p.meta);
						else return true;
					}
					
					try
					{
						paint.getDisplayName();
						
						ItemStack paint0 = getPaintItem(is);
						
						if(paint0 == null || !ItemStack.areItemStacksEqual(paint0, paint))
						{
							if(!is.hasTagCompound()) is.setTagCompound(new NBTTagCompound());
							
							NBTTagCompound paintTag = new NBTTagCompound();
							paint.writeToNBT(paintTag);
							is.getTagCompound().setTag("Paint", paintTag);
							
							FTBLib.printChat(ep, "Paint texture set to " + paint.getDisplayName());
						}
					}
					catch(Exception e) { }
				}
			}
		}
		
		return true;
	}
}