package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.lib.util.InvUtils;
import mcjty.lib.compat.CompatInventory;
import mcjty.lib.tools.ItemStackTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

public class InvSeeInventory implements CompatInventory
{
    private static final int slotMapping[] = {39, 38, 37, 36, -1, 40, 41, 42, 43, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 0, 1, 2, 3, 4, 5, 6, 7, 8,};

    private final EntityPlayerMP player;
    private final IInventory invPlayer;
    private final IInventory baubles;

    public InvSeeInventory(EntityPlayerMP ep)
    {
        player = ep;
        invPlayer = ep.inventory;
        baubles = InvUtils.getBaubles(ep);
    }

    @Override
    public int getSizeInventory()
    {
        return 9 * 5;
    }

    @Nullable
    private IInventory getInv(int slot)
    {
        if(slot == -1)
        {
            return null;
        }
        if(slot >= 40)
        {
            return baubles;
        }
        return invPlayer;
    }

    public int getSlot(int slot)
    {
        return (slot == -1) ? -1 : (slot % 40);
    }

    @Override
    public ItemStack getStackInSlot(int i)
    {
        int j = slotMapping[i];
        IInventory inv = getInv(j);
        return (inv == null) ? ItemStackTools.getEmptyStack() : inv.getStackInSlot(getSlot(j));
    }

    @Override
    public ItemStack decrStackSize(int i, int k)
    {
        int j = slotMapping[i];
        IInventory inv = getInv(j);
        return (inv == null) ? ItemStackTools.getEmptyStack() : inv.decrStackSize(getSlot(j), k);
    }

    @Override
    public ItemStack removeStackFromSlot(int i)
    {
        int j = slotMapping[i];
        IInventory inv = getInv(j);
        return (inv == null) ? ItemStackTools.getEmptyStack() : inv.removeStackFromSlot(getSlot(j));
    }

    @Override
    public void setInventorySlotContents(int i, @Nullable ItemStack is)
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
    public String getName()
    {
        return player.getName();
    }

    @Override
    public boolean hasCustomName()
    {
        return player.hasCustomName();
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return player.getDisplayName();
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public void markDirty()
    {
        invPlayer.markDirty();
        player.openContainer.detectAndSendChanges();
        if(baubles != null)
        {
            baubles.markDirty();
        }
    }

    @Override
    public boolean isUsable(EntityPlayer player)
    {
        return true;
    }

    @Override
    public void openInventory(EntityPlayer ep)
    {
    }

    @Override
    public void closeInventory(EntityPlayer ep)
    {
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack is)
    {
        int j = slotMapping[i];
        IInventory inv = getInv(j);
        return inv != null && inv.isItemValidForSlot(getSlot(j), is);
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

        if(baubles != null)
        {
            baubles.clear();
        }
    }
}