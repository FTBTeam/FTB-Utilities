package com.feed_the_beast.ftbutilities.command;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

public class InvSeeInventory implements IInventory
{
	private final EntityPlayerMP player;
	private final IInventory invPlayer;

	public InvSeeInventory(EntityPlayerMP ep)
	{
		player = ep;
		invPlayer = ep.inventory;
	}

	@Override
	public int getSizeInventory()
	{
		return 45;
	}

	@Override
	public boolean isEmpty()
	{
		return invPlayer.isEmpty();
	}

	public int getSlot(int slot)
	{
		if (slot == 8)
		{
			return 40;
		}
		else if (slot >= 0 && slot <= 3)
		{
			return 39 - slot;
		}
		else if (slot >= 9 && slot <= 35)
		{
			return slot;
		}
		else if (slot >= 36 && slot <= 44)
		{
			return slot - 36;
		}

		return -1;
	}

	@Override
	public ItemStack getStackInSlot(int index)
	{
		int slot = getSlot(index);
		return slot == -1 ? ItemStack.EMPTY : invPlayer.getStackInSlot(slot);
	}

	@Override
	public ItemStack decrStackSize(int index, int count)
	{
		int slot = getSlot(index);
		return slot == -1 ? ItemStack.EMPTY : invPlayer.decrStackSize(slot, count);
	}

	@Override
	public ItemStack removeStackFromSlot(int index)
	{
		int slot = getSlot(index);
		return slot == -1 ? ItemStack.EMPTY : invPlayer.removeStackFromSlot(slot);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack is)
	{
		int slot = getSlot(index);

		if (slot != -1)
		{
			invPlayer.setInventorySlotContents(slot, is);
			markDirty();
		}
	}

	@Override
	public String getName()
	{
		return player.getName();
	}

	@Override
	public boolean hasCustomName()
	{
		return true;
	}

	@Override
	public ITextComponent getDisplayName()
	{
		return player.getDisplayName();
	}

	@Override
	public int getInventoryStackLimit()
	{
		return invPlayer.getInventoryStackLimit();
	}

	@Override
	public void markDirty()
	{
		invPlayer.markDirty();
		player.openContainer.detectAndSendChanges();
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player)
	{
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player)
	{
	}

	@Override
	public void closeInventory(EntityPlayer player)
	{
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack)
	{
		int slot = getSlot(index);
		return slot != -1 && invPlayer.isItemValidForSlot(slot, stack);
	}

	@Override
	public int getField(int id)
	{
		return 0;
	}

	@Override
	public void setField(int id, int value)
	{
	}

	@Override
	public int getFieldCount()
	{
		return 0;
	}

	@Override
	public void clear()
	{
		invPlayer.clear();
	}
}