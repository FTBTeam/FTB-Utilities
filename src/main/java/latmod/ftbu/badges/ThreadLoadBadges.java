package latmod.ftbu.badges;

import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.config.FTBUConfigLogin;
import latmod.lib.*;

@SideOnly(Side.CLIENT)
public class ThreadLoadBadges extends Thread
{
	public static final String DEF_BADGES = "http://latvianmodder.github.io/images/badges/global_badges.json";
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
		FTBU.mod.logger.info("Loading badges...");
		urlBadges.clear();
		Badge.badges.clear();
		
		loadBages(DEF_BADGES);
		
		if(!FTBUConfigLogin.customBadges.get().isEmpty())
			loadBages(FTBUConfigLogin.customBadges.get());
		
		FTBU.mod.logger.info("Loaded " + urlBadges.size() + " badges for " + Badge.badges.size() + " players in " + (LMUtils.millis() - msStarted) + " ms!");
	}
	
	public void loadBages(String url)
	{
		try
		{
			InputStream is = new URL(url).openStream();
			String raw = LMStringUtils.readString(is).trim();
			
			Badges list = LMJsonUtils.fromJson(raw, Badges.class);
			
			for(String k : list.badges.keySet())
				urlBadges.put(k, new Badge(list.badges.get(k)));
			
			for(UUID id : list.players.keySet())
			{
				Badge b = urlBadges.get(list.players.get(id));
				if(id != null && b != null)
				{
					Badge.badges.put(id, b);
				}
			}
		}
		catch(Exception e) { e.printStackTrace(); }
	}
}