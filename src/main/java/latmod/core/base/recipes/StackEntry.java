package latmod.core.base.recipes;
import latmod.core.*;
import net.minecraft.block.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraftforge.common.util.ForgeDirection;

public class StackEntry implements IStackArray
{
	public final Object item;
	
	private String oreName;
	private ItemStack[] items;
	private int hashCode;
	
	private StackEntry[] array;
	
	public StackEntry(Object o)
	{
		item = o;
		items = getItems(o);
		hashCode = toString().hashCode();
		
		array = new StackEntry[] { this };
	}
	
	public String toString()
	{
		String s = "ore@" + oreName; if(oreName == null)
		s = "item@" + items[0].getUnlocalizedName() + "@" + items[0].getItemDamage();
		return s;
	}
	
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
	
	public static StackEntry[] convert(ItemStack... o)
	{
		if(o == null) return null;
		StackEntry[] se = new StackEntry[o.length];
		for(int i = 0; i < o.length; i++)
			se[i] = (o[i] == null) ? null : new StackEntry(o[i]);
		return se;
	}
	
	public static StackEntry[] convert(Object... o)
	{
		if(o == null) return null;
		StackEntry[] se = new StackEntry[o.length];
		for(int i = 0; i < o.length; i++)
			se[i] = (o[i] == null) ? null : new StackEntry(o[i]);
		return se;
	}
	
	public static StackEntry[] convertInv(IInventory inv, ForgeDirection side)
	{
		if(inv == null || side == null) return null;
		return convert(InvUtils.getAllItems(inv, side));
	}
	
	public static ItemStack[] getItems(Object o)
	{
		ItemStack[] nullStacks = new ItemStack[0];
		
		if(o == null) return nullStacks;
		else if(o instanceof ItemStack) return new ItemStack[] { (ItemStack)o };
		else if(o instanceof ItemStack[]) return (ItemStack[])o;
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
			return dmg1 == dmg2 || dmg2 == LatCore.ANY;// || dmg1 == LatCore.ANY;
		}
				
		return false;
	}
	
	public boolean matches(ItemStack[] ai)
	{ return ai != null && ai.length == 1 && equalsItem(ai[0]); }
	
	public StackEntry[] getItems()
	{ return array; }
}