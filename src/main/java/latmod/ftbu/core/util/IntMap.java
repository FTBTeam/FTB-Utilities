package latmod.ftbu.core.util;


public class IntMap
{
	private final int init;
	private int defVal;
	public final IntList keys;
	public final IntList values;
	
	public IntMap(int i)
	{
		init = i;
		keys = new IntList(init);
		values = new IntList(init);
		setDefVal(-1);
	}
	
	public IntMap()
	{ this(10); }
	
	public void setDefVal(int i)
	{ defVal = i; keys.setDefVal(i); values.setDefVal(i); }
	
	public int size()
	{ return keys.size(); }
	
	public int indexOf(int key)
	{ return keys.indexOf(key); }
	
	public int get(int key)
	{ return values.get(indexOf(key)); }
	
	public void put(int key, int value)
	{
		int i = indexOf(key);
		if(i != -1) values.set(i, value);
		else { keys.add(key); values.add(value); }
	}
	
	public void clear()
	{ keys.clear(); values.clear(); }
	
	public IntMap clone()
	{
		IntMap m = new IntMap(Math.max(init, size()));
		m.keys.addAll(keys);
		m.values.addAll(values);
		m.setDefVal(defVal);
		return m;
	}
	
	public int[] toIntArray()
	{
		int size = size();
		int ai[] = new int[size * 2];
		for(int i = 0; i < size; i++)
		{
			ai[i * 2 + 0] = keys.get(i);
			ai[i * 2 + 1] = values.get(i);
		}
		return ai;
	}
	
	public void fromIntArray(int[] ai)
	{
		clear();
		for(int i = 0; i < ai.length / 2; i++)
			put(ai[i * 2 + 0], ai[i * 2 + 1]);
	}
	
	public static IntMap fromIntArrayS(int[] ai)
	{
		IntMap m = new IntMap(ai.length / 2);
		m.fromIntArray(ai); return m;
	}
}