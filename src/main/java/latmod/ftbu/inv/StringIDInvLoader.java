package latmod.ftbu.inv;

import java.util.Arrays;

import latmod.ftbu.util.LMNBTUtils;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.*;

public class StringIDInvLoader
{
	public static void readItemsFromNBT(ItemStack[] items, NBTTagCompound tag, String s)
	{
		if(items == null || items.length == 0) return;
		Arrays.fill(items, null);
		
		if(tag.hasKey(s))
		{
			NBTTagList list = tag.getTagList(s, LMNBTUtils.MAP);
			
			for(int i = 0; i < list.tagCount(); i++)
			{
				NBTTagCompound tag1 = list.getCompoundTagAt(i);
				Item item = LMInvUtils.getItemFromRegName(tag1.getString("ID"));
		        
		        if(item != null)
		        {
		        	if(tag1.hasKey("S"))
		    		{
		        		int slot = tag1.getShort("S");
		        		
		        		if(slot >= 0 && slot < items.length)
		        		{
		        			int size = tag1.getByte("C");
				        	int dmg = Math.max(0, tag1.getShort("D"));
				        	items[slot] = new ItemStack(item, size, dmg);
				        	if(tag1.hasKey("T")) items[slot].setTagCompound(tag1.getCompoundTag("T"));
		        		}
		    		}
		        	else
		        	{
		        		int[] ai = tag1.getIntArray("D");
		        		
		        		if(ai.length == 3 && ai[0] >= 0 && ai[0] < items.length)
		        		{
		        			items[ai[0]] = new ItemStack(item, ai[1], ai[2]);
				        	if(tag1.hasKey("T", 10)) items[ai[0]].setTagCompound(tag1.getCompoundTag("T"));
		        		}
		        	}
		        }
			}
		}
	}
	
	public static void writeItemsToNBT(ItemStack[] items, NBTTagCompound tag, String s)
	{
		if(items == null || items.length == 0) return;
		NBTTagList list = new NBTTagList();
		
		for(int i = 0; i < items.length; i++)
		{
			if(items[i] != null)
			{
				NBTTagCompound tag1 = new NBTTagCompound();
				tag1.setString("ID", LMInvUtils.getRegName(items[i].getItem()));
				tag1.setIntArray("D", new int[] { i, items[i].stackSize, items[i].getItemDamage() });
		        if(items[i].stackTagCompound != null) tag1.setTag("T", items[i].stackTagCompound);
				list.appendTag(tag1);
			}
			
		}
		
		if(list.tagCount() > 0) tag.setTag(s, list);
	}
	
	public static void readInvFromNBT(IInventory inv, NBTTagCompound tag, String s)
	{
		if(inv == null) return;
		ItemStack[] items = new ItemStack[inv.getSizeInventory()];
		readItemsFromNBT(items, tag, s);
		for(int i = 0; i < items.length; i++)
			inv.setInventorySlotContents(i, items[i]);
		inv.markDirty();
	}
	
	public static void writeInvToNBT(IInventory inv, NBTTagCompound tag, String s)
	{
		if(inv == null) return;
		ItemStack[] items = new ItemStack[inv.getSizeInventory()];
		for(int i = 0; i < items.length; i++)
			items[i] = inv.getStackInSlot(i);
		writeItemsToNBT(items, tag, s);
	}
	
	public static int getSlotsUsed(NBTTagCompound tag, String s)
	{ return tag.hasKey(s) ? tag.getTagList(s, LMNBTUtils.MAP).tagCount() : 0; }
	
	public static int getItemCount(NBTTagCompound tag, String s)
	{
		int count = 0;
		
		if(tag.hasKey(s))
		{
			NBTTagList list = tag.getTagList(s, LMNBTUtils.MAP);
			
			for(int i = 0; i < list.tagCount(); i++)
			{
				NBTTagCompound tag1 = list.getCompoundTagAt(i);
		        
				if(tag1.hasKey("S"))
					count += tag1.getByte("C");
	        	else
	        	{
	        		int[] ai = tag1.getIntArray("D");
	        		if(ai.length == 3) count += ai[1];
	        	}
			}
		}
		
		return count;
	}
}