package latmod.ftbu.core.recipes;
import latmod.ftbu.core.inv.*;
import latmod.ftbu.core.util.FastList;
import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.*;
import net.minecraftforge.fluids.*;

public class StackEntry implements IStackArray
{
	public final Object item;
	
	public final FastList<ItemStack> items;
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
	{ return "StackEntry: " + items.toString(); }
	
	public int hashCode()
	{ return hashCode; }
	
	public boolean equals(Object o)
	{
		if(o == null) return false;
		if(o == this) return true;
		FastList<ItemStack> items1 = null;
		
		if(o instanceof StackEntry) items1 = ((StackEntry)o).items;
		else items1 = getItems(o);
		
		if(items1 != null) for(int i = 0; i < items1.size(); i++)
		{ if(equalsItem(items1.get(i))) return true; }
		return false;
	}
	
	public boolean equalsItem(ItemStack is)
	{
		if(is == null) return false;
		
		for(int i = 0; i < items.size(); i++)
		{
			if(itemsEquals(items.get(i), is))
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
	
	public static StackEntry[] convertInv(IInventory inv, int side)
	{
		if(inv == null) return null;
		return convert(LMInvUtils.getAllItems(inv, side));
	}
	
	public static FastList<ItemStack> getItems(Object o)
	{
		FastList<ItemStack> list = new FastList<ItemStack>();
		
		if(o == null) return list;
		else if(o instanceof ItemStack) list.add((ItemStack)o);
		else if(o instanceof ItemStack[]) list.addAll((ItemStack[])o);
		else if(o instanceof Item) list.add(new ItemStack((Item)o));
		else if(o instanceof Block) list.add(new ItemStack((Block)o));
		else if(o instanceof String) list = ODItems.getOres((String)o).clone();
		else if(o instanceof FluidStack)
		{
			FluidStack fs = (FluidStack)o;
			FluidContainerRegistry.FluidContainerData[] fd = FluidContainerRegistry.getRegisteredFluidContainerData();
			
			if(fd != null && fd.length > 0)
			for(FluidContainerRegistry.FluidContainerData f : fd)
			{
				if(f.fluid.getFluid() == fs.getFluid() && f.fluid.amount >= fs.amount && f.filledContainer != null)
					list.add(f.filledContainer.copy());
			}
		}
		else if(o instanceof Fluid) return getItems(new FluidStack((Fluid)o, 1000));
		
		return list;
	}
	
	public static boolean itemsEquals(ItemStack is1, ItemStack is2)
	{
		if(is1 == null && is2 == null) return true;
		if(is1 == null || is2 == null) return false;
		
		if(is1.getItem() == is2.getItem())
		{
			int dmg1 = is1.getItemDamage();
			int dmg2 = is1.getItemDamage();
			return dmg1 == dmg2 || dmg2 == ODItems.ANY;// || dmg1 == ODItems.ANY;
		}
				
		return false;
	}
	
	public boolean matches(ItemStack[] ai)
	{ return ai != null && ai.length == 1 && equalsItem(ai[0]); }
	
	public StackEntry[] getItems()
	{ return array; }
	
	public static ItemStack getFrom(Object o)
	{
		if(o instanceof ItemStack) return ((ItemStack)o);
		if(o instanceof Item) return new ItemStack((Item)o);
		if(o instanceof Block) return new ItemStack((Block)o);
		return null;
	}
}