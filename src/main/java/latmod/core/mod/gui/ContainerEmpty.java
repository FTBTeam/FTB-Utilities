package latmod.core.mod.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class ContainerEmpty extends ContainerLM
{
	public ContainerEmpty(EntityPlayer ep, IInventory inv)
	{ super(ep, inv); }
	
	public ItemStack transferStackInSlot(EntityPlayer ep, int i)
	{ return null; }
}