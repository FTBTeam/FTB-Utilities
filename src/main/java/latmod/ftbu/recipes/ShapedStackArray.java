package latmod.ftbu.recipes;
import net.minecraft.item.ItemStack;

public class ShapedStackArray implements IStackArray
{
	public StackArray[] items;
	
	public ShapedStackArray(StackArray... se)
	{
		items = se;
	}
	
	public ShapedStackArray(Object... o)
	{ this(StackArray.convert(o)); }
	
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
	
	public StackArray[] getItems()
	{ return items; }
}