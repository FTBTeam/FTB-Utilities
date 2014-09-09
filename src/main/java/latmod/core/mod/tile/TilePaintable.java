package latmod.core.mod.tile;

import scala.actors.threadpool.Arrays;
import latmod.core.LatCoreMC;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;

public class TilePaintable extends TileLM implements IPaintable
{
	private ItemStack[] paintItems = new ItemStack[6];
	
	public boolean rerenderBlock()
	{ return true; }
	
	public void readTileData(NBTTagCompound tag)
	{
		super.readTileData(tag);
		
		NBTTagList list = tag.getTagList("Textures", LatCoreMC.NBT_MAP);
		
		Arrays.fill(paintItems, null);
		
		for(int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound tag1 = list.getCompoundTagAt(i);
			
			int s = tag1.getByte("Side");
			paintItems[s] = ItemStack.loadItemStackFromNBT(tag1);
		}
	}
	
	public void writeTileData(NBTTagCompound tag)
	{
		super.writeTileData(tag);
		
		NBTTagList list = new NBTTagList();
		
		for(int i = 0; i < 6; i++)
		{
			if(paintItems[i] != null)
			{
				NBTTagCompound tag1 = new NBTTagCompound();
				paintItems[i].writeToNBT(tag1);
				tag1.setByte("Side", (byte)i);
				list.appendTag(tag1);
			}
		}
		
		if(list.tagCount() > 0) tag.setTag("Textures", list);
	}
	
	public ItemStack getPaint(int s)
	{ return paintItems[s]; }
	
	public boolean setPaint(ItemStack is, EntityPlayer ep, int s)
	{
		if(paintItems[s] == null || is == null || !paintItems[s].isItemEqual(is))
		{
			paintItems[s] = is;
			markDirty();
			return true;
		}
		
		return false;
	}
}