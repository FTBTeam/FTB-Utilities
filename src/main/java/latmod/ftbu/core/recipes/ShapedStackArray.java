package latmod.ftbu.core.recipes;
import net.minecraft.item.ItemStack;

public class ShapedStackArray implements IStackArray
{
	public StackEntry[] items;
	
	public ShapedStackArray(StackEntry... se)
	{
		items = se;
	}
	
	public ShapedStackArray(Object... o)
	{ this(StackEntry.convert(o)); }
	
	public boolean matches(ItemStack[] ai)
	{
		if(items == null || ai == null) return false;
		
		if(items.length != ai.length) return false;
		
		for(int i = 0; i < items.length; i++)
		{
			if(!items[i].equalsItem(ai[i]))
				return false;
		}
		
		return true;
	}
	
	public StackEntry[] getItems()
	{ return items; }
}