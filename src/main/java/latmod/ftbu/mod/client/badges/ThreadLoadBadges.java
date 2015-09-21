package latmod.ftbu.mod.client.badges;

import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.util.*;
import latmod.ftbu.mod.config.FTBUConfig;

@SideOnly(Side.CLIENT)
public class ThreadLoadBadges extends Thread
{
	public static final String DEF_BADGES = "http://pastebin.com/raw.php?i=KWGvviPR";
	private static final FastMap<String, Badge> urlBadges = new FastMap<String, Badge>();
	
	public static void init()
	{ new ThreadLoadBadges().start(); }
	
	public ThreadLoadBadges()
	{
		super("BadgeLoader");
		setDaemon(true);
	}
	
	public void run()
	{
		long msStarted = LMUtils.millis();
		Badge.isReloading = true;
		urlBadges.clear();
		
		LatCoreMC.logger.info("Loading badges...");
		Badge.init();
		
		int loaded = loadBages(DEF_BADGES);
		
		if(!FTBUConfig.login.customBadges.isEmpty())
			loaded += loadBages(FTBUConfig.login.customBadges);
		
		LatCoreMC.logger.info("Loaded badges for " + loaded + " players in " + ((LMUtils.millis() - msStarted) / 1000F) + " ms!");
		
		Badge.isReloading = false;
	}
	
	public int loadBages(String url)
	{
		int loaded = 0;
		
		try
		{
			InputStream is = new URL(url).openStream();
			String raw = LMStringUtils.toString(is).trim();
			
			Badges list = LMJsonUtils.fromJson(LMJsonUtils.formatJson(raw, false), Badges.class);
			
			for(String k : list.badges.keySet())
				urlBadges.put(k, new Badge(list.badges.get(k)));
			
			for(String k : list.players.keySet())
			{
				UUID id = LatCoreMC.getUUIDFromString(k);
				Badge b = urlBadges.get(list.players.get(k));
				if(id != null && b != null)
				{
					Badge.badges.put(id, b);
					loaded++;
				}
			}
		}
		catch(Exception e) { }
		
		return loaded;
	}
}