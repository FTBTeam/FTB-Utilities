package latmod.ftbu.core.client.badges;

import java.io.*;
import java.net.URL;
import java.util.UUID;

import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.util.*;
import latmod.ftbu.mod.client.FTBURenderHandler;
import latmod.ftbu.mod.config.FTBUConfig;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class ThreadLoadBadges extends Thread
{
	private static final FastMap<String, BadgeURL> urlBadges = new FastMap<String, BadgeURL>();
	
	public static void init()
	{ new ThreadLoadBadges().start(); }
	
	private String lastLoadedURL = "";
	
	public ThreadLoadBadges()
	{
		super("BadgeLoader");
		setDaemon(true);
	}
	
	public void run()
	{
		long msStarted = LatCore.millis();
		
		Badge.reloading = true;
		Badge.init();
		LatCoreMC.logger.info("Loading badges...");
		FTBURenderHandler.playerBadges.clear();
		urlBadges.clear();
		
		try
		{
			int loaded = 0;
			lastLoadedURL = "http://pastebin.com/raw.php?i=3FRAVbJN";
			BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(lastLoadedURL).openStream()));
			String raw = null;
			while((raw = reader.readLine()) != null)
			{
				if(!raw.isEmpty() && !raw.startsWith("#"))
				{
					String[] s = raw.split(":");
					
					if(s != null && s.length >= 2)
					{
						UUID id = LatCoreMC.getUUIDFromString(s[0]);
						
						if(id != null)
						{
							FTBURenderHandler.playerBadges.put(id, Badge.getBadge(s[1]));
							loaded++;
						}
						else LatCoreMC.logger.warn("Invalid UUID: " + s[0]);
					}
				}
			}
			reader.close();
			
			lastLoadedURL = "http://pastebin.com/raw.php?i=LvBB9HmV";
			CustomBadges custom = LatCore.fromJson("{" + LatCore.toString(new URL(lastLoadedURL).openStream()) + "}", CustomBadges.class);
			
			for(String k : custom.badges.keySet())
				urlBadges.put(k, new BadgeURL(custom.badges.get(k)));
			
			for(String k : custom.players.keySet())
			{
				UUID id = LatCoreMC.getUUIDFromString(k);
				String bs = custom.players.get(k);
				if(bs.indexOf(',') != -1) bs = bs.split(",")[0];
				
				BadgeURL b = urlBadges.get(bs);
				if(id != null && b != null)
				{
					FTBURenderHandler.playerBadges.put(id, b);
					loaded++;
				}
			}
			
			if(!FTBUConfig.login.customBadges.isEmpty())
			{
				lastLoadedURL = FTBUConfig.login.customBadges;
				custom = LatCore.fromJson("{" + LatCore.toString(new URL(lastLoadedURL).openStream()) + "}", CustomBadges.class);
				
				for(String k : custom.badges.keySet())
					urlBadges.put(k, new BadgeURL(custom.badges.get(k)));
				
				for(String k : custom.players.keySet())
				{
					UUID id = LatCoreMC.getUUIDFromString(k);
					BadgeURL b = urlBadges.get(custom.players.get(k));
					if(id != null && b != null)
					{
						FTBURenderHandler.playerBadges.put(id, b);
						loaded++;
					}
				}
			}
			
			LatCoreMC.logger.info("Loaded badges for " + loaded + MathHelperLM.getPluralWord(loaded, " player ", " players ") + "in " + ((LatCore.millis() - msStarted) / 1000F) + " ms!");
		}
		catch(Exception ex)
		{
			//ex.printStackTrace();
			LatCoreMC.logger.warn("Badges failed to load! (" + lastLoadedURL + ")");
		}
		
		Badge.reloading = false;
	}
}