package latmod.core.util;
import java.util.*;

/** Made by LatvianModder */
public class FastMap<K, V> implements Iterable<V>
{
	public FastList<K> keys;
	public FastList<V> values;
	private boolean nullRemoves = true;
	
	public FastMap(int init, int inc)
	{
		keys = new FastList<K>(init, inc);
		values = new FastList<V>(init, inc);
	}
	
	public FastMap<K, V> setNullRemoves(boolean b)
	{ nullRemoves = b; return this; }
	
	public FastMap(int init)
	{ this(init, 5); }
	
	public FastMap()
	{ this(10); }
	
	public int size()
	{ return values.size(); }
	
	public V get(Object o)
	{ int i = keys.indexOf(o);
	return (i == -1) ? null : values.get(i); }
	
	public K getKey(Object o)
	{ int i = values.indexOf(o);
	return (i == -1) ? null : keys.get(i); }
	
	public boolean put(K k, V v)
	{
		int i = keys.indexOf(k);
		if(i != -1)
		{
			if(nullRemoves && v == null)
			{
				keys.remove(i);
				values.remove(i);
			}
			else
			{
				keys.set(i, k);
				values.set(i, v);
			}
			
			return false;
		}
		else
		{
			if(nullRemoves && v == null)
				return false;
			
			keys.add(k);
			values.add(v);
			return true;
		}
	}
	
	public boolean remove(K k)
	{
		int i = keys.indexOf(k);
		if(i != -1)
		{
			keys.remove(i);
			values.remove(i);
			return true;
		}
		
		return false;
	}
	
	public boolean removeValue(V v)
	{
		int i = values.indexOf(v);
		if(i != -1)
		{
			keys.remove(i);
			values.remove(i);
			return true;
		}
		
		return false;
	}
	
	public boolean clear()
	{
		boolean b = hasKeys();
		keys.clear();
		values.clear();
		return b;
	}
	
	public FastMap<K, V> clone()
	{
		FastMap<K, V> map1 = new FastMap<K, V>();
		map1.keys = keys.clone();
		map1.values = values.clone();
		return map1;
	}
	
	public void removeAllKeys(FastList<? extends K> al)
	{
		for(int i = 0; i < al.size(); i++)
		remove(al.get(i));
	}
	
	public void removeAllValues(FastList<? extends V> al)
	{
		for(int i = 0; i < al.size(); i++)
		removeValue(al.get(i));
	}
	
	public boolean isEmpty()
	{ return keys.isEmpty(); }
	
	public boolean hasKeys()
	{ return !keys.isEmpty(); }

	public void putAll(FastMap<K, V> map)
	{
		Iterator<K> itrK = map.keys.iterator();
		Iterator<V> itrV = map.values.iterator();
		while(itrK.hasNext() && itrV.hasNext())
		put(itrK.next(), itrV.next());
	}
	
	public void putAll(Map<K, V> map)
	{
		Iterator<K> itrK = map.keySet().iterator();
		Iterator<V> itrV = map.values().iterator();
		while(itrK.hasNext() && itrV.hasNext())
		put(itrK.next(), itrV.next());
	}

	public Iterator<V> iterator()
	{ return values.iterator(); }
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Map { ");
		
		for(int i = 0; i < size(); i++)
		{
			sb.append(keys.get(i));
			sb.append(": ");
			sb.append(values.get(i));
			
			if(i != size() - 1)
			sb.append(", ");
		}
		
		sb.append(" }");
		return sb.toString();
	}
	
	public FastMap<V, K> inverse()
	{
		FastMap<V, K> map = new FastMap<V, K>();
		map.keys.addAll(values);
		map.values.addAll(keys);
		return map;
	}
	
	public FastList<TwoObjects<K, V>> toTwoObjects()
	{
		FastList<TwoObjects<K, V>> l = new FastList<TwoObjects<K, V>>();
		for(int i = 0; i < size(); i++)
			l.add(new TwoObjects<K, V>(keys.get(i), values.get(i)));
		return l;
	}
}