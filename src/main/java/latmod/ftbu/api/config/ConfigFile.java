package latmod.ftbu.api.config;

import java.io.File;

import latmod.core.util.*;

public final class ConfigFile
{
	public final String ID;
	public final File file;
	final FastList<ConfigGroup> groups;
	public final boolean canEdit;
	
	public ConfigFile(String id, File f, boolean edit)
	{
		ID = id;
		file = LMFileUtils.newFile(f);
		groups = new FastList<ConfigGroup>();
		canEdit = edit;
	}
	
	public String toString()
	{ return ID; }
	
	public boolean equals(Object o)
	{ return o != null && (o == this || o.toString().equals(toString())); }
	
	public void add(ConfigGroup g)
	{
		if(g != null && !groups.contains(g))
		{ groups.add(g); g.parentFile = this; }
	}
	
	public void load()
	{
		groups.clear();
		ConfigList l = LMJsonUtils.fromJsonFile(file, ConfigList.class);
		
		if(l != null) groups.addAll(l.list);
		for(ConfigGroup g : groups)
			g.parentFile = this;
	}
	
	public void save()
	{
		ConfigList l = new ConfigList();
		l.list = groups.clone();
		LMJsonUtils.toJsonFile(file, l);
	}
	
	public String toJsonString()
	{
		ConfigList l = new ConfigList();
		l.list = groups.clone();
		return LMJsonUtils.toJson(l);
	}
}