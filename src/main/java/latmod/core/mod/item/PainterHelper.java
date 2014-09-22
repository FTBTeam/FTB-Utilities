package latmod.core.mod.item;

import latmod.core.*;
import latmod.core.mod.tile.IPaintable;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class PainterHelper
{
	public static interface IPainterItem
	{
		public ItemStack getPaintItem(ItemStack is);
		public boolean canPaintBlock(ItemStack is);
		public void damagePainter(ItemStack is, EntityPlayer ep);
	}
	
	public static ItemStack getPaintItem(ItemStack is)
	{
		return (is.hasTagCompound() && is.stackTagCompound.hasKey("Paint"))
				? ItemStack.loadItemStackFromNBT(is.stackTagCompound.getCompoundTag("Paint")) : null;
	}
	
	public static ItemStack onItemRightClick(IPainterItem i, ItemStack is, World w, EntityPlayer ep)
	{
		if(!w.isRemote && ep.isSneaking() && is.hasTagCompound() && is.stackTagCompound.hasKey("Paint"))
		{
			is = InvUtils.removeTags(is, "Paint");
			LatCoreMC.printChat(ep, "Paint texture cleared");
		}
		
		return is;
	}
	
	public static boolean onItemUse(IPainterItem i, ItemStack is, EntityPlayer ep, World w, int x, int y, int z, int s, float x1, float y1, float z1)
	{
		if(w.isRemote) return true;
		
		TileEntity te = ep.worldObj.getTileEntity(x, y, z);
		
		if(te != null && te instanceof IPaintable)
		{
			ItemStack paint = getPaintItem(is);
			
			if(ep.capabilities.isCreativeMode || i.canPaintBlock(is))
			{
				//MovingObjectPosition mop = new MovingObjectPosition(x, y, z, s, Vec3.createVectorHelper(x1, y1, z1));
				MovingObjectPosition mop = LatCoreMC.rayTrace(ep);
				
				if(((IPaintable)te).setPaint(ep, mop, paint))
				{
					if(!ep.capabilities.isCreativeMode)
						i.damagePainter(is, ep);
				}
			}
		}
		else if(te == null && ep.isSneaking())
		{
			Block b = ep.worldObj.getBlock(x, y, z);
			
			if(b != Blocks.air)
			{
				if(b.getBlockBoundsMinX() == 0D && b.getBlockBoundsMinY() == 0D && b.getBlockBoundsMinZ() == 0D
				&& b.getBlockBoundsMaxX() == 1D && b.getBlockBoundsMaxY() == 1D && b.getBlockBoundsMaxZ() == 1D)
				{
					ItemStack paint = new ItemStack(b, 1, ep.worldObj.getBlockMetadata(x, y, z));
					
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
		
		//	LMNetHandler.INSTANCE.sendToServer(new MessageClientItemAction(is, ACTION_PAINT, data));
		//else onClientAction(is, ep, ACTION_PAINT, data);
		
		return true;
	}
}