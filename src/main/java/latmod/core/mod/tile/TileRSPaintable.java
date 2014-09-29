package latmod.core.mod.tile;

import latmod.core.mod.tile.PainterHelper.Paint;
import net.minecraft.nbt.NBTTagCompound;

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
	
	public Paint[] currentPaint()
	{ return redstonePowered ? paint_on : paint; }
}