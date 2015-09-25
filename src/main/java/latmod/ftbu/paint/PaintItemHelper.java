package latmod.ftbu.paint;

import latmod.ftbu.inv.LMInvUtils;
import latmod.ftbu.util.*;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class PaintItemHelper
{
	public static ItemStack getPaintItem(ItemStack is)
	{
		return (is.hasTagCompound() && is.stackTagCompound.hasKey("Paint"))
				? ItemStack.loadItemStackFromNBT(is.stackTagCompound.getCompoundTag("Paint")) : null;
	}
	
	public static ItemStack onItemRightClick(IPainterItem i, ItemStack is, World w, EntityPlayer ep)
	{
		if(!w.isRemote && ep.isSneaking() && is.hasTagCompound() && is.stackTagCompound.hasKey("Paint"))
		{
			is = LMInvUtils.removeTags(is, "Paint");
			LatCoreMC.printChat(ep, "Paint texture cleared");
		}
		
		return is;
	}
	
	public static boolean onItemUse(IPainterItem i, ItemStack is, EntityPlayer ep, World w, int x, int y, int z, int s, float x1, float y1, float z1)
	{
		if(w.isRemote) return true;
		
		TileEntity te = w.getTileEntity(x, y, z);
		
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
					
					if(b != Blocks.air)
						p = new Paint(b, paint.getItemDamage());
				}
				
				if(mop != null && ((IPaintable)te).setPaint(new PaintData(ep, p, x, y, z, x1, y1, z1, s, mop.subHit)))
				{
					if(!ep.capabilities.isCreativeMode)
						i.damagePainter(is, ep);
				}
			}
		}
		else if(ep.isSneaking())
		{
			Block b = w.getBlock(x, y, z);
			
			if(b != Blocks.air)
			{
				int m = w.getBlockMetadata(x, y, z);
				
				if(b.hasTileEntity(m) && !(b instanceof ICustomPaintBlock)) return true;
				
				if(b.getBlockBoundsMinX() == 0D && b.getBlockBoundsMinY() == 0D && b.getBlockBoundsMinZ() == 0D
				&& b.getBlockBoundsMaxX() == 1D && b.getBlockBoundsMaxY() == 1D && b.getBlockBoundsMaxZ() == 1D)
				{
					if(b instanceof INoPaintBlock && !((INoPaintBlock)b).hasPaint(w, x, y, z, s))
						return true;
					
					ItemStack paint = new ItemStack(b, 1, m);
					
					if(b instanceof ICustomPaintBlock)
					{
						Paint p = ((ICustomPaintBlock) b).getCustomPaint(w, x, y, z);
						if(p != null) paint = new ItemStack(p.block, 1, p.meta);
						else return true;
					}
					
					try
					{
						paint.getDisplayName();
						
						ItemStack paint0 = getPaintItem(is);
						
						if(paint0 == null || !ItemStack.areItemStacksEqual(paint0, paint))
						{
							if(!is.hasTagCompound())
								is.stackTagCompound = new NBTTagCompound();
							
							NBTTagCompound paintTag = new NBTTagCompound();
							paint.writeToNBT(paintTag);
							is.stackTagCompound.setTag("Paint", paintTag);
							
							LatCoreMC.printChat(ep, "Paint texture set to " + paint.getDisplayName());
						}
					}
					catch(Exception e) { }
				}
			}
		}
		
		return true;
	}
}