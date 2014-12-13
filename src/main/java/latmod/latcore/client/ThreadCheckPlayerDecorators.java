package latmod.latcore.client;

import java.net.URL;

import latmod.core.*;
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
			FastList<String> al = LatCore.toStringList(new URL("http://pastebin.com/raw.php?i=ihHF9uta").openStream());
			
			if(al != null && al.size() > 0)
			{
				for(int i = 0; i < al.size(); i++)
				{
					String[] s = al.get(i).split(":");
					
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
				
				if(LatCoreMC.isDevEnv) LatCoreMC.logger.info("Player Decorators: " + LCClientEventHandler.instance.playerDecorators + " [ " + PlayerDecorator.map.keys + " ] from file " + al);
			}
			else LatCoreMC.logger.warn("Player Decorators failed to load!");
		}
		catch(Exception ex)
		{ ex.printStackTrace(); }
		
		thread = null;
	}
}