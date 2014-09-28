package latmod.core.mod.tile;

import latmod.core.client.RenderBlocksCustom;
import latmod.core.mod.tile.PainterHelper.Paint;
import latmod.core.mod.tile.PainterHelper.PaintData;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.*;

public class TileRSPaintable extends TilePaintable
{
	public final Paint[] paint_on = new Paint[6];
	
	public boolean rerenderBlock()
	{ return true; }
	
	public void readTileData(NBTTagCompound tag)
	{
		super.readTileData(tag);
		Paint.readFromNBT(tag, "TexturesOn", paint_on);
		redstonePowered = tag.getBoolean("RSIn");
	}
	
	public void writeTileData(NBTTagCompound tag)
	{
		super.writeTileData(tag);
		Paint.writeToNBT(tag, "TexturesOn", paint_on);
		tag.setBoolean("RSIn", redstonePowered);
	}
	
	public void onUpdate()
	{
	}
	
	public void onNeighborBlockChange()
	{
		redstonePowered = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
		markDirty();
	}
	
	public boolean setPaint(PaintData p)
	{
		Paint[] paint1 = redstonePowered ? paint_on : paint;
		
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
	
	@SideOnly(Side.CLIENT)
	public void renderFace(RenderBlocksCustom renderBlocks, ForgeDirection face)
	{
		int id = face.ordinal();
		
		Paint[] paint1 = redstonePowered ? paint_on : paint;
		
		if(paint1[id] != null)
		{
			renderBlocks.setOverrideBlockTexture(paint1[id].block.getIcon(id, paint1[id].meta));
			renderBlocks.setCustomColor(paint1[id].block.getRenderColor(paint1[id].meta));
		}
		else
		{
			renderBlocks.setOverrideBlockTexture(getBlockType().getIcon(0, 0));
			renderBlocks.setCustomColor(null);
		}
		
		renderBlocks.renderStandardBlock(Blocks.stone, xCoord, yCoord, zCoord);
	}
}