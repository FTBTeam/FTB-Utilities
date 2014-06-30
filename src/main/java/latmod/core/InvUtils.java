package latmod.core;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.*;
import net.minecraft.world.*;
import net.minecraftforge.common.ForgeDirection;

public class InvUtils
{
	public static ItemStack singleCopy(ItemStack is)
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
		TileEntity te1 = te.getWorldObj().getBlockTileEntity(x, y, z);
		if(te1 != null && te1 instanceof IInventory) return (IInventory)te1;
		
		return null;
	}
	
	public static boolean itemsEquals(ItemStack is1, ItemStack is2, boolean size, boolean nbt)
	{ return is1.getItem() == is2.getItem() && is1.getItemDamage() == is2.getItemDamage() && (nbt ? ItemStack.areItemStackTagsEqual(is1, is2) : true) && (size ? (is1.stackSize == is2.stackSize) : true); }
	
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
				else if(itemsEquals(filter, is1, false, true) && (is1.stackSize + filter.stackSize <= filter.getMaxStackSize())) return i;
			}
		}
		
		return getFirstEmptyIndex(inv, side);
	}
	
	public static int getFirstIndexWithItem(IInventory inv, ItemStack filter, ForgeDirection side, boolean size, boolean nbt)
	{
		if(inv == null || filter == null) return -1;
		int slots[] = getAllSlots(inv, side);
		for(int i = 0; i < slots.length; i++)
		{
			ItemStack is1 = inv.getStackInSlot(slots[i]);
			if(is1 != null && itemsEquals(filter, is1, size, nbt)) return i;
		}
		
		return -1;
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
				else if(itemsEquals(filter, is1, false, true)) return i;
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
			else if(itemsEquals(is, is1, false, true))
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
	
	public static ItemStack decrStackSize(IInventory inv, int slot, int amt)
	{
	    ItemStack stack = inv.getStackInSlot(slot);
	    if (stack != null)
	    {
		    if (stack.stackSize <= amt)
		    inv.setInventorySlotContents(slot, null);
		    else
		    {
			    stack = stack.splitStack(amt);
			    if (stack.stackSize == 0)
			    inv.setInventorySlotContents(slot, null);
		    }
	    }
	    
	    return stack;
    }
	
	public static ItemStack getStackInSlotOnClosing(IInventory inv, int i)
	{
		ItemStack is = inv.getStackInSlot(i);
		
		if(is != null)
		{
			inv.setInventorySlotContents(i, null);
			return is;
		}
		
		return null;
	}
	
	public static void dropAllItems(World w, double x, double y, double z, ItemStack[] items)
	{
		if(w.isRemote || items == null || items.length == 0) return;
		
		for(int i = 0; i < items.length; i++)
		{
			if(items[i] != null && items[i].stackSize > 0)
				LMUtils.dropItem(w, x, y, z, items[i], 10);
		}
	}

	public static boolean canStack(ItemStack is1, ItemStack is2)
	{
		if(is1 == null || is2 == null) return false;
		return (is1.stackSize + is2.stackSize <= is1.getMaxStackSize()
		&& is1.stackSize + is2.stackSize <= is2.getMaxStackSize());
	}

	public static ItemStack[] getAllItems(IInventory inv)
	{
		if(inv == null) return null;
		ItemStack[] ai = new ItemStack[inv.getSizeInventory()];
		if(ai.length == 0) return ai;
		for(int i = 0; i < ai.length; i++)
			ai[i] = inv.getStackInSlot(i);
		return ai;
	}
}