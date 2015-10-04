package latmod.ftbu.api.config;

import latmod.core.util.PrimitiveType;

public class ConfigEntryStringArray extends ConfigEntry
{
	private String[] value;
	private final String[] defValue;
	
	public ConfigEntryStringArray(String id, String[] def)
	{
		super(id, PrimitiveType.STRING_ARRAY);
		defValue = def == null ? new String[0] : def;
	}
	
	public void set(String[] o)
	{ value = o; }
	
	public String[] get()
	{ return value == null ? defValue : value; }
	
	void setJson(Object o)
	{ set((String[])o); }
	
	Object getJson()
	{ return get(); }
}