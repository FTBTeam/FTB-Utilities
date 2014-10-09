package latmod.core.mod.tile;

import latmod.core.client.RenderBlocksCustom;
import latmod.core.mod.tile.PainterHelper.IPaintable;
import latmod.core.mod.tile.PainterHelper.Paint;
import latmod.core.mod.tile.PainterHelper.PaintData;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.*;

public class TilePaintable extends TileLM implements IPaintable
{
	public final Paint[] paint = new Paint[6];
	
	public boolean rerenderBlock()
	{ return true; }
	
	public void readTileData(NBTTagCompound tag)
	{
		super.readTileData(tag);
		Paint.readFromNBT(tag, "Textures", paint);
	}
	
	public void writeTileData(NBTTagCompound tag)
	{
		super.writeTileData(tag);
		Paint.writeToNBT(tag, "Textures", paint);
	}
	
	public boolean setPaint(PaintData p)
	{
		if(p.paint != null) return false;
		
		Paint[] paint1 = currentPaint();
		
		if(p.player.isSneaking())
		{
			for(int i = 0; i < 6; i++)
				paint1[i] = p.paint;
			markDirty();
			return true;
		}
		
		if(p.canReplace(paint1[p.side]))
		{
			paint1[p.side] = p.paint;
			markDirty();
			return true;
		}
		
		return false;
	}
	
	public Paint[] currentPaint()
	{ return paint; }
	
	@SideOnly(Side.CLIENT)
	public static void renderFace(RenderBlocksCustom rb, ForgeDirection face, Paint[] p, IIcon defIcon, int x, int y, int z)
	{
		int id = face.ordinal();
		
		if(p[id] != null)
		{
			rb.setOverrideBlockTexture(p[id].block.getIcon(id, p[id].meta));
			rb.setCustomColor(p[id].block.getRenderColor(p[id].meta));
		}
		else
		{
			rb.setCustomColor(null);
			
			//if(worldObj.getBlock(xCoord + face.offsetX, yCoord + face.offsetY, zCoord + face.offsetZ) == getBlockType())
			//	renderBlocks.setOverrideBlockTexture(LatCoreMCClient.blockNullIcon); else
			
			rb.setOverrideBlockTexture(defIcon);
		}
		
		rb.renderStandardBlock(Blocks.glass, x, y, z);
	}
	
	@SideOnly(Side.CLIENT)
	public static void renderBlock(RenderBlocksCustom rb, Paint[] p, IIcon defIcon, int x, int y, int z)
	{
		double d0 = 0D;
		double d1 = 1D - d0;
		
		rb.setRenderBounds(0D, d0, 0D, 1D, d0, 1D);
		renderFace(rb, ForgeDirection.DOWN, p, defIcon, x, y, z);
		
		rb.setRenderBounds(0D, d1, 0D, 1D, d1, 1D);
		renderFace(rb, ForgeDirection.UP, p, defIcon, x, y, z);
		
		rb.setRenderBounds(0D, 0D, d0, 1D, 1D, d0);
		renderFace(rb, ForgeDirection.NORTH, p, defIcon, x, y, z);
		
		rb.setRenderBounds(0D, 0D, d1, 1D, 1D, d1);
		renderFace(rb, ForgeDirection.SOUTH, p, defIcon, x, y, z);
		
		rb.setRenderBounds(d0, 0D, 0D, d0, 1D, 1D);
		renderFace(rb, ForgeDirection.WEST, p, defIcon, x, y, z);
		
		rb.setRenderBounds(d1, 0D, 0D, d1, 1D, 1D);
		renderFace(rb, ForgeDirection.EAST, p, defIcon, x, y, z);
	}
}