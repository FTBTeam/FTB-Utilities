package latmod.core.mod.tile;

import latmod.core.client.*;
import latmod.core.mod.tile.PainterHelper.IPaintable;
import latmod.core.mod.tile.PainterHelper.Paint;
import latmod.core.mod.tile.PainterHelper.PaintData;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
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
		if(p.canReplace(paint[p.side]))
		{
			paint[p.side] = p.paint;
			markDirty();
			return true;
		}
		
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	public void renderFace(RenderBlocksCustom renderBlocks, ForgeDirection face)
	{
		int id = face.ordinal();
		
		if(paint[id] != null)
		{
			renderBlocks.setOverrideBlockTexture(paint[id].block.getIcon(id, paint[id].meta));
			renderBlocks.setCustomColor(paint[id].block.getRenderColor(paint[id].meta));
		}
		else
		{
			renderBlocks.setCustomColor(null);
			
			if(worldObj.getBlock(xCoord, yCoord, zCoord) == getBlockType())
				renderBlocks.setOverrideBlockTexture(LatCoreMCClient.blockNullIcon);
			else renderBlocks.setOverrideBlockTexture(getBlockType().getIcon(0, 0));
		}
		
		renderBlocks.renderStandardBlock(Blocks.stone, xCoord, yCoord, zCoord);
	}
}