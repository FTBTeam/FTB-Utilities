package latmod.ftbu.inv;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class BasicInventory implements IInventory
{
	public final ItemStack[] items;
	
	public BasicInventory(int i)
	{ items = new ItemStack[i]; }
	
	public int getSizeInventory()
	{ return items.length; }
	
	public ItemStack getStackInSlot(int i)
	{ return items[i]; }
	
	public ItemStack decrStackSize(int slot, int amt)
	{ return LMInvUtils.decrStackSize(this, slot, amt); }
	
	public ItemStack getStackInSlotOnClosing(int i)
	{ return LMInvUtils.getStackInSlotOnClosing(this, i); }
	
	public void setInventorySlotContents(int i, ItemStack is)
	{ items[i] = is; markDirty(); }
	
	public String getInventoryName()
	{ return ""; }
	
	public boolean hasCustomInventoryName()
	{ return false; }
	
	public int getInventoryStackLimit()
	{ return 64; }
	
	public void markDirty() { }
	
	public boolean isUseableByPlayer(EntityPlayer ep)
	{ return true; }
	
	public void openInventory() { }
	
	public void closeInventory() { }
	
	public boolean isItemValidForSlot(int i, ItemStack is)
	{ return true; }
}