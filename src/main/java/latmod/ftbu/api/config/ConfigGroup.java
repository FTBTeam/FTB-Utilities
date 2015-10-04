package latmod.ftbu.api.config;

import latmod.core.util.FastList;

public final class ConfigGroup
{
	public final String ID;
	final FastList<ConfigEntry> config;
	
	public ConfigFile parentFile = null;
	
	public ConfigGroup(String s)
	{
		ID = s;
		config = new FastList<ConfigEntry>();
	}
	
	public void add(ConfigEntry e)
	{
		if(e != null && !config.contains(e))
		{ config.add(e); e.group = this; }
	}
}