package latmod.ftbu.core.util;

import java.util.Arrays;

public class IntList // Improve this // FastList
{
	private final int init;
	private int defVal = 0;
	private int array[];
	private int size;
	
	public IntList(int i)
	{ init = i; array = new int[init]; }
	
	public IntList()
	{ this(0); }
	
	public int size()
	{ return size; }
	
	public void clear()
	{ size = 0; array = new int[init]; }
	
	public void setDefVal(int value)
	{ defVal = value; }
	
	public void add(int value)
	{
		int array1[] = new int[size + 1];
		for(int i = 0; i < size; i++)
			array1[i] = array[i];
		array1[array1.length - 1] = value;
		array = array1;
		size++;
	}
	
	public void addAll(int... values)
	{
		if(values.length <= 0) return;
		int array1[] = new int[size + values.length];
		for(int i = 0; i < size; i++)
			array1[i] = array[i];
		for(int i = 0; i < values.length; i++)
			array1[array1.length - values.length + i] = values[i];
		array = array1;
		size += values.length;
	}
	
	public void addAll(IntList l)
	{ if(l.size > 0) addAll(l.toArray()); }
	
	public void setAll(int... values)
	{ clear(); addAll(values); }
	
	public int get(int key)
	{ return (key >= 0 && key < size()) ? array[key] : defVal; }
	
	public int indexOf(int value)
	{
		for(int i = 0; i < size(); i++)
			if(array[i] == value) return i;
		return -1;
	}
	
	public boolean contains(int value)
	{ return indexOf(value) != -1; }
	
	public int remove(int key)
	{
		if(key < 0 || key >= size) return defVal;
		int rem = get(key);
		size--;
		for(int j = key; j < size; j++)
		array[j] = array[j + 1];
		return rem;
	}
	
	public int removeValue(int value)
	{ return remove(indexOf(value)); }
	
	public boolean isEmpty()
	{ return size <= 0; }
	
	public int[] toArray()
	{
		if(size <= 0) return new int[0];
		int ai[] = new int[size];
		for(int i = 0; i < size; i++)
			ai[i] = array[i];
		return ai;
	}
	
	public void sort()
	{
		int[] a = toArray();
		Arrays.sort(a);
		clear();
		addAll(a);
	}

	public IntList copy()
	{
		IntList l = new IntList(init);
		l.defVal = defVal;
		l.array = toArray();
		l.size = size;
		return l;
	}
	
	public int hashCode()
	{
		int h = 0;
		for(int i = 0; i < size; i++)
			h = h * 31 + array[i];
		return h;
	}
	
	public String toString()
	{
		String s = "[ ";
		
		for(int i = 0; i < size; i++)
		{
			s += array[i];
			if(i != size - 1)
				s += ", ";
		}
		
		return s + " ]";
	}
}