package latmod.core.util;

import java.util.*;

public class MapSorter
{
	public static <K extends Comparable<K>, V> Map<K, V> sortMap(Map<K, V> m)
	{
		FastList<ComparableKVPair<K, V>> l = new FastList<ComparableKVPair<K, V>>();
		
		Iterator<K> keys = m.keySet().iterator();
		Iterator<V> values = m.values().iterator();
		
		while(keys.hasNext() && values.hasNext())
			l.add(new ComparableKVPair<K, V>(keys.next(), values.next()));
		
		l.sort(null);
		
		FastMap<K, V> m1 = new FastMap<K, V>();
		
		for(int i = 0; i < l.size(); i++)
		{
			ComparableKVPair<K, V> me = l.get(i);
			m1.put(me.key, me.value);
		}
		
		return m;
	}
	
	public static <K extends Comparable<K>, V> FastMap<K, V> sortMap(FastMap<K, V> m)
	{
		FastList<ComparableKVPair<K, V>> l = new FastList<ComparableKVPair<K, V>>();
		
		for(int i = 0; i < m.size(); i++)
			l.add(new ComparableKVPair<K, V>(m.keys.get(i), m.values.get(i)));
		
		l.sort(null);
		
		FastMap<K, V> m1 = new FastMap<K, V>();
		
		for(int i = 0; i < l.size(); i++)
		{
			ComparableKVPair<K, V> me = l.get(i);
			m1.put(me.key, me.value);
		}
		
		return m1;
	}
	
	public static class ComparableKVPair<K extends Comparable<K>, V> implements Comparable<K>
	{
		public final K key;
		public final V value;
		
		public ComparableKVPair(K k, V v)
		{ key = k; value = v; }
		
		public int compareTo(K o)
		{ return key.compareTo(o); }
	}
}