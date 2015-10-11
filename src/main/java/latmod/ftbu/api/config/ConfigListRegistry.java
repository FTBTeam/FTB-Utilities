package latmod.ftbu.api.config;

import latmod.lib.FastList;
import latmod.lib.config.*;

public class ConfigListRegistry
{
	static final FastList<ConfigList> list = new FastList<ConfigList>();
	
	public static void add(ConfigList e)
	{ if(e != null && !list.contains(e)) list.add(e); }
	
	public static void add(ConfigFile e)
	{ if(e != null) add(e.configList); }
	
	public static void reloadAll()
	{
		for(int i = 0; i < list.size(); i++)
		{
			ConfigList l = list.get(i);
			if(l.parentFile != null)
				l.parentFile.load();
		}
	}
}