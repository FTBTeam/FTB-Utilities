package latmod.ftbu.recipes;
import latmod.ftbu.inv.*;
import latmod.ftbu.item.MaterialItem;
import latmod.lib.FastList;
import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.*;
import net.minecraftforge.fluids.*;

public class StackArray implements IStackArray
{
	public final FastList<ItemStack> items;
	private int hashCode;
	private IStackArray[] array;
	
	public StackArray(Object o)
	{
		items = getItems(o);
		hashCode = toString().hashCode();
		array = new IStackArray[] { this };
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
		
		if(o instanceof StackArray) items1 = ((StackArray)o).items;
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
	
	public static StackArray[] convert(ItemStack... o)
	{
		if(o == null) return null;
		StackArray[] se = new StackArray[o.length];
		for(int i = 0; i < o.length; i++)
			se[i] = (o[i] == null) ? null : new StackArray(o[i]);
		return se;
	}
	
	public static StackArray[] convert(Object... o)
	{
		if(o == null) return null;
		StackArray[] se = new StackArray[o.length];
		for(int i = 0; i < o.length; i++)
			se[i] = (o[i] == null) ? null : new StackArray(o[i]);
		return se;
	}
	
	public static StackArray[] convertInv(IInventory inv, int side)
	{
		if(inv == null) return null;
		return convert(LMInvUtils.getAllItems(inv, side));
	}
	
	public static FastList<ItemStack> getItems(Object o)
	{
		FastList<ItemStack> list = new FastList<ItemStack>();
		
		if(o == null) return list;
		
		ItemStack item0 = getFrom(o);
		if(item0 != null) list.add(item0);
		else if(o instanceof ItemStack[]) list.addAll((ItemStack[])o);
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
	
	public static ItemStack getFrom(Object o)
	{
		if(o == null) return null;
		else if(o instanceof ItemStack) return ((ItemStack)o);
		else if(o instanceof Item) return new ItemStack((Item)o);
		else if(o instanceof Block) return new ItemStack((Block)o);
		else if(o instanceof MaterialItem) return ((MaterialItem)o).getStack();
		else return null;
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
	
	public IStackArray[] getItems()
	{ return array; }
}