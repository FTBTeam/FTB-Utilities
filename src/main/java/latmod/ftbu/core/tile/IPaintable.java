package latmod.ftbu.core.tile;

import java.util.Arrays;

import latmod.ftbu.core.*;
import latmod.ftbu.core.client.RenderBlocksCustom;
import latmod.ftbu.core.util.MathHelperLM;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.*;

public interface IPaintable
{
	public boolean setPaint(PaintData p);
	
	public static interface IPainterItem
	{
		public ItemStack getPaintItem(ItemStack is);
		public boolean canPaintBlock(ItemStack is);
		public void damagePainter(ItemStack is, EntityPlayer ep);
	}
	
	public static class Paint implements Cloneable
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
			if(paint == null || paint.length == 0) return;
			Arrays.fill(paint, null);
			
			NBTTagList l = (NBTTagList)tag.getTag(s);
			
			if(l != null)
			for(int i = 0; i < l.tagCount(); i++)
			{
				if(l.func_150303_d() == NBTHelper.MAP)
				{
					NBTTagCompound tag1 = l.getCompoundTagAt(i);
					int id = tag1.getByte("ID");
					Block b = Block.getBlockById(tag1.getInteger("BlockID"));
					int m = tag1.getInteger("Metadata");
					if(b == null || b == Blocks.air || b.hasTileEntity(m))
					{ b = Blocks.stone; m = 0; }
					paint[id] = new Paint(b, m);
				}
				else
				{
					int[] ai = l.func_150306_c(i);
					
					Block b = Block.getBlockById(ai[1]);
					if(b == null || b == Blocks.air || b.hasTileEntity(ai[2]))
					{ b = Blocks.stone; ai[2] = 0; }
					
					if(l.tagCount() == 1 && ai[0] == -1)
						for(int j = 0; j < paint.length; j++)
							paint[j] = new Paint(b, ai[2]);
					else paint[ai[0]] = new Paint(b, ai[2]);
				}
			}
		}
		
		public static void writeToNBT(NBTTagCompound tag, String s, Paint[] paint)
		{
			if(paint == null || paint.length == 0) return;
			
			NBTTagList l = new NBTTagList();
			
			boolean allEqual = true;
			
			for(int i = 0; i < paint.length; i++)
			{
				if(!(paint[i] != null && paint[i].block == paint[0].block && paint[i].meta == paint[0].meta))
				{ allEqual = false; break; }
			}
			
			if(allEqual)
				l.appendTag(new NBTTagIntArray(new int[] { -1, Block.getIdFromBlock(paint[0].block), paint[0].meta }));
			else for(int i = 0; i < paint.length; i++) if(paint[i] != null)
				l.appendTag(new NBTTagIntArray(new int[] { i, Block.getIdFromBlock(paint[i].block), paint[i].meta }));
			
			if(l.tagCount() > 0) tag.setTag(s, l);
		}
		
		public Paint clone()
		{ return new Paint(block, meta); }
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
		{
			if(p == null && paint == null) return false;
			if(p == null && paint != null) return true;
			if(p != null && paint == null) return true;
			return p.block != paint.block || (p.block == paint.block && p.meta != paint.meta);
		}
	}
	
	public static interface ICustomPaintBlock
	{
		@SideOnly(Side.CLIENT)
		public IIcon getCustomPaint(int side, int meta);
	}
	
	public static interface INoPaint
	{
		public boolean hasPaint(IBlockAccess iba, int x, int y, int z, int s);
	}
	
	public static class Helper
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
					MovingObjectPosition mop = MathHelperLM.rayTrace(ep);
					
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
				Block b = ep.worldObj.getBlock(x, y, z);
				
				if(b != Blocks.air)
				{
					int m = ep.worldObj.getBlockMetadata(x, y, z);
					
					if(b.hasTileEntity(m)) return true;
					
					if(b.getBlockBoundsMinX() == 0D && b.getBlockBoundsMinY() == 0D && b.getBlockBoundsMinZ() == 0D
					&& b.getBlockBoundsMaxX() == 1D && b.getBlockBoundsMaxY() == 1D && b.getBlockBoundsMaxZ() == 1D)
					{
						if(b instanceof INoPaint && !((INoPaint)b).hasPaint(w, x, y, z, s))
							return true;
						
						ItemStack paint = new ItemStack(b, 1, m);
						
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
	
	@SideOnly(Side.CLIENT)
	public static class Renderer
	{
		public static IIcon[] to6(IIcon p)
		{ return new IIcon[] { p, p, p, p, p, p }; }
		
		public static Paint[] to6(Paint p)
		{ return new Paint[] { p, p, p, p, p, p }; }
		
		public static void renderCube(IBlockAccess iba, RenderBlocksCustom rb, Paint[] p, IIcon[] defIcon, int x, int y, int z, AxisAlignedBB aabb)
		{
			for(int i = 0; i < 6; i++)
			{
				rb.setFaceBounds(i, aabb);
				renderFace(iba, rb, i, p[i], defIcon[i], x, y, z);
			}
		}
		
		public static void renderFace(IBlockAccess iba, RenderBlocksCustom rb, int side, Paint p, IIcon defIcon, int x, int y, int z)
		{
			if(rb.blockAccess != null)
			{
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[side];
				Block b = rb.blockAccess.getBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
				
				if(b.getMaterial() != Material.air)
				{
					double d = -0.0001D;
					if(side == 0) { rb.renderMaxY -= d; rb.renderMinY -= d; }
					if(side == 1) { rb.renderMinY += d; rb.renderMaxY += d; }
					if(side == 2) { rb.renderMaxZ -= d; rb.renderMinZ -= d; }
					if(side == 3) { rb.renderMinZ += d; rb.renderMaxZ += d; }
					if(side == 4) { rb.renderMaxX -= d; rb.renderMinX -= d; }
					if(side == 5) { rb.renderMinX += d; rb.renderMaxX += d; }
				}
			}
			
			boolean b = rb.renderAllFaces;
			rb.renderAllFaces = true;
			rb.setCustomColor(null);
			
			rb.blockAccess = new BlockAccess(iba, x, y, z, p);
			
			IIcon icon = defIcon;
			
			if(p != null)
			{
				if(p.block == null || p.block == Blocks.air) icon = Blocks.stone.getBlockTextureFromSide(1);
				else try
				{
					if(p.block instanceof ICustomPaintBlock)
					{
						icon = ((ICustomPaintBlock)p.block).getCustomPaint(side, p.meta);
						if(icon == null) icon = defIcon;
					}
					else icon = p.block.getIcon(rb.blockAccess, x, y, z, side);
					
					if(side != 1 && p.block == Blocks.grass)
						rb.setCustomColor(null);
					else
						rb.setCustomColor(p.block.colorMultiplier(rb.blockAccess, x, y, z));
				}
				catch(Exception e)
				{
					icon = Blocks.stone.getBlockTextureFromSide(1);
				}
			}
			
			rb.setOverrideBlockTexture(icon);
			rb.renderStandardBlock(Blocks.stone, x, y, z);
			rb.renderAllFaces = b;
			rb.blockAccess = iba;
		}
	}
	
	public static class BlockAccess extends CustomBlockAccess
	{
		public final int blockX, blockY, blockZ;
		public final Paint paint;
		
		public BlockAccess(IBlockAccess iba, int x, int y, int z, Paint p)
		{ super(iba); blockX = x; blockY = y; blockZ = z; paint = p; }
		
		public Block getBlock(int x, int y, int z)
		{
			if(paint != null && x == blockX && y == blockY && z == blockZ)
				return paint.block; return Blocks.air;
		}
		
		public int getBlockMetadata(int x, int y, int z)
		{
			if(paint != null && x == blockX && y == blockY && z == blockZ)
				return paint.meta; return 0;
		}
	}
}