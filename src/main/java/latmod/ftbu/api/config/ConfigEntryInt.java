package latmod.ftbu.api.config;

import latmod.core.util.PrimitiveType;

public class ConfigEntryInt extends ConfigEntry
{
	private int value;
	private final IntBounds bounds;
	private boolean valueSet = false;
	
	public ConfigEntryInt(String id, IntBounds b)
	{
		super(id, PrimitiveType.INT);
		bounds = (b == null) ? new IntBounds(0) : b;
	}
	
	public void set(int v)
	{ value = bounds.getVal(v); valueSet = true; }
	
	public int get()
	{ return valueSet ? bounds.getVal(value) : bounds.defValue; }
	
	public void add(int i)
	{ set(get() + i); }
	
	void setJson(Object o)
	{ set(o == null ? bounds.defValue : o.hashCode()); }
	
	Object getJson()
	{ return Integer.valueOf(get()); }
}