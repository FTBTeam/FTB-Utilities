package latmod.core.mod.tile;

import latmod.core.LatCoreMC;
import latmod.core.mod.LCItems;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.util.*;
import net.minecraftforge.common.util.ForgeDirection;
import scala.actors.threadpool.Arrays;
import cpw.mods.fml.relauncher.*;

public class TilePaintable extends TileLM implements IPaintable
{
	public Block[] paintBlocks = new Block[6];
	public int[] paintMetas = new int[6];
	
	public boolean rerenderBlock()
	{ return true; }
	
	public void readTileData(NBTTagCompound tag)
	{
		super.readTileData(tag);
		
		NBTTagList list = tag.getTagList("Textures", LatCoreMC.NBT_MAP);
		
		Arrays.fill(paintBlocks, null);
		
		for(int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound tag1 = list.getCompoundTagAt(i);
			
			int s = tag1.getByte("Side");
			ItemStack is = ItemStack.loadItemStackFromNBT(tag1);
			paintBlocks[s] = Block.getBlockFromItem(is.getItem());
			paintMetas[s] = is.getItemDamage();
		}
	}
	
	public void writeTileData(NBTTagCompound tag)
	{
		super.writeTileData(tag);
		
		NBTTagList list = new NBTTagList();
		
		for(int i = 0; i < 6; i++)
		{
			if(paintBlocks[i] != null)
			{
				NBTTagCompound tag1 = new NBTTagCompound();
				(new ItemStack(paintBlocks[i], 1, paintMetas[i])).writeToNBT(tag1);
				tag1.setByte("Side", (byte)i);
				list.appendTag(tag1);
			}
		}
		
		if(list.tagCount() > 0) tag.setTag("Textures", list);
	}
	
	public boolean setPaint(EntityPlayer ep, MovingObjectPosition mop, ItemStack paint)
	{
		if(paintBlocks[mop.sideHit] == null || paint == null || Item.getItemFromBlock(paintBlocks[mop.sideHit]) != paint.getItem())
		{
			paintBlocks[mop.sideHit] = Block.getBlockFromItem(paint.getItem());
			paintMetas[mop.sideHit] = paint.getItemDamage();
			markDirty();
			return true;
		}
		
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(ForgeDirection f)
	{
		int id = f.ordinal();
		
		if(paintBlocks[id] != null)
			return paintBlocks[id].getIcon(id, paintMetas[id]);
		return LCItems.b_paintable.getBlockIcon();
	}
	
	@SideOnly(Side.CLIENT)
	public int getColor(ForgeDirection f)
	{
		int id = f.ordinal();
		
		if(paintBlocks[id] != null)
			return paintBlocks[id].getRenderColor(paintMetas[id]);
		return 0xFFFFFFFF;
	}
}