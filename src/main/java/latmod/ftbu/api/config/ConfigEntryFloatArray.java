package latmod.ftbu.api.config;

import latmod.core.util.*;

public class ConfigEntryFloatArray extends ConfigEntry
{
	private float[] value;
	private final float[] defValue;
	
	public ConfigEntryFloatArray(String id, float[] def)
	{
		super(id, PrimitiveType.FLOAT_ARRAY);
		defValue = def == null ? new float[0] : def;
	}
	
	public void set(float[] o)
	{ value = o; }
	
	public float[] get()
	{ return value == null ? defValue : value; }
	
	void setJson(Object o)
	{ set(Converter.toFloats((Float[])o)); }
	
	Object getJson()
	{ return Converter.fromFloats(get()); }
}