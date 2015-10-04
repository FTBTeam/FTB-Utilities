package latmod.ftbu.api.config;

import latmod.core.util.FastList;
import net.minecraft.entity.player.EntityPlayerMP;

public class ConfigFileRegistry
{
	private static final FastList<ConfigFile> list = new FastList<ConfigFile>();
	
	public static void add(ConfigFile f)
	{ if(f != null && !list.contains(f)) list.add(f); }
	
	public static ConfigGroup getGroup(String s)
	{
		if(s == null || s.isEmpty()) return null;
		for(ConfigFile f : list) for(ConfigGroup g : f.groups)
			if(g.ID.equals(s)) return g;
		return null;
	}

	public static void syncWithClient(EntityPlayerMP ep)
	{
	}

	public static void reloadAll()
	{
		for(int i = 0; i < list.size(); i++)
			list.get(i).load();
	}
}