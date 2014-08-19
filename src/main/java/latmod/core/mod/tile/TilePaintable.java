package latmod.core.mod.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TilePaintable extends TileLM implements IPaintable
{
	private ItemStack paintItem;
	
	public void readTileData(NBTTagCompound tag)
	{
		super.readTileData(tag);
		
		if(!tag.hasKey("Paint")) paintItem = null;
		else paintItem = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("Paint"));
	}
	
	public void writeTileData(NBTTagCompound tag)
	{
		super.writeTileData(tag);
		
		if(paintItem != null)
		{
			NBTTagCompound tag1 = new NBTTagCompound();
			paintItem.writeToNBT(tag1);
			tag.setTag("Paint", tag1);
		}
	}
	
	public final ItemStack getPaint()
	{ return paintItem; }
	
	public boolean setPaint(ItemStack is, EntityPlayer ep)
	{
		if(paintItem == null || is == null || !paintItem.isItemEqual(is))
		{
			paintItem = is;
			markDirty();
			return true;
		}
		
		return false;
	}
}