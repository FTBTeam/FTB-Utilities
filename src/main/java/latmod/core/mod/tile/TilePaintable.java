package latmod.core.mod.tile;

import latmod.core.LatCoreMC;
import latmod.core.mod.LCItems;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.util.*;
import net.minecraftforge.common.util.ForgeDirection;
import scala.actors.threadpool.Arrays;
import cpw.mods.fml.relauncher.*;

public class TilePaintable extends TileLM implements IPaintable
{
	public ItemStack[] paintItems = new ItemStack[6];
	
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
	
	public boolean setPaint(EntityPlayer ep, MovingObjectPosition mop, ItemStack paint)
	{
		if(paintItems[mop.sideHit] == null || paint == null || !paintItems[mop.sideHit].isItemEqual(paint))
		{
			paintItems[mop.sideHit] = paint;
			markDirty();
			return true;
		}
		
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(ForgeDirection f)
	{
		int id = f.ordinal();
		
		if(paintItems[id] != null)
			return Block.getBlockFromItem(paintItems[id].getItem()).getIcon(id, paintItems[id].getItemDamage());
		return LCItems.b_paintable.getBlockIcon();
	}
}