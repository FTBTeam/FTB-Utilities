package latmod.core.mod.recipes;
import net.minecraft.item.ItemStack;
import latmod.core.util.*;

public class ShapelessStackArray implements IStackArray
{
	public StackEntry[] items;
	
	public ShapelessStackArray(StackEntry... se)
	{
		items = se;
	}
	
	public ShapelessStackArray(Object... o)
	{ this(StackEntry.convert(o)); }
	
	public boolean matches(ItemStack[] ai)
	{
		if(items == null || ai == null) return false;
		if(items.length != ai.length) return false;
		
		FastList<StackEntry> itemsList = new FastList<StackEntry>(items);
		
		for(int i = 0; i < ai.length; i++)
			itemsList.remove(ai[i]);
		
		return itemsList.isEmpty();
	}
	
	public StackEntry[] getItems()
	{ return items; }
}