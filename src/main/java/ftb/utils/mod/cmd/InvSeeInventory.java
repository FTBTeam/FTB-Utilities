package ftb.utils.mod.cmd;

import ftb.lib.BaublesHelper;
import ftb.lib.api.item.LMInvUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

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
	
	@Override
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
	
	@Override
	public ItemStack getStackInSlot(int i)
	{
		int j = slotMapping[i];
		IInventory inv = getInv(j);
		return (inv == null) ? null : inv.getStackInSlot(getSlot(j));
	}
	
	@Override
	public ItemStack decrStackSize(int i, int k)
	{
		int j = slotMapping[i];
		IInventory inv = getInv(j);
		return (inv == null) ? null : inv.decrStackSize(getSlot(j), k);
	}
	
	@Override
	public ItemStack getStackInSlotOnClosing(int i)
	{
		int j = slotMapping[i];
		IInventory inv = getInv(j);
		return (inv == null) ? null : inv.getStackInSlotOnClosing(getSlot(j));
	}
	
	@Override
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
	
	@Override
	public String getInventoryName()
	{ return player.getCommandSenderName(); }
	
	@Override
	public boolean hasCustomInventoryName()
	{ return true; }
	
	@Override
	public int getInventoryStackLimit()
	{ return 64; }
	
	@Override
	public void markDirty()
	{
		invPlayer.markDirty();
		player.openContainer.detectAndSendChanges();
		if(baubles != null) baubles.markDirty();
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer ep)
	{ return true; }
	
	@Override
	public void openInventory()
	{
	}
	
	@Override
	public void closeInventory()
	{
	}
	
	@Override
	public boolean isItemValidForSlot(int i, ItemStack is)
	{
		int j = slotMapping[i];
		IInventory inv = getInv(j);
		return inv != null && inv.isItemValidForSlot(getSlot(j), is);
	}
	
	public void clear()
	{
		LMInvUtils.clear(invPlayer);
		LMInvUtils.clear(baubles);
	}
}