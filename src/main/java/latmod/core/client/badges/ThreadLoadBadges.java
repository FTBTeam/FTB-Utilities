package latmod.core.client.badges;

import java.io.*;
import java.net.URL;

import latmod.core.LatCoreMC;
import latmod.core.event.CustomBadgesEvent;
import latmod.core.mod.client.LCClientEventHandler;
import latmod.core.util.MathHelperLM;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class ThreadLoadBadges extends Thread
{
	public static void init()
	{
		Badges.init();
		new ThreadLoadBadges().start();
	}
	
	public ThreadLoadBadges()
	{
		super("BadgeLoader");
		setDaemon(true);
	}
	
	public void run()
	{
		LatCoreMC.logger.info("Loading Badges...");
		LCClientEventHandler.playerBadges.clear();
		
		try
		{
			int loaded = 0;
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("http://pastebin.com/raw.php?i=W7DcLN1b").openStream()));
			String raw = null;
			while((raw = reader.readLine()) != null)
			{
				if(!raw.isEmpty() && !raw.startsWith("#"))
				{
					String[] s = raw.split(": ");
					
					if(s != null && s.length == 2)
					{
						Badge b = Badges.registry.get(s[1]);
						if(b != null)
						{
							LCClientEventHandler.playerBadges.put(s[0], b);
							loaded++;
						}
					}
				}
			}
			
			reader.close();
			LatCoreMC.logger.info(loaded + MathHelperLM.getPluralWord(loaded, " badge", " badges") + " loaded!");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			LatCoreMC.logger.warn("Badges failed to load!");
		}
		
		new CustomBadgesEvent().post();
	}
}