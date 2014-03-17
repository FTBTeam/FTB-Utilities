package mods.lm_core;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.world.*;

public class SimpleInv implements IInventory
{
	public final ISimpleInvOwner owner;
	public final String invName;
	public ItemStack[] items;
	public String customName = null;
	public int maxStackSize = 64;
	
	public SimpleInv(ISimpleInvOwner o, String s, int i)
	{
		owner = o;
		invName = s;
		items = new ItemStack[i];
	}
	
	public void setCustomName(String s)
	{ customName = s; }
	
	public void setMaxStackSize(int i)
	{ maxStackSize = i; }
	
	@Override
	public int getSizeInventory()
	{ return items.length; }

	@Override
	public ItemStack getStackInSlot(int i)
	{ return items[i]; }
	
	@Override
	public ItemStack decrStackSize(int i, int j)
	{
		if (items[i] != null)
		{
			if (items[i].stackSize <= j)
			{
				ItemStack is = items[i];
				items[i] = null;
				onInventoryChanged();
				return is;
			}
			else
			{
				ItemStack is = items[i].splitStack(j);
				if (items[i].stackSize == 0) items[i] = null;
				onInventoryChanged();
				return is;
			}
		}
		
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i)
	{
		ItemStack is = items[i];
		items[i] = null; return is;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack is)
	{ items[i] = is; }

	@Override
	public String getInvName()
	{ return isInvNameLocalized() ? customName : invName; }

	@Override
	public boolean isInvNameLocalized()
	{ return customName != null; }

	@Override
	public int getInventoryStackLimit()
	{ return maxStackSize; }

	@Override
	public void onInventoryChanged()
	{ if(owner != null) owner.onInventoryChanged(); }

	@Override
	public boolean isUseableByPlayer(EntityPlayer ep)
	{ return owner.isUseableByPlayer(ep); }

	@Override
	public boolean isItemValidForSlot(int i, ItemStack is)
	{ return owner.isItemValidForSlot(i, is); }
	
	@Override
	public void openChest()
	{
	}

	@Override
	public void closeChest()
	{
	}
	
	public int getTotalItemCount()
	{
		int itemCount = 0;
		
		for(int i = 0; i < items.length; i++)
		if(items[i] != null && items[i].stackSize > 0) itemCount += items[i].stackSize;
		
		return itemCount;
	}
	
	public int getTotalSlotsUsed()
	{
		int itemCount = 0;
		
		for(int i = 0; i < items.length; i++)
		if(items[i] != null && items[i].stackSize > 0) itemCount++;
		
		return itemCount;
	}
	
	public boolean hasItems()
	{
		for(int i = 0; i < items.length; i++)
		if(items[i] != null && items[i].stackSize > 0) return true;
		return false;
	}
}