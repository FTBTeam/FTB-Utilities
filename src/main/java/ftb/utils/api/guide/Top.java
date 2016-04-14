package ftb.utils.api.guide;

import ftb.lib.api.*;
import ftb.utils.world.LMPlayerServer;
import latmod.lib.*;
import latmod.lib.util.FinalIDObject;

import java.util.*;

public abstract class Top extends FinalIDObject implements Comparator<LMPlayerServer>
{
	public static final LangKey langTopTitle = new LangKey("ftbu.top.title");
	static final Map<String, Top> registry = new HashMap<>();
	
	public static void init()
	{
		add(new Top("first_joined")
		{
			public int compare(LMPlayerServer o1, LMPlayerServer o2)
			{ return Long.compare(o1.stats.firstJoined, o2.stats.firstJoined); }
			
			public Object getData(LMPlayerServer p)
			{ return LMStringUtils.getTimeString(LMUtils.millis() - p.stats.firstJoined); }
		});
		
		add(new Top("deaths")
		{
			public int compare(LMPlayerServer o1, LMPlayerServer o2)
			{ return Integer.compare(o2.stats.deaths, o1.stats.deaths); }
			
			public Object getData(LMPlayerServer p)
			{ return Integer.toString(p.stats.deaths); }
		});
		
		add(new Top("deaths_per_hour")
		{
			public int compare(LMPlayerServer o1, LMPlayerServer o2)
			{ return Double.compare(o2.stats.getDeathsPerHour(), o1.stats.getDeathsPerHour()); }
			
			public Object getData(LMPlayerServer p)
			{ return MathHelperLM.toSmallDouble(p.stats.getDeathsPerHour()); }
		});
		
		add(new Top("last_seen")
		{
			public int compare(LMPlayerServer o1, LMPlayerServer o2)
			{ return Long.compare(o2.stats.lastSeen, o1.stats.lastSeen); }
			
			public Object getData(LMPlayerServer p)
			{
				if(p.isOnline()) return GuiLang.label_online.chatComponent();
				return LMStringUtils.getTimeString(LMUtils.millis() - p.stats.lastSeen);
			}
		});
		
		add(new Top("time_played")
		{
			public int compare(LMPlayerServer o1, LMPlayerServer o2)
			{ return Long.compare(o2.stats.timePlayed, o1.stats.timePlayed); }
			
			public Object getData(LMPlayerServer p)
			{ return LMStringUtils.getTimeString(p.stats.timePlayed) + " [" + (p.stats.timePlayed / 3600000L) + "h]"; }
		});
	}
	
	public static void add(Top t)
	{ registry.put(t.getID(), t); }
	
	public final LangKey langKey;
	
	public Top(String s)
	{
		super(s);
		langKey = new LangKey("ftbu.top." + getID());
	}
	
	public abstract Object getData(LMPlayerServer p);
}