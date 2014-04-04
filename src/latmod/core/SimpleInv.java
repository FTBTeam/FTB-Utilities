package latmod.core;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;

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
    public ItemStack decrStackSize(int slot, int amt)
	{
	    ItemStack stack = getStackInSlot(slot);
	    if (stack != null)
	    {
		    if (stack.stackSize <= amt)
		    setInventorySlotContents(slot, null);
		    else
		    {
			    stack = stack.splitStack(amt);
			    if (stack.stackSize == 0)
			    setInventorySlotContents(slot, null);
		    }
	    }
	    
	    return stack;
    }

	@Override
	public ItemStack getStackInSlotOnClosing(int i)
	{
		ItemStack is = items[i];
		items[i] = null; return is;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack stack)
	{
		items[i] = stack;
		if (stack != null && stack.stackSize > getInventoryStackLimit())
		stack.stackSize = getInventoryStackLimit();
	}
	
	@Override
	public String getInventoryName()
	{ return hasCustomInventoryName() ? customName : invName; }
	
	@Override
	public boolean hasCustomInventoryName()
	{ return customName != null; }

	@Override
	public int getInventoryStackLimit()
	{ return maxStackSize; }

	@Override
	public void markDirty()
	{ if(owner != null) owner.markDirty(); }

	@Override
	public boolean isUseableByPlayer(EntityPlayer ep)
	{ return owner.isUseableByPlayer(ep); }

	@Override
	public boolean isItemValidForSlot(int i, ItemStack is)
	{ return owner.isItemValidForSlot(i, is); }
	
	@Override
	public void openInventory()
	{
	}
	
	@Override
	public void closeInventory()
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