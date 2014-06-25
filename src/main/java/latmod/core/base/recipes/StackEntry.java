package latmod.core.base.recipes;
import java.util.Comparator;

import latmod.core.*;
import net.minecraft.block.*;
import net.minecraft.item.*;

public class StackEntry implements Comparable<StackEntry>, IStackArray
{
	public final Object item;
	private ItemStack[] items;
	
	private String toString;
	private int hashCode;
	
	public StackEntry(Object o)
	{
		item = o;
		items = getItems(o);
		
		if(o instanceof String) toString = "ore@" + o;
		else toString = "item@" + items[0].getUnlocalizedName() + "@" + items[0].getItemDamage();
		
		hashCode = toString.hashCode();
	}
	
	public String toString()
	{ return toString; }
	
	public int hashCode()
	{ return hashCode; }
	
	public boolean equals(Object o)
	{
		if(o == null) return false;
		if(o == this) return true;
		ItemStack[] items1 = null;
		
		if(o instanceof StackEntry) items1 = ((StackEntry)o).items;
		else items1 = getItems(o);
		
		if(items1 != null) for(int i = 0; i < items1.length; i++)
		{ if(equalsItem(items1[i])) return true; }
		return false;
	}
	
	public boolean equalsItem(ItemStack is)
	{
		if(is == null) return false;
		
		for(int i = 0; i < items.length; i++)
		{
			if(itemsEquals(items[i], is))
				return true;
		}
		
		return false;
	}
	
	public static StackEntry[] convert(Object... o)
	{
		StackEntry[] se = new StackEntry[o.length];
		for(int i = 0; i < o.length; i++)
			se[i] = (o[i] == null) ? null : new StackEntry(o[i]);
		return se;
	}
	
	public static ItemStack[] getItems(Object o)
	{
		ItemStack[] nullStacks = new ItemStack[0];
		
		if(o == null) return nullStacks;
		else if(o instanceof ItemStack) return new ItemStack[] { (ItemStack)o };
		else if(o instanceof Item) return new ItemStack[] { new ItemStack((Item)o) };
		else if(o instanceof Block) return new ItemStack[] { new ItemStack((Block)o) };
		else if(o instanceof String)
		{
			ItemStack[] is = LatCore.getOreDictionary((String)o).toArray(nullStacks);
			if(is != null) return is; else return nullStacks;
		}
		
		return nullStacks;
	}
	
	public static boolean itemsEquals(ItemStack is1, ItemStack is2)
	{
		if(is1 == null && is2 == null) return true;
		if(is1 == null || is2 == null) return false;
		
		if(is1.getItem() == is2.getItem())
		{
			int dmg1 = is1.getItemDamage();
			int dmg2 = is1.getItemDamage();
			return dmg1 == dmg2 || dmg1 == LatCore.ANY || dmg2 == LatCore.ANY;
		}
				
		return false;
	}
	
	public boolean equalsArray(StackEntry... se)
	{ return se != null && se.length == 1 && equals(se[0]); }
	
	public StackEntry[] getItems()
	{ return new StackEntry[] { this }; }
	
	public int compareTo(StackEntry se)
	{ return StackComparator.compareStacks(this, se); }
	
	public static class StackComparator implements Comparator<StackEntry>
	{
		public int compare(StackEntry se1, StackEntry se2)
		{ return compareStacks(se1, se2); }
		
		public static int compareStacks(StackEntry se1, StackEntry se2)
		{ return se1.toString().compareTo(se2.toString()); }
	}
}