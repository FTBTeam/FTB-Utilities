package mods.lm.core;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.*;
import net.minecraft.world.*;
import net.minecraftforge.common.*;

public class InvUtils
{
	public static ItemStack single(ItemStack is)
	{
		if(is == null || is.stackSize <= 0) return null;
		ItemStack is1 = is.copy(); is1.stackSize = 1; return is1;
	}
	
	public static ItemStack loadFromSubtag(NBTTagCompound tag, String s)
	{ return ItemStack.loadItemStackFromNBT(tag.getCompoundTag(s)); }
	
	public static IInventory getInvAt(World w, double x, double y, double z, boolean entities)
	{
		if(entities) return TileEntityHopper.getInventoryAtLocation(w, x, y, z);
		TileEntity te = w.getBlockTileEntity((int)x, (int)y, (int)z);
		return (te != null && te instanceof IInventory) ? (IInventory)te : null;
	}
	
	public static IInventory getInvAt(TileEntity te, ForgeDirection side)
	{
		if(side == null || side == ForgeDirection.UNKNOWN) return null;
		
		int x = te.xCoord + side.offsetX;
		int y = te.yCoord + side.offsetY;
		int z = te.zCoord + side.offsetZ;
		
		//TODO: Make more specific when Tile == Chest
		TileEntity te1 = te.worldObj.getBlockTileEntity(x, y, z);
		if(te1 != null && te1 instanceof IInventory) return (IInventory)te1;
		
		return null;
	}
	
	public static boolean itemsEquals(ItemStack is1, ItemStack is2, boolean size)
	{ return is1.itemID == is2.itemID && is1.getItemDamage() == is2.getItemDamage() && ItemStack.areItemStackTagsEqual(is1, is2) && (size ? (is1.stackSize == is2.stackSize) : true); }
	
	public static int[] getAllSlots(IInventory inv, ForgeDirection side)
	{
		int ai[] = null;
		
		if(side != null && side != ForgeDirection.UNKNOWN && inv instanceof ISidedInventory)
		ai = ((ISidedInventory)inv).getAccessibleSlotsFromSide(side.ordinal());
		
		else { ai = new int[inv.getSizeInventory()];
		for(int i = 0; i < ai.length; i++) ai[i] = i; }
		
		if(ai == null) ai = new int[0];
		return ai;
	}
	
	public static int getFirstIndexWhereFits(IInventory inv, ItemStack filter, ForgeDirection side)
	{
		if(inv == null) return -1;
		int slots[] = getAllSlots(inv, side);
		for(int i = 0; i < slots.length; i++)
		{
			ItemStack is1 = inv.getStackInSlot(slots[i]);
			
			if(is1 != null && is1.stackSize < is1.getMaxStackSize())
			{
				if(filter == null) return i;
				else if(itemsEquals(filter, is1, false) && (is1.stackSize + filter.stackSize <= filter.getMaxStackSize())) return i;
			}
		}
		
		return getFirstEmptyIndex(inv, side);
	}
	
	public static int getFirstFilledIndex(IInventory inv, ItemStack filter, ForgeDirection side)
	{
		if(inv == null) return -1;
		int slots[] = getAllSlots(inv, side);
		for(int i = 0; i < slots.length; i++)
		{
			ItemStack is1 = inv.getStackInSlot(slots[i]);
			
			if(is1 != null)
			{
				if(filter == null) return i;
				else if(itemsEquals(filter, is1, false)) return i;
			}
		}
		
		return -1;
	}
	
	public static int getFirstEmptyIndex(IInventory inv, ForgeDirection side)
	{
		if(inv == null) return -1;
		
		int slots[] = getAllSlots(inv, side);
		for(int i = 0; i < slots.length; i++)
		{
			ItemStack is1 = inv.getStackInSlot(slots[i]);
			if(is1 == null || is1.stackSize <= 0) return i;
		}
		
		return -1;
	}
	
