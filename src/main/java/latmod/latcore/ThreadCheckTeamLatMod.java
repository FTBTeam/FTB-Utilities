package latmod.latcore;

import java.io.InputStream;
import java.net.URL;
import java.util.*;

import latmod.core.util.LatCore;

public class ThreadCheckTeamLatMod implements Runnable
{
	private Thread thread;
	
	public static void init()
	{
		ThreadCheckTeamLatMod t = new ThreadCheckTeamLatMod();
		t.thread = new Thread(t, "LatMod_CheckTeamLatMod");
		t.thread.start();
	}
	
	public void run()
	{
		LC.logger.info("Loading TeamLatMod.json...");
		
		try
		{
			InputStream is = new URL("http://pastebin.com/raw.php?i=ihHF9uta").openStream();
			byte[] b = new byte[is.available()];
			is.read(b);
			String s = new String(b);
			
			if(s.length() > 0 && s.startsWith("{") && s.endsWith("}"))
			{
				Map<String, List<String>> map = LatCore.fromJson(s, LatCore.getMapType(String.class, LatCore.getListType(String.class)));
				
				if(map != null && map.size() > 0)
				{
					Iterator<String> keys = map.keySet().iterator();
					Iterator<List<String>> values = map.values().iterator();
					
					while(keys.hasNext())
					{
						String k = keys.next();
						List<String> v = values.next();
						
						EnumLatModTeam e = EnumLatModTeam.get(k);
						
						if(e != null && v != null && v.size() >= 2 && v.size() % 2 == 0)
						{
							for(int i = 0; i < v.size(); i += 2)
							{
								e.uuids.add(UUID.fromString(v.get(i)));
								e.names.add(v.get(i + 1));
							}
						}
					}
					
					LC.logger.info("LatMod Team loaded");
				}
				else LC.logger.info("Invalid LatMod Team file");
			}
			else LC.logger.info("LatMod Team failed to load");
		}
		catch(Exception ex)
		{ ex.printStackTrace(); }
		
		thread = null;
	}
}