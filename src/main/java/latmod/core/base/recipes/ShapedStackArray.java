package latmod.core.base.recipes;

public class ShapedStackArray implements IStackArray
{
	public StackEntry[] items;
	
	public ShapedStackArray(StackEntry... se)
	{
		items = se;
	}
	
	public ShapedStackArray(Object... o)
	{ this(StackEntry.convert(o)); }
	
	public boolean equals(Object o)
	{
		if(o == null || !(o instanceof StackEntry[])) return false;
		if(this == o) return true;
		return equalsArray((StackEntry[])o);
	}
	
	public boolean equalsArray(StackEntry... se)
	{
		if(items == null || se == null) return false;
		
		if(items.length != se.length) return false;
		
		for(int i = 0; i < items.length; i++)
		{
			if(!items[i].equals(se[i].item))
				return false;
		}
		
		return true;
	}
	
	public StackEntry[] getItems()
	{ return items; }
}