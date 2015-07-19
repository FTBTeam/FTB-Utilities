package latmod.ftbu.core.util;

import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.*;

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
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("[ ");
		
		for(int i = 0; i < size(); i++)
		{
			sb.append(keys.get(i));
			sb.append(": ");
			sb.append(values.get(i));
			
			if(i != size() - 1)
			sb.append(", ");
		}
		
		sb.append(" ]");
		return sb.toString();
	}
	
	public int[] toIntArray()
	{
		int size = size();
		if(size == 0) return new int[0];
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
		if(ai.length == 0) return;
		for(int i = 0; i < ai.length / 2; i++)
			put(ai[i * 2 + 0], ai[i * 2 + 1]);
	}
	
	public static IntMap fromIntArrayS(int[] ai)
	{
		IntMap m = new IntMap(ai.length / 2);
		m.fromIntArray(ai); return m;
	}
	
	public FastMap<Integer, Integer> toMap()
	{
		FastMap<Integer, Integer> map = new FastMap<Integer, Integer>();
		for(int i = 0; i < size(); i++)
			map.put(keys.get(i), values.get(i));
		return map;
	}
	
	public boolean isEmpty()
	{ return size() <= 0; }
	
	public static class Serializer implements JsonDeserializer<IntMap>, JsonSerializer<IntMap>
	{
		public JsonElement serialize(IntMap src, Type typeOfSrc, JsonSerializationContext context)
		{
			JsonObject o = new JsonObject();
			for(int i = 0; i < src.size(); i++)
				o.add(Integer.toString(src.keys.get(i)), new JsonPrimitive(src.values.get(i)));
			return o;
		}
		
		public IntMap deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
		{
			if(json.isJsonNull()) return null;
			
			IntMap map = new IntMap();
			JsonObject o = json.getAsJsonObject();
			
			for(Map.Entry<String, JsonElement> e : o.entrySet())
			{
				Integer i = MathHelperLM.decode(e.getKey());
				if(i != null) map.put(i.intValue(), e.getValue().getAsInt());
			}
			
			return map;
		}
	}
}