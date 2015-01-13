package latmod.core;

public class EArray<E>
{
	public final FastList<E> array;
	
	public EArray(FastList<E> a)
	{ array = a; }
	
	@SuppressWarnings("all")
	public boolean equals(Object o)
	{ return (o instanceof EArray) ? equalsEArray((EArray)o) : false; }

	private boolean equalsEArray(EArray<E> o)
	{
		if(o == null) return false;
		if(this == o) return true;
		if(array == o.array) return true;
		if(array == null && o.array == null) return true;
		if(array != null && o.array == null) return false;
		if(array == null && o.array != null) return false;
		if(array.size() != o.array.size()) return false;
		
		for(int i = 0; i < array.size(); i++)
		{
			if(!array.get(i).equals(o.array.get(i)))
				return false;
		}
		
		return true;
	}
}