package latmod.core.mod.tile;

import latmod.core.*;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import scala.actors.threadpool.Arrays;

public class PainterHelper
{
	public static interface IPainterItem
	{
		public ItemStack getPaintItem(ItemStack is);
		public boolean canPaintBlock(ItemStack is);
		public void damagePainter(ItemStack is, EntityPlayer ep);
	}
	
	public static interface IPaintable extends ITileInterface
	{
		public boolean setPaint(PaintData p);
	}
	
	public static class Paint
	{
		public final Block block;
		public final int meta;
		
		public Paint(Block b, int m)
		{
			block = b;
			meta = m;
		}
		
		public static void readFromNBT(NBTTagCompound tag, String s, Paint[] paint)
		{
			Arrays.fill(paint, null);
			
			NBTTagList l = (NBTTagList)tag.getTag(s);
			
			if(l != null) for(int i = 0; i < l.tagCount(); i++)
			{
				NBTTagCompound tag1 = l.getCompoundTagAt(i);
				
				int id = tag1.getByte("ID");
				paint[id] = new Paint(Block.getBlockById(tag1.getInteger("BlockID")), tag1.getInteger("Metadata"));
			}
		}
		
		public static void writeToNBT(NBTTagCompound tag, String s, Paint[] paint)
		{
			NBTTagList l = new NBTTagList();
			
			for(int i = 0; i < paint.length; i++) if(paint[i] != null)
			{
				NBTTagCompound tag1 = new NBTTagCompound();
				tag1.setByte("ID", (byte)i);
				tag1.setInteger("BlockID", Block.getIdFromBlock(paint[i].block));
				tag1.setInteger("Metadata", paint[i].meta);
				l.appendTag(tag1);
			}
			
			if(l.tagCount() > 0) tag.setTag(s, l);
		}
	}
	
	public static class PaintData
	{
		public final EntityPlayer player;
		
		public final int posX;
		public final int posY;
		public final int posZ;
		
		public final float hitX;
		public final float hitY;
		public final float hitZ;
		
		public final int side;
		public final int subHit;
		
		public final Paint paint;
		
		public PaintData(EntityPlayer ep, Paint p, int x, int y, int z, float hx, float hy, float hz, int s, int sh)
		{
			player = ep; paint = p;
			posX = x; posY = y; posZ = z;
			hitX = hx; hitY = hx; hitZ = hx;
			side = s; subHit = sh;
		}
		
		public boolean canReplace(Paint p)
		{ return p == null || p.block != paint.block || (p.block == paint.block && p.meta != paint.meta); }
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
			Block b = Block.getBlockFromItem(paint.getItem());
			
			if(b != Blocks.air && (ep.capabilities.isCreativeMode || i.canPaintBlock(is)))
			{
				//MovingObjectPosition mop = new MovingObjectPosition(x, y, z, s, Vec3.createVectorHelper(x1, y1, z1));
				MovingObjectPosition mop = LatCoreMC.rayTrace(ep);
				
				if(mop != null && ((IPaintable)te).setPaint(new PaintData(ep, new Paint(b, paint.getItemDamage()), x, y, z, x1, y1, z1, s, mop.subHit)))
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