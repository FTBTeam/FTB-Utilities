package latmod.ftbu.core.gui;

import cpw.mods.fml.relauncher.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class ContainerEmpty extends ContainerLM
{
	public ContainerEmpty(EntityPlayer ep, Object inv)
	{ super(ep, inv); }
	
	public ItemStack transferStackInSlot(EntityPlayer ep, int i)
	{ return null; }
	
	public void detectAndSendChanges()
	{
	}
	
	public void onContainerClosed(EntityPlayer ep)
	{
	}
	
	public ItemStack slotClick(int i, int j, int k, EntityPlayer ep)
	{
		return null;
	}

	public void onCraftMatrixChanged(IInventory inv)
	{
	}

	public void putStackInSlot(int i, ItemStack is)
	{
	}

	@SideOnly(Side.CLIENT)
	public void putStacksInSlots(ItemStack[] is)
	{
	}
}