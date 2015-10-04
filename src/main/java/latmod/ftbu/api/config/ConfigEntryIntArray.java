package latmod.ftbu.api.config;

import latmod.core.util.*;

public class ConfigEntryIntArray extends ConfigEntry
{
	private int[] value;
	private final int[] defValue;
	
	public ConfigEntryIntArray(String id, int[] def)
	{
		super(id, PrimitiveType.INT_ARRAY);
		defValue = def == null ? new int[0] : def;
	}
	
	public void set(int[] o)
	{ value = o; }
	
	public int[] get()
	{ return value == null ? defValue : value; }
	
	void setJson(Object o)
	{ set(Converter.toInts((Integer[])o)); }
	
	Object getJson()
	{ return Converter.fromInts(get()); }
}