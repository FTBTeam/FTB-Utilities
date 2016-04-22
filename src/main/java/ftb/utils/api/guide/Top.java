package ftb.utils.api.guide;

import ftb.lib.api.*;
import ftb.lib.mod.FTBLibMod;
import latmod.lib.*;

import java.util.Comparator;

public abstract class Top implements Comparator<ForgePlayerMP>
{
	public final String ID;
	public final LangKey langKey;
	
	public Top(String s)
	{
		ID = s;
		langKey = new LangKey("top." + s);
	}
	
	public abstract Object getData(ForgePlayerMP p);
	
	// tops //
	
	public static final Top first_joined = new Top("first_joined")
	{
		@Override
		public int compare(ForgePlayerMP o1, ForgePlayerMP o2)
		{ return Long.compare(o1.stats.firstJoined, o2.stats.firstJoined); }
		
		@Override
		public Object getData(ForgePlayerMP p)
		{ return LMStringUtils.getTimeString(LMUtils.millis() - p.stats.firstJoined); }
	};
	
	public static final Top deaths = new Top("deaths")
	{
		@Override
		public int compare(ForgePlayerMP o1, ForgePlayerMP o2)
		{ return Integer.compare(o2.stats.deaths, o1.stats.deaths); }
		
		@Override
		public Object getData(ForgePlayerMP p)
		{ return Integer.toString(p.stats.deaths); }
	};
	
	public static final Top deaths_ph = new Top("deaths_ph")
	{
		@Override
		public int compare(ForgePlayerMP o1, ForgePlayerMP o2)
		{ return Double.compare(o2.stats.getDeathsPerHour(), o1.stats.getDeathsPerHour()); }
		
		@Override
		public Object getData(ForgePlayerMP p)
		{ return MathHelperLM.toSmallDouble(p.stats.getDeathsPerHour()); }
	};
	
	public static final Top last_seen = new Top("last_seen")
	{
		@Override
		public int compare(ForgePlayerMP o1, ForgePlayerMP o2)
		{ return Long.compare(o2.stats.lastSeen, o1.stats.lastSeen); }
		
		@Override
		public Object getData(ForgePlayerMP p)
		{
			if(p.isOnline()) return FTBLibMod.mod.chatComponent("label.online");
			return LMStringUtils.getTimeString(LMUtils.millis() - p.stats.lastSeen);
		}
	};
	
	public static final Top time_played = new Top("time_played")
	{
		@Override
		public int compare(ForgePlayerMP o1, ForgePlayerMP o2)
		{ return Long.compare(o2.stats.timePlayed, o1.stats.timePlayed); }
		
		@Override
		public Object getData(ForgePlayerMP p)
		{ return LMStringUtils.getTimeString(p.stats.timePlayed) + " [" + (p.stats.timePlayed / 3600000L) + "h]"; }
	};
}