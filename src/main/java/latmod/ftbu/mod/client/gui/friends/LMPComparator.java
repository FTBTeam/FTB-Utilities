package latmod.ftbu.mod.client.gui.friends;

import java.util.Comparator;

import latmod.ftbu.core.util.MathHelperLM;
import latmod.ftbu.core.world.*;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public enum LMPComparator implements Comparator<LMPlayerClient>
{
	FRIENDS_STATUS("friends_status", true)
	{
		public int compare(LMPlayerClient o1, LMPlayerClient o2)
		{
			int i = FriendStatus.compare(LMWorldClient.inst.clientPlayer, o1, o2);
			if(i == 0) return ONLINE_STATUS.compare(o1, o2);
			return i;
		}
	},
	
	NAME("name", true)
	{
		public int compare(LMPlayerClient o1, LMPlayerClient o2)
		{ return o1.getName().compareToIgnoreCase(o2.getName()); }
	},
	
	ONLINE_STATUS("online_status", false)
	{
		public int compare(LMPlayerClient o1, LMPlayerClient o2)
		{
			boolean on0 = o1.isOnline();
			boolean on1 = o2.isOnline();
			
			if(on0 && !on1) return -1;
			if(!on0 && on1) return 1;
			
			return NAME.compare(o1, o2);
		}
	},
	
	DEATHS("deaths", true)
	{
		public int compare(LMPlayerClient o1, LMPlayerClient o2)
		{
			int i = Integer.compare(o2.deaths, o1.deaths);
			if(i == 0) return NAME.compare(o1, o2);
			return i;
		}
	},
	
	DATE_JOINED("date_joined", true)
	{
		public int compare(LMPlayerClient o1, LMPlayerClient o2)
		{
			if(o1.firstJoined == 0L) return 1;
			int i = Long.compare(o1.firstJoined, o2.firstJoined);
			if(i == 0) return ONLINE_STATUS.compare(o1, o2);
			return i;
		}
	},
	
	LAST_SEEN("last_seen", true)
	{
		public int compare(LMPlayerClient o1, LMPlayerClient o2)
		{
			if(o1.lastSeen == 0L) return 1;
			int i = Long.compare(o1.lastSeen, o2.lastSeen);
			if(i == 0) return ONLINE_STATUS.compare(o1, o2);
			return i;
		}
	};
	
	public final String ID;
	public final boolean addToList;
	public String translatedName;
	
	LMPComparator(String s, boolean b)
	{ ID = s; addToList = b; }
	
	public LMPComparator next()
	{
		LMPComparator p = this;
		LMPComparator v[] = LMPComparator.values();
		while(!(p = v[MathHelperLM.wrap(p.ordinal() + 1, v.length)]).addToList);
		return p;
	}
	
	public LMPComparator prev()
	{
		LMPComparator p = this;
		LMPComparator v[] = LMPComparator.values();
		while(!(p = v[MathHelperLM.wrap(p.ordinal() - 1, v.length)]).addToList);
		return p;
	}
}