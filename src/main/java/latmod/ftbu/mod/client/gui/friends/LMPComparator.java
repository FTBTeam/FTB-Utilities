package latmod.ftbu.mod.client.gui.friends;

import java.util.Comparator;

import latmod.ftbu.core.api.EventLM;
import latmod.ftbu.core.util.FastMap;
import latmod.ftbu.core.world.*;
import net.minecraft.client.resources.I18n;

public abstract class LMPComparator implements Comparator<LMPlayerClient>
{
	static final FastMap<String, LMPComparator> map = new FastMap<String, LMPComparator>();
	
	public static void init()
	{
		map.clear();
		Event e = new Event();
		e.add("friends_status", new ByFriendsStatus());
		e.add("name", new ByName());
		e.add("deaths", new ByDeaths());
		e.add("date_joined", new ByJoined());
		e.add("last_seen", new ByLastSeen());
		e.post();
	}
	
	public String translatedName;
	public int listID;
	
	public static class Event extends EventLM
	{
		public void add(String s, LMPComparator c)
		{
			if(!map.keys.contains(s))
			{
				c.listID = map.size();
				c.translatedName = I18n.format("lmp_comparator." + s);
				map.put(s, c);
			}
		}
	}
	
	public static class ByName extends LMPComparator
	{
		public int compare(LMPlayerClient o1, LMPlayerClient o2)
		{ return o1.getName().compareToIgnoreCase(o2.getName()); }
	}
	
	public static class ByOnlineStatus extends ByName
	{
		public int compare(LMPlayerClient o1, LMPlayerClient o2)
		{
			boolean on0 = o1.isOnline();
			boolean on1 = o2.isOnline();
			
			if(on0 && !on1) return -1;
			if(!on0 && on1) return 1;
			
			return super.compare(o1, o2);
		}
	}
	
	public static class ByFriendsStatus extends ByOnlineStatus
	{
		public int compare(LMPlayerClient o1, LMPlayerClient o2)
		{
			int i = FriendStatus.compare(LMWorldClient.inst.clientPlayer, o1, o2);
			if(i == 0) return super.compare(o1, o2);
			return i;
		}
	}
	
	public static class ByDeaths extends ByName
	{
		public int compare(LMPlayerClient o1, LMPlayerClient o2)
		{
			int i = Integer.compare(o2.deaths, o1.deaths);
			if(i == 0) return super.compare(o1, o2);
			return i;
		}
	}
	
	public static class ByJoined extends ByOnlineStatus
	{
		public int compare(LMPlayerClient o1, LMPlayerClient o2)
		{
			if(o1.firstJoined == 0L) return 1;
			int i = Long.compare(o1.firstJoined, o2.firstJoined);
			if(i == 0) return super.compare(o1, o2);
			return i;
		}
	}
	
	public static class ByLastSeen extends ByOnlineStatus
	{
		public int compare(LMPlayerClient o1, LMPlayerClient o2)
		{
			if(o1.lastSeen == 0L) return 1;
			int i = Long.compare(o1.lastSeen, o2.lastSeen);
			if(i == 0) return super.compare(o1, o2);
			return i;
		}
	}
}