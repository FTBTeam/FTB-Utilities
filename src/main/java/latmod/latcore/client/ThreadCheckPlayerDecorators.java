package latmod.latcore.client;

import java.io.InputStream;
import java.net.URL;

import latmod.core.LatCoreMC;
import latmod.core.util.*;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class ThreadCheckPlayerDecorators implements Runnable
{
	private Thread thread;
	
	public static void init()
	{
		ThreadCheckPlayerDecorators t = new ThreadCheckPlayerDecorators();
		t.thread = new Thread(t, "LatMod_CheckPlayerDecorators");
		t.thread.start();
	}
	
	public void run()
	{
		LatCoreMC.logger.info("Loading PlayerDecorators...");
		
		LCClientEventHandler.instance.playerDecorators.clear();
		
		try
		{
			InputStream is = new URL("http://pastebin.com/raw.php?i=ihHF9uta").openStream();
			byte[] b = new byte[is.available()];
			is.read(b);
			String raw = new String(b);
			
			if(raw != null && raw.length() > 0)
			{
				String[] rawA = LatCore.split(raw.trim(), "\n");
				
				for(int i = 0; i < rawA.length; i++)
				{
					rawA[i] = rawA[i].trim();
					String[] s = rawA[i].split(":");
					
					if(s != null && s.length == 2)
					{
						FastList<PlayerDecorator> al1 = new FastList<PlayerDecorator>();
						String[] s1 = LatCore.split(s[1], ",");
						
						for(int j = 0; j < s1.length; j++)
						{
							PlayerDecorator p = PlayerDecorator.map.get(s1[j]);
							if(p != null) al1.add(p); else LatCoreMC.logger.warn("Unknown PlayerDecorator: " + s1[j]);
						}
						
						if(al1.size() > 0) LCClientEventHandler.instance.playerDecorators.put(s[0], al1);
					}
					else LatCoreMC.logger.warn("Invalid line: " + LatCore.strip(s));
				}
				
				if(LatCoreMC.isDevEnv) LatCoreMC.logger.info("Player Decorators: " + LCClientEventHandler.instance.playerDecorators + " [ " + PlayerDecorator.map.keys + " ] " + " from file " + LatCore.strip(rawA));
			}
			else LatCoreMC.logger.warn("Player Decorators failed to load!");
		}
		catch(Exception ex)
		{ ex.printStackTrace(); }
		
		thread = null;
	}
}