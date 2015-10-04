package latmod.ftbu.api.config;

import latmod.core.util.PrimitiveType;

public class ConfigEntryFloat extends ConfigEntry
{
	private float value;
	private final FloatBounds bounds;
	private boolean valueSet = false;
	
	public ConfigEntryFloat(String id, FloatBounds b)
	{
		super(id, PrimitiveType.FLOAT);
		bounds = (b == null) ? new FloatBounds(0F) : b;
	}
	
	public void set(float v)
	{ value = v; valueSet = true; }
	
	public float get()
	{ return valueSet ? bounds.getVal(value) : bounds.defValue; }
	
	void setJson(Object o)
	{ set(((Float)o).floatValue()); }
	
	Object getJson()
	{ return Float.valueOf(get()); }
}