	public static boolean reduceItemInInv(IInventory inv, int i)
	{
		if(inv == null || i == -1 || i >= inv.getSizeInventory()) return false;
		ItemStack is = inv.getStackInSlot(i);

		if(is != null)
		{
			is.stackSize--;
			if(is.stackSize <= 0) is = null;
			inv.setInventorySlotContents(i, is);
			inv.onInventoryChanged();
		}

		return false;
	}
	
	public static boolean addItemToInv(IInventory inv, ItemStack is, ForgeDirection side)
	{
		if(inv == null || is == null || is.stackSize != 1 || side == null || side == ForgeDirection.UNKNOWN) return false;
		int[] slots = null;
		
		ISidedInventory sidedInv = (inv instanceof ISidedInventory) ? (ISidedInventory)inv : null;
		
		if(sidedInv != null)
			slots = sidedInv.getAccessibleSlotsFromSide(side.ordinal());
		else
		{
			slots = new int[inv.getSizeInventory()];
			for(int i = 0; i < slots.length; i++)
				slots[i] = i;
		}
		
		for(int i = 0; i < slots.length; i++)
		{
			ItemStack is1 = inv.getStackInSlot(slots[i]);
			if(is1 == null || is1.stackSize == 0)
			{
				if(sidedInv != null && !sidedInv.canInsertItem(slots[i], is, side.ordinal())) return false;
				
				inv.setInventorySlotContents(slots[i], is);
				inv.onInventoryChanged();
				return true;
			}
			else if(itemsEquals(is, is1, false))
			{
				if(is1.stackSize + 1 <= is1.getMaxStackSize())
				{
					if(sidedInv != null && !sidedInv.canInsertItem(slots[i], is, side.ordinal())) return false;
					
					is1.stackSize++;
					inv.setInventorySlotContents(slots[i], is1);
					inv.onInventoryChanged();
					return true;
				}
			}
		}

		return false;
	}
	
	public static NBTTagCompound removeTags(NBTTagCompound tag, String... tags)
	{
		if(tag == null || tag.hasNoTags()) return null;
		for(int i = 0; i < tags.length; i++)
		tag.removeTag(tags[i]);
		if(tag.hasNoTags()) tag = null; return tag;
	}
	
	public static void writeItemsToNBT(ItemStack[] stacks, NBTTagCompound tag, String s)
	{
		NBTTagList list = new NBTTagList();
		
		for(int i = 0; i < stacks.length; i++)
		if(stacks[i] != null)
		{
			NBTTagCompound tag1 = new NBTTagCompound();
			tag1.setShort("Slot", (short)i);
			stacks[i].writeToNBT(tag1);
			list.appendTag(tag1);
		}
		
		if(list.tagCount() > 0)
		tag.setTag(s, list);
	}
	
	public static ItemStack[] readItemsFromNBT(int invSize, NBTTagCompound tag, String s)
	{
		ItemStack[] stacks = new ItemStack[invSize];
		
		if(tag.hasKey(s))
		{
			NBTTagList list = tag.getTagList(s);
			
			for(int i = 0; i < list.tagCount(); i++)
			{
				NBTTagCompound tag1 = (NBTTagCompound)list.tagAt(i);
				int slot = tag1.getShort("Slot");
				stacks[slot] = ItemStack.loadItemStackFromNBT(tag1);
			}
		}
		
		return stacks;
	}
	
	public static void dropAllItems(World w, double x, double y, double z, ItemStack[] items)
	{
		if(w.isRemote) return;
		
		for(int i = 0; i < items.length; i++)
		{
			if(items[i] != null && items[i].stackSize > 0)
			{
				EntityItem ei = new EntityItem(w, x, y, z, items[i]);
				ei.motionX = w.rand.nextGaussian() * 0.07F;
				ei.motionY = w.rand.nextFloat() * 0.05F;
				ei.motionZ = w.rand.nextGaussian() * 0.07F;
				w.spawnEntityInWorld(ei);
			}
			
			items[i] = null;
		}
	}
}