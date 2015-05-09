package latmod.core.client.playerdeco;

import java.net.URL;
import java.util.UUID;

import latmod.core.LatCoreMC;
import latmod.core.event.CustomPDEvent;
import latmod.core.mod.client.LCClientEventHandler;
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
			FastList<String> al = LatCore.toStringList(new URL("http://pastebin.com/raw.php?i=rurC7PNd").openStream());
			
			for(int i = 0; i < al.size(); i++)
			{
				String raw = al.get(i).trim();
				
				if(!raw.isEmpty() && !raw.startsWith("#"))
				{
					String[] s = raw.split(" :: ");
					
					if(s != null && s.length == 2)
					{
						FastList<PlayerDecorator> al1 = new FastList<PlayerDecorator>();
						String[] s1 = LatCore.split(s[1], ", ");
						
						for(int j = 0; j < s1.length; j++)
						{
							PlayerDecorator p = PlayerDecorator.getFromLine(s1[j]);
							if(p != null) al1.add(p); else if(LatCoreMC.isDevEnv) LatCoreMC.logger.warn("Unknown PlayerDecorator: " + s1[j]);
						}
						
						if(al1.size() > 0) LCClientEventHandler.instance.playerDecorators.put(UUID.fromString(s[0]), al1);
					}
				}
			}
			
			readList("http://pastebin.com/raw.php?i=c2LJZtE3", LCClientEventHandler.instance.listLatMod);
			readList("http://pastebin.com/raw.php?i=6wQU5MWK", LCClientEventHandler.instance.listFTB);
			
			LatCoreMC.logger.info("PlayerDecorators loaded!");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			LatCoreMC.logger.warn("Player Decorators failed to load!");
		}
		
		new CustomPDEvent().post();
		
		thread = null;
	}
	
	public void readList(String url, FastList<UUID> list) throws Exception
	{
		list.clear();
		
		FastList<String> al = LatCore.toStringList(new URL(url).openStream());
		
		for(int i = 0; i < al.size(); i++)
		{
			String raw = al.get(i).trim();
			
			if(!raw.isEmpty() && !raw.startsWith("#"))
				list.add(UUID.fromString(raw));
		}
	}
}