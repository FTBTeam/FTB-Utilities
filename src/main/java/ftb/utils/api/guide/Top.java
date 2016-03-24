package ftb.utils.api.guide;

import ftb.lib.mod.FTBLibMod;
import ftb.utils.mod.FTBU;
import ftb.utils.world.LMPlayerServer;
import latmod.lib.*;
import net.minecraft.util.IChatComponent;

import java.util.Comparator;

public abstract class Top implements Comparator<LMPlayerServer>
{
	public final String ID;
	public final IChatComponent title;
	
	public Top(String s)
	{
		ID = s;
		title = FTBU.mod.chatComponent("top." + s);
	}
	
	public abstract Object getData(LMPlayerServer p);
	
	// tops //
	
	public static final Top first_joined = new Top("first_joined")
	{
		public int compare(LMPlayerServer o1, LMPlayerServer o2)
		{ return Long.compare(o1.stats.firstJoined, o2.stats.firstJoined); }
		
		public Object getData(LMPlayerServer p)
		{ return LMStringUtils.getTimeString(LMUtils.millis() - p.stats.firstJoined); }
	};
	
	public static final Top deaths = new Top("deaths")
	{
		public int compare(LMPlayerServer o1, LMPlayerServer o2)
		{ return Integer.compare(o2.stats.deaths, o1.stats.deaths); }
		
		public Object getData(LMPlayerServer p)
		{ return Integer.toString(p.stats.deaths); }
	};
	
	public static final Top deaths_ph = new Top("deaths_ph")
	{
		public int compare(LMPlayerServer o1, LMPlayerServer o2)
		{ return Double.compare(o2.stats.getDeathsPerHour(), o1.stats.getDeathsPerHour()); }
		
		public Object getData(LMPlayerServer p)
		{ return MathHelperLM.toSmallDouble(p.stats.getDeathsPerHour()); }
	};
	
	public static final Top last_seen = new Top("last_seen")
	{
		public int compare(LMPlayerServer o1, LMPlayerServer o2)
		{ return Long.compare(o2.stats.lastSeen, o1.stats.lastSeen); }
		
		public Object getData(LMPlayerServer p)
		{
			if(p.isOnline()) return FTBLibMod.mod.chatComponent("label.online");
			return LMStringUtils.getTimeString(LMUtils.millis() - p.stats.lastSeen);
		}
	};
	
	public static final Top time_played = new Top("time_played")
	{
		public int compare(LMPlayerServer o1, LMPlayerServer o2)
		{ return Long.compare(o2.stats.timePlayed, o1.stats.timePlayed); }
		
		public Object getData(LMPlayerServer p)
		{ return LMStringUtils.getTimeString(p.stats.timePlayed) + " [" + (p.stats.timePlayed / 3600000L) + "h]"; }
	};
}