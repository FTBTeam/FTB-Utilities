package latmod.core.mod.tile;

import java.util.List;

import latmod.core.client.RenderBlocksCustom;
import latmod.core.mod.tile.PainterHelper.IPaintable;
import latmod.core.mod.tile.PainterHelper.Paint;
import latmod.core.mod.tile.PainterHelper.PaintData;
import mcp.mobius.waila.api.*;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.*;

public class TilePaintable extends TileLM implements IPaintable, IWailaTile.Stack, IWailaTile.Head
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
		if(p.player.isSneaking())
		{
			for(int i = 0; i < 6; i++)
				currentPaint()[i] = p.paint;
			markDirty();
			return true;
		}
		
		if(p.canReplace(currentPaint()[p.side]))
		{
			currentPaint()[p.side] = p.paint;
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

	public ItemStack getWailaStack(IWailaDataAccessor data, IWailaConfigHandler config)
	{
		Paint p = currentPaint()[data.getSide().ordinal()];
		return (p == null) ? null : new ItemStack(p.block, 1, p.meta);
	}
	
	public void addWailaHead(IWailaDataAccessor data, IWailaConfigHandler config, List<String> info)
	{
		Paint p = currentPaint()[data.getSide().ordinal()];
		if(p != null)
		{
			info.clear();
			info.add(SpecialChars.WHITE + new ItemStack(p.block, 1, p.meta).getDisplayName());
		}
	}
}