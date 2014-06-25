package latmod.core.base.recipes;
import java.util.Arrays;

public class ShapelessStackArray implements IStackArray
{
	public StackEntry[] items;
	public StackEntry[] itemsSorted;
	
	public ShapelessStackArray(StackEntry... se)
	{
		items = se;
		itemsSorted = items.clone();
		Arrays.sort(itemsSorted, 0, itemsSorted.length, new StackEntry.StackComparator());
	}
	
	public ShapelessStackArray(Object... o)
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
		
		StackEntry[] se1 = se.clone();
		Arrays.sort(se1, 0, se1.length, new StackEntry.StackComparator());
		
		for(int i = 0; i < itemsSorted.length; i++)
		{
			if(!itemsSorted[i].equals(se1[i].item))
				return false;
		}
		
		return true;
	}

	public StackEntry[] getItems()
	{ return items; }
}