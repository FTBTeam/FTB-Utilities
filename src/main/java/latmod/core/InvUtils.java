package latmod.core;
import java.util.Arrays;

import latmod.core.item.Tool;
import latmod.core.util.*;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.*;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

/** Made by LatvianModder */
public class InvUtils
{
	public static ItemStack singleCopy(ItemStack is)
	{
		if(is == null || is.stackSize <= 0) return null;
		ItemStack is1 = is.copy(); is1.stackSize = 1; return is1;
	}
	
	public static IInventory getInvAt(World w, double x, double y, double z, boolean entities)
	{
		if(entities) return TileEntityHopper.func_145893_b(w, x, y, z);
		TileEntity te = w.getTileEntity((int)x, (int)y, (int)z);
		return (te != null && te instanceof IInventory) ? (IInventory)te : null;
	}
	
	public static IInventory getInvAt(TileEntity te, ForgeDirection side, boolean entities)
	{
		if(side == null || side == ForgeDirection.UNKNOWN) return null;
		return getInvAt(te.getWorldObj(), te.xCoord + side.offsetX + 0.5D, te.yCoord + side.offsetY + 0.5D, te.zCoord + side.offsetZ + 0.5D, entities);
	}
	
	public static boolean itemsEquals(ItemStack is1, ItemStack is2, boolean size, boolean nbt)
	{ return is1.getItem() == is2.getItem() && is1.getItemDamage() == is2.getItemDamage() && (nbt ? ItemStack.areItemStackTagsEqual(is1, is2) : true) && (size ? (is1.stackSize == is2.stackSize) : true); }
	
	public static int[] getAllSlots(IInventory inv, int side)
	{
		if(side != -1 && inv instanceof ISidedInventory)
			return ((ISidedInventory)inv).getAccessibleSlotsFromSide(side);
		
		int[] ai = new int[inv.getSizeInventory()];
		for(int i = 0; i < ai.length; i++) ai[i] = i;
		return ai;
	}
	
	public static int getFirstIndexWhereFits(IInventory inv, ItemStack filter, int side)
	{
		if(inv == null) return -1;
		
		if(filter != null)
		{
			int slots[] = getAllSlots(inv, side);
			for(int i = 0; i < slots.length; i++)
			{
				ItemStack is1 = inv.getStackInSlot(slots[i]);
				
				if(is1 != null && is1.stackSize < is1.getMaxStackSize())
				{
					if(filter == null) return i;
					else if(itemsEquals(filter, is1, false, true) && is1.stackSize < is1.getMaxStackSize()) return i;
				}
			}
		}
		
		return getFirstEmptyIndex(inv, side);
	}
	
	public static int getFirstIndexWithItem(IInventory inv, ItemStack filter, int side, boolean size, boolean nbt)
	{
		if(inv == null || filter == null) return -1;
		int slots[] = getAllSlots(inv, side);
		for(int i = 0; i < slots.length; i++)
		{
			ItemStack is1 = inv.getStackInSlot(slots[i]);
			if(is1 != null && itemsEquals(filter, is1, size, nbt)) return i;
		}
		
		return -1;
	}
	
	public static int getFirstFilledIndex(IInventory inv, ItemStack filter, int side)
	{
		if(inv == null) return -1;
		int slots[] = getAllSlots(inv, side);
		for(int i = 0; i < slots.length; i++)
		{
			ItemStack is1 = inv.getStackInSlot(slots[i]);
			
			if(is1 != null)
			{
				if(filter == null) return i;
				else if(itemsEquals(filter, is1, false, true)) return i;
			}
		}
		
		return -1;
	}
	
	public static int getFirstEmptyIndex(IInventory inv, int side)
	{
		if(inv == null) return -1;
		
		int slots[] = getAllSlots(inv, side);
		for(int i = 0; i < slots.length; i++)
		{
			ItemStack is1 = inv.getStackInSlot(slots[i]);
			if(is1 == null || is1.stackSize <= 0) return i;
		}
		
		return -1;
	}
	
	public static boolean reduceItemInInv(IInventory inv, int i)
	{
		if(inv == null || i == -1 || i >= inv.getSizeInventory()) return false;
		ItemStack is = inv.getStackInSlot(i);

		if(is != null)
		{
			is.stackSize--;
			if(is.stackSize <= 0) is = null;
			inv.setInventorySlotContents(i, is);
			inv.markDirty();
		}

		return false;
	}
	
	public static void reduceItemInInv(IInventory inv, int[] slots, ItemStack is, int size)
	{
		for(int i = 0; i < slots.length; i++)
		{
			ItemStack is1 = inv.getStackInSlot(slots[i]);
			
			if(is1 != null && itemsEquals(is1, is, false, true))
			{
				int s = Math.min(is1.stackSize, size);
				
				size -= s;
				is1.stackSize -= s;
				if(is1.stackSize <= 0)
					inv.setInventorySlotContents(i, null);
				if(size <= 0) return;
			}
		}
		
		inv.markDirty();
	}
	
