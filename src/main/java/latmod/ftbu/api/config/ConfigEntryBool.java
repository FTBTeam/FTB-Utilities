package latmod.ftbu.api.config;

import latmod.core.util.PrimitiveType;

public class ConfigEntryBool extends ConfigEntry
{
	private boolean value;
	private final boolean defValue;
	private boolean valueSet = false;
	
	public ConfigEntryBool(String id, boolean def)
	{ super(id, PrimitiveType.BOOLEAN); defValue = def; }
	
	public void set(boolean v)
	{ value = v; valueSet = true; }
	
	public boolean get()
	{ return valueSet ? value : defValue; }
	
	void setJson(Object o)
	{ set(((Boolean)o).booleanValue()); }
	
	Object getJson()
	{ return Boolean.valueOf(get()); }
}