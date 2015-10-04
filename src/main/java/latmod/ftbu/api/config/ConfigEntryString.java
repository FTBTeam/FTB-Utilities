package latmod.ftbu.api.config;

import latmod.core.util.PrimitiveType;

public class ConfigEntryString extends ConfigEntry
{
	private String value;
	private final String defValue;
	
	public ConfigEntryString(String id, String def)
	{ super(id, PrimitiveType.STRING); defValue = def; }
	
	public void set(String o)
	{ value = o; }
	
	public String get()
	{ return value == null ? defValue : value; }
	
	void setJson(Object o)
	{ set(o.toString()); }
	
	Object getJson()
	{ return get(); }
}