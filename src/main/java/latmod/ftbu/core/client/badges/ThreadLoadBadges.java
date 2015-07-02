package latmod.ftbu.core.client.badges;

import java.io.*;
import java.net.URL;
import java.util.UUID;

import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.util.MathHelperLM;
import latmod.ftbu.mod.client.FTBURenderHandler;
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
		Badge.reloading = true;
		Badge.init();
		LatCoreMC.logger.info("Loading badges...");
		FTBURenderHandler.playerBadges.clear();
		
		try
		{
			int loaded = 0;
			long msStarted = System.currentTimeMillis();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("http://pastebin.com/raw.php?i=3FRAVbJN").openStream()));
			String raw = null;
			while((raw = reader.readLine()) != null)
			{
				if(!raw.isEmpty())
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
			LatCoreMC.logger.info("Loaded badges for " + loaded + MathHelperLM.getPluralWord(loaded, " player ", " players ") + "in " + ((System.currentTimeMillis() - msStarted) / 1000F) + " ms!");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			LatCoreMC.logger.warn("Badges failed to load!");
		}
		
		Badge.reloading = false;
	}
}