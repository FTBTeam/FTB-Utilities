package latmod.ftbu.mod.client.badges;

import java.net.URL;
import java.util.UUID;

import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.util.*;
import latmod.ftbu.mod.config.FTBUConfig;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class ThreadLoadBadges extends Thread
{
	public static void init()
	{ new ThreadLoadBadges().start(); }
	
	public ThreadLoadBadges()
	{
		super("BadgeLoader");
		setDaemon(true);
	}
	
	public void run()
	{
		long msStarted = LatCore.millis();
		String url = FTBUConfig.login.customBadges;
		Badge.isReloading = true;
		
		LatCoreMC.logger.info("Loading badges...");
		Badge.badges.clear();
		FastMap<String, Badge> urlBadges = new FastMap<String, Badge>();
		
		if(!url.isEmpty()) try
		{
			int loaded = 0;
			
			String raw = LMStringUtils.toString(new URL(url).openStream()).trim();
			Badges list = LatCore.fromJson(LatCore.formatJson(raw, false), Badges.class);
			
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
			
			LatCoreMC.logger.info("Loaded badges for " + loaded + MathHelperLM.getPluralWord(loaded, " player ", " players ") + "from " + url + " in " + ((LatCore.millis() - msStarted) / 1000F) + " ms!");
		}
		catch(Exception ex)
		{
			//ex.printStackTrace();
			LatCoreMC.logger.warn("Badges failed to load! (" + url + ")");
		}
		
		Badge.isReloading = false;
	}
}