	public static boolean addSingleItemToInv(ItemStack is, IInventory inv, int[] slots, int side, boolean doAdd)
	{
		if(is == null) return false;
		ItemStack single = singleCopy(is);
		
		for(int i = 0; i < slots.length; i++)
		{
			ItemStack is1 = inv.getStackInSlot(slots[i]);
			if(is1 != null && is1.stackSize > 0 && InvUtils.itemsEquals(is, is1, false, true))
			{
				if(is1.stackSize + 1 <= is1.getMaxStackSize())
				{
					if(side == -1 || canInsert(inv, single, i, side))
					{
						if(doAdd)
						{
							is1.stackSize++;
							inv.setInventorySlotContents(slots[i], is1);
							inv.markDirty();
						}
						
						return true;
					}
				}
			}
		}
		
		for(int i = 0; i < slots.length; i++)
		{
			ItemStack is1 = inv.getStackInSlot(slots[i]);
			if(is1 == null || is1.stackSize == 0)
			{
				if(side == -1 || canInsert(inv, single, i, side))
				{
					if(doAdd)
					{
						inv.setInventorySlotContents(slots[i], single);
						inv.markDirty();
					}
					
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static boolean addSingleItemToInv(ItemStack is, IInventory inv, int side, boolean doAdd)
	{ return addSingleItemToInv(is, inv, getAllSlots(inv, side), side, doAdd); }
	
	public static NBTTagCompound removeTags(NBTTagCompound tag, String... tags)
	{
		if(tag == null || tag.hasNoTags()) return null;
		for(int i = 0; i < tags.length; i++)
		tag.removeTag(tags[i]);
		if(tag.hasNoTags()) tag = null; return tag;
	}
	
	public static ItemStack removeTags(ItemStack is, String... tags)
	{ if(is == null) return null; is.setTagCompound(removeTags(is.stackTagCompound, tags)); return is; }
	
	public static void writeItemsToNBT(ItemStack[] stacks, NBTTagCompound tag, String s)
	{
		NBTTagList list = new NBTTagList();
		
		for(int i = 0; i < stacks.length; i++)
		if(stacks[i] != null)
		{
			NBTTagCompound tag1 = new NBTTagCompound();
			tag1.setShort("Slot", (short)i);
			stacks[i].writeToNBT(tag1);
			list.appendTag(tag1);
		}
		
		if(list.tagCount() > 0)
		tag.setTag(s, list);
	}
	
	public static void readItemsFromNBT(ItemStack[] stacks, NBTTagCompound tag, String s)
	{
		Arrays.fill(stacks, null);
		
		if(tag.hasKey(s))
		{
			NBTTagList list = tag.getTagList(s, NBTHelper.MAP);
			
			for(int i = 0; i < list.tagCount(); i++)
			{
				NBTTagCompound tag1 = list.getCompoundTagAt(i);
				int slot = tag1.getShort("Slot");
				stacks[slot] = ItemStack.loadItemStackFromNBT(tag1);
				
				if(i >= stacks.length) break;
			}
		}
	}
	
	public static ItemStack decrStackSize(IInventory inv, int slot, int amt)
	{
	    ItemStack stack = inv.getStackInSlot(slot);
	    if (stack != null)
	    {
		    if (stack.stackSize <= amt)
		    inv.setInventorySlotContents(slot, null);
		    else
		    {
			    stack = stack.splitStack(amt);
			    if (stack.stackSize == 0)
			    inv.setInventorySlotContents(slot, null);
		    }
	    }
	    
	    return stack;
    }
	
	public static ItemStack getStackInSlotOnClosing(IInventory inv, int i)
	{
		ItemStack is = inv.getStackInSlot(i);
		
		if(is != null)
		{
			inv.setInventorySlotContents(i, null);
			return is;
		}
		
		return null;
	}
	
	public static void dropItem(World w, double x, double y, double z, double mx, double my, double mz, ItemStack is, int delay)
	{
		if(w == null || is == null || is.stackSize == 0) return;
		EntityItem ei = new EntityItem(w, x, y, z, is.copy());
		ei.motionX = mx; ei.motionY = my; ei.motionZ = mz;
		ei.delayBeforeCanPickup = delay;
		w.spawnEntityInWorld(ei);
	}
	
	public static void dropItem(World w, double x, double y, double z, ItemStack is, int delay)
	{ dropItem(w, x, y, z, w.rand.nextGaussian() * 0.07F, w.rand.nextFloat() * 0.05F, w.rand.nextGaussian() * 0.07F, is, delay); }
	
	public static void dropItem(Entity e, ItemStack item)
	{
		if(e == null || item == null || item.stackSize <= 0) return;
		dropItem(e.worldObj, e.posX, e.posY, e.posZ, item, 0);
	}
	
	public static void giveItem(EntityPlayer ep, ItemStack item)
	{ giveItem(ep, item, -1); }
	
	public static void giveItem(EntityPlayer ep, ItemStack item, int optionalSlot)
	{
		if(ep == null || ep.inventory == null || item == null || item.stackSize <= 0) return;
		ItemStack is = item.copy();
		boolean changed = false;
		
		int size = is.stackSize;
		for(int i = 0; i < size; i++)
		{
			if(InvUtils.addSingleItemToInv(is, ep.inventory, InvUtils.getPlayerSlots(ep), -1, true))
			{ is.stackSize--; changed = true; }
		}
		
		if(changed)
		{
			ep.inventory.markDirty();
			
			if(ep.openContainer != null)
				ep.openContainer.detectAndSendChanges();
		}
		
		if(is.stackSize > 0) dropItem(ep, is);
	}
	
	public static void dropAllItems(World w, double x, double y, double z, ItemStack[] items)
	{
		if(w.isRemote || items == null || items.length == 0) return;
		
		for(int i = 0; i < items.length; i++)
		{
			if(items[i] != null && items[i].stackSize > 0)
				dropItem(w, x, y, z, items[i], 10);
		}
	}

	public static boolean canStack(ItemStack is1, ItemStack is2)
	{
		if(is1 == null || is2 == null) return false;
		return (is1.stackSize + is2.stackSize <= is1.getMaxStackSize()
		&& is1.stackSize + is2.stackSize <= is2.getMaxStackSize());
	}

	public static ItemStack[] getAllItems(IInventory inv, int side)
	{
		if(inv == null) return null;
		int[] slots = InvUtils.getAllSlots(inv, side);
		ItemStack[] ai = new ItemStack[slots.length];
		if(ai.length == 0) return ai;
		for(int i = 0; i < ai.length; i++)
			ai[i] = inv.getStackInSlot(slots[i]);
		return ai;
	}
	
	public static boolean canExtract(IInventory inv, ItemStack is, int slot, int side)
	{ return !(inv instanceof ISidedInventory) || ((ISidedInventory)inv).canExtractItem(slot, is, side); }
	
	public static boolean canInsert(IInventory inv, ItemStack is, int slot, int side)
	{ return !(inv instanceof ISidedInventory) || ((ISidedInventory)inv).canInsertItem(slot, is, side); }
	
	public static FastMap<Integer, ItemStack> getAllItemsMap(IInventory inv, int side)
	{
		ItemStack[] is = getAllItems(inv, side);
		if(is == null) return null;
		FastMap<Integer, ItemStack> map = new FastMap<Integer, ItemStack>();
		for(int i = 0; i < is.length; i++)
		if(is[i] != null) map.put(i, is[i]);
		return map;
	}
	
	public static int[] getPlayerSlots(EntityPlayer ep)
	{
		int[] ai = new int[ep.inventory.mainInventory.length];
		for(int i = 0; i < ai.length; i++) ai[i] = i; return ai;
	}
	
	public static ItemStack reduceItem(ItemStack is)
	{
		if (is.stackSize == 1)
		{
			if (is.getItem().hasContainerItem(is))
				return is.getItem().getContainerItem(is);
			return null;
		}
		
		is.splitStack(1);
		return is;
	}
	
	public static ItemStack loadStack(NBTTagCompound tag, String s)
	{ if(tag.hasKey(s)) return ItemStack.loadItemStackFromNBT(tag.getCompoundTag(s)); return null; }
	
	public static void saveStack(NBTTagCompound tag, String s, ItemStack is)
	{
		if(is != null)
		{
			NBTTagCompound tag1 = new NBTTagCompound();
			is.writeToNBT(tag1);
			tag.setTag(s, tag1);
		}
	}
	
	public static Item getItemFromRegName(String s)
	{ return (Item)Item.itemRegistry.getObject(s); }
	
	public static String getRegName(Item item)
	{ return Item.itemRegistry.getNameForObject(item); }
	
	public static String getRegName(Block block)
	{ return Block.blockRegistry.getNameForObject(block); }
	
	public static String getRegName(ItemStack is)
	{ return (is != null && is.getItem() != null) ? getRegName(is.getItem()) : null; }
	
	public static ItemStack parseItem(String s)
	{
		try
		{
			String regex = "@";
			if(s.contains(";")) regex = ";";
			else if(s.contains(" x ")) regex = " x ";
			
			String[] s1 = LatCore.split(s, regex);
			if(s1.length <= 0 || s1.length > 3) return null;
			Item item = getItemFromRegName(s1[0]);
			if(item == null) return null;
			int dmg = 0;
			int size = 1;
			if(s1.length == 2) dmg = Integer.parseInt(s1[1]);
			if(s1.length == 3)
			{ size = Integer.parseInt(s1[1]); dmg = Integer.parseInt(s1[2]); }
			return new ItemStack(item, size, dmg);
		}
		catch(Exception e) {}
		return null;
	}
	
	public static boolean isWrench(ItemStack is)
	{ return is != null && is.getItem() != null && is.getItem().getHarvestLevel(is, Tool.Type.WRENCH) >= Tool.Level.BASIC; }
	
	public static FluidStack getFluid(ItemStack is)
	{
		if(is == null || is.getItem() == null) return null;
		
		if(is.getItem() instanceof IFluidContainerItem)
		{
			FluidStack fs = ((IFluidContainerItem)is.getItem()).getFluid(is);
			if(fs != null) return fs;
		}
		
		return FluidContainerRegistry.getFluidForFilledItem(is);
	}
	
	public static boolean isBucket(ItemStack is)
	{ return FluidContainerRegistry.isBucket(is); }
}