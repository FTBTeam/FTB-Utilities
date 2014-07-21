package latmod.core;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.*;
import net.minecraft.world.*;
import net.minecraftforge.common.util.ForgeDirection;

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
		if(entities) return TileEntityHopper.func_145893_b(w, x, y, z);
		TileEntity te = w.getTileEntity((int)x, (int)y, (int)z);
		return (te != null && te instanceof IInventory) ? (IInventory)te : null;
	}
	
	public static IInventory getInvAt(TileEntity te, ForgeDirection side, boolean entities)
	{
		if(side == null || side == ForgeDirection.UNKNOWN) return null;
		return getInvAt(te.getWorldObj(), te.xCoord + side.offsetX + 0.5D, te.yCoord + side.offsetY + 0.5D, te.zCoord + side.offsetZ + 0.5D, entities);
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
			inv.markDirty();
		}

		return false;
	}
	
	public static boolean addSingleItemToInv(ItemStack is, IInventory inv, int[] slots, int side, boolean doAdd)
	{
		if(is == null) return false;
		ItemStack single = singleCopy(is);
		
		for(int i = 0; i < slots.length; i++)
		{
			ItemStack is1 = inv.getStackInSlot(slots[i]);
			if(is1 != null && is1.stackSize > 0 && InvUtils.itemsEquals(is, is1, false, true))
			{
				if(is1.stackSize + 1 <= is1.getMaxStackSize())
				{
					if(side == -1 || !(inv instanceof ISidedInventory) || ((ISidedInventory)inv).canInsertItem(i, single, side))
					{
						if(doAdd)
						{
							is1.stackSize++;
							inv.setInventorySlotContents(slots[i], is1);
							inv.markDirty();
						}
						
						return true;
					}
				}
			}
		}
		
		for(int i = 0; i < slots.length; i++)
		{
			ItemStack is1 = inv.getStackInSlot(slots[i]);
			if(is1 == null || is1.stackSize == 0)
			{
				if(side == -1 || !(inv instanceof ISidedInventory) || ((ISidedInventory)inv).canInsertItem(i, single, side))
				{
					if(doAdd)
					{
						inv.setInventorySlotContents(slots[i], single);
						inv.markDirty();
					}
					
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
			NBTTagList list = tag.getTagList(s, LatCore.NBT_MAP);
			
			for(int i = 0; i < list.tagCount(); i++)
			{
				NBTTagCompound tag1 = list.getCompoundTagAt(i);
				int slot = tag1.getShort("Slot");
				stacks[slot] = ItemStack.loadItemStackFromNBT(tag1);
				
				if(i >= invSize) break;
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
	
	public static int[] getPlayerSlots(EntityPlayer ep)
	{
		int[] ai = new int[ep.inventory.mainInventory.length];
		for(int i = 0; i < ai.length; i++) ai[i] = i; return ai;
	}
}