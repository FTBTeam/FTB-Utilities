package ftb.utils.cmd;

import ftb.lib.BaublesHelper;
import ftb.lib.api.item.LMInvUtils;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

public class InvSeeInventory implements IInventory
{
	public static final int slotMapping[] = {39, 38, 37, 36, -1, 40, 41, 42, 43, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 0, 1, 2, 3, 4, 5, 6, 7, 8,};
	
	public final EntityPlayerMP player;
	public final IInventory invPlayer;
	public final IInventory baubles;
	
	public InvSeeInventory(EntityPlayerMP ep)
	{
		player = ep;
		invPlayer = ep.inventory;
		baubles = BaublesHelper.getBaubles(ep);
	}
	
	public int getSizeInventory()
	{ return 9 * 5; }
	
	public IInventory getInv(int slot)
	{
		if(slot == -1) return null;
		if(slot >= 40) return baubles;
		return invPlayer;
	}
	
	public int getSlot(int slot)
	{ return (slot == -1) ? -1 : (slot % 40); }
	
	public ItemStack getStackInSlot(int i)
	{
		int j = slotMapping[i];
		IInventory inv = getInv(j);
		return (inv == null) ? null : inv.getStackInSlot(getSlot(j));
	}
	
	public ItemStack decrStackSize(int i, int k)
	{
		int j = slotMapping[i];
		IInventory inv = getInv(j);
		return (inv == null) ? null : inv.decrStackSize(getSlot(j), k);
	}
	
	public ItemStack removeStackFromSlot(int i)
	{
		int j = slotMapping[i];
		IInventory inv = getInv(j);
		return (inv == null) ? null : inv.removeStackFromSlot(getSlot(j));
	}
	
	public void setInventorySlotContents(int i, ItemStack is)
	{
		int j = slotMapping[i];
		IInventory inv = getInv(j);
		
		if(inv != null)
		{
			inv.setInventorySlotContents(getSlot(j), is);
			inv.markDirty();
		}
	}
	
	public String getName()
	{ return player.getName(); }
	
	public boolean hasCustomName()
	{ return true; }
	
	@Override
	public ITextComponent getDisplayName()
	{
		return null;
	}
	
	public int getInventoryStackLimit()
	{ return 64; }
	
	public void markDirty()
	{
		invPlayer.markDirty();
		player.openContainer.detectAndSendChanges();
		if(baubles != null) baubles.markDirty();
	}
	
	public boolean isUseableByPlayer(EntityPlayer ep)
	{ return true; }
	
	public void openInventory(EntityPlayer ep)
	{
	}
	
	public void closeInventory(EntityPlayer ep)
	{
	}
	
	public boolean isItemValidForSlot(int i, ItemStack is)
	{
		int j = slotMapping[i];
		IInventory inv = getInv(j);
		return inv != null && inv.isItemValidForSlot(getSlot(j), is);
	}
	
	public int getField(int id)
	{ return 0; }
	
	public void setField(int id, int value)
	{
	}
	
	public int getFieldCount()
	{ return 0; }
	
	public void clear()
	{
		LMInvUtils.clear(invPlayer);
		LMInvUtils.clear(baubles);
	}
}