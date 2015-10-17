package latmod.ftbu.api.config;

import latmod.ftbu.mod.FTBUFinals;
import latmod.lib.*;
import latmod.lib.config.*;

public class ConfigSyncRegistry
{
	private static final FastList<ConfigList> lists = new FastList<ConfigList>();
	
	public static void add(ConfigEntry e)
	{
		if(e != null && e.parentGroup != null && e.parentGroup.parentList != null)
		{
			ConfigList l = lists.getObj(e.parentGroup.parentList.toString());
			if(l == null)
			{
				l = new ConfigList();
				l.groups = new FastList<ConfigGroup>();
				l.setID(e.parentGroup.parentList.toString());
				lists.add(l);
			}
			
			ConfigGroup g = l.groups.getObj(e.parentGroup.toString());
			if(g == null)
			{
				g = new ConfigGroup(e.parentGroup.toString());
				l.groups.add(g);
			}
			
			g.add(e);
		}
	}
	
	//public static void remove(ConfigEntry e)
	//{ if(e != null) list.removeObj(e); }
	
	public static int writeToIO(ByteIOStream io)
	{
		int count = 0;
		io.writeUShort(lists.size());
		
		for(int i = 0; i < lists.size(); i++)
		{
			ConfigList l = lists.get(i);
			io.writeString(l.toString());
			count += l.writeToIO(io);
		}
		
		if(FTBUFinals.DEV) System.out.println("Sent " + count + " synced config values");
		return count;
	}
	
	public static int readFromIO(ByteIOStream io)
	{
		int count = 0;
		
		int s = io.readUShort();
		
		for(int i = 0; i < s; i++)
		{
			String id = io.readString();
			ConfigList l = ConfigList.readFromIO(io);
			l.setID(id);
			count += l.totalEntryCount();
			
			ConfigList list = ConfigListRegistry.list.getObj(l);
			if(list != null) list.loadFromList(l);
			else new EventSyncedConfig(l).post();
		}
		
		System.out.println("Received " + count + " synced config values");
		return count;
	}
}