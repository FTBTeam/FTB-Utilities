package latmod.ftbu.recipes;

import net.minecraft.item.ItemStack;

import java.util.*;

public class ShapelessStackArray implements IStackArray
{
	public StackArray[] items;
	
	public ShapelessStackArray(StackArray... se)
	{
		items = se;
	}
	
	public ShapelessStackArray(Object... o)
	{ this(StackArray.convert(o)); }
	
	public boolean matches(ItemStack[] ai)
	{
		if(items == null || ai == null) return false;
		if(items.length != ai.length) return false;

		List<StackArray> itemsList = Arrays.asList(items);
		
		for(int i = 0; i < ai.length; i++)
			itemsList.remove(ai[i]);
		
		return itemsList.isEmpty();
	}
	
	public StackArray[] getItems()
	{ return items; }
}