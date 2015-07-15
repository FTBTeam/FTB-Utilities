package latmod.ftbu.core.gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;

public abstract class ContainerLM extends Container
{
	public final EntityPlayer player;
	public final Object inv;
	public final IInventory iinv;
	
	public ContainerLM(EntityPlayer ep, Object i)
	{
		player = ep;
		inv = i;
		iinv = (inv !=  null && inv instanceof IInventory) ? (IInventory)inv : null;
	}
	
	public ItemStack transferStackInSlot(EntityPlayer ep, int i)
	{
		if(iinv == null) return null;
		
		ItemStack is = null;
		Slot slot = (Slot)inventorySlots.get(i);
		
		if(slot != null && slot.getHasStack())
		{
			ItemStack is1 = slot.getStack();
			is = is1.copy();

			if (i < iinv.getSizeInventory())
			{
				if (!mergeItemStack(is1, iinv.getSizeInventory(), inventorySlots.size(), true))
					return null;
			}
			else if (!mergeItemStack(is1, 0, iinv.getSizeInventory(), false))
				return null;

			if (is1.stackSize == 0)
				slot.putStack(null);
			else
				slot.onSlotChanged();
		}

		return is;
	}
	
	public Slot getSlot(int i)
	{
		if(i < 0 || i >= inventorySlots.size()) return null;
		return super.getSlot(i);
	}
	
	public void addPlayerSlots(int posX, int posY)
	{ addPlayerSlots(posX, posY, false); }
	
	public void addPlayerSlots(int posX, int posY, boolean ignoreCurrent)
	{
		if(player == null || player.inventory == null) return;
		
		for(int y = 0; y < 3; y++) for(int x = 0; x < 9; x++)
			addSlotToContainer(new Slot(player.inventory, x + y * 9 + 9, posX + x * 18, posY + y * 18));
		
		int i = ignoreCurrent ? player.inventory.currentItem : -1;
		
		for(int x = 0; x < 9; x++) if(x != i)
			addSlotToContainer(new Slot(player.inventory, x, posX + x * 18, posY + 58));
	}
	
	public void addPlayerSlots(int posY)
	{ addPlayerSlots(8, posY); }
	
	public boolean canInteractWith(EntityPlayer ep)
	{ return true; }
	
	public void onContainerClosed(EntityPlayer ep)
	{
		super.onContainerClosed(ep);
		iinv.closeInventory();
    }
}