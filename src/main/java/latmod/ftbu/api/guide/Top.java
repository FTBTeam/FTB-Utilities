package latmod.ftbu.api.guide;

import java.util.Comparator;

import latmod.ftbu.mod.FTBU;
import latmod.ftbu.world.LMPlayerServer;
import latmod.lib.*;
import net.minecraft.util.*;

public abstract class Top implements Comparator<LMPlayerServer>
{
	public final IChatComponent ID;
	
	public Top(String s)
	{ ID = new ChatComponentTranslation(FTBU.mod.assets + "top." + s); }
	
	public abstract String getData(LMPlayerServer p);
	
	// tops //
	
	public static final Top age = new Top("age")
	{
		public int compare(LMPlayerServer o1, LMPlayerServer o2)
		{ return Long.compare(o1.firstJoined, o2.firstJoined); }
		
		public String getData(LMPlayerServer p)
		{ return LMStringUtils.getTimeString(LMUtils.millis() - p.firstJoined); }
	};
	
	public static final Top deaths = new Top("deaths")
	{
		public int compare(LMPlayerServer o1, LMPlayerServer o2)
		{ return Integer.compare(o2.deaths, o1.deaths); }
		
		public String getData(LMPlayerServer p)
		{ return Integer.toString(p.deaths); }
	};
	
	public static final Top deathsPerHour = new Top("deaths_ph")
	{
		public int compare(LMPlayerServer o1, LMPlayerServer o2)
		{ return Double.compare(o2.getDeathsPerHour(), o1.getDeathsPerHour()); }
		
		public String getData(LMPlayerServer p)
		{ return MathHelperLM.toSmallDouble(p.getDeathsPerHour()); }
	};
	
	public static final Top lastSeen = new Top("last_seen")
	{
		public int compare(LMPlayerServer o1, LMPlayerServer o2)
		{ return Long.compare(o2.lastSeen, o1.lastSeen); }
		
		public String getData(LMPlayerServer p)
		{ return LMStringUtils.getTimeString(LMUtils.millis() - p.lastSeen); }
	};
	
	public static final Top timePlayed = new Top("time_played")
	{
		public int compare(LMPlayerServer o1, LMPlayerServer o2)
		{ return Long.compare(o2.timePlayed, o1.timePlayed); }
		
		public String getData(LMPlayerServer p)
		{ return LMStringUtils.getTimeString(p.timePlayed) + " [" + (p.timePlayed / 3600000L) + "h]"; }
	};
}