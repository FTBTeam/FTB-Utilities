package latmod.core.mod;

import java.io.InputStream;
import java.net.URL;
import java.util.*;

import latmod.core.LMUtils;

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
			
			if(s.length() > 0 && s.startsWith("[") && s.endsWith("]"))
			{
				List<String> list = LMUtils.fromJson(s, LMUtils.getListType(String.class));
				
				if(list.size() >= 2 && list.size() % 2 == 0)
				for(int i = 0; i < list.size(); i += 2)
				{
					LC.teamLatModUUIDs.add(UUID.fromString(list.get(i)));
					LC.teamLatModNames.add(list.get(i + 1));
				}
				else LC.logger.info("Invalid LatMod Team file");
				
				LC.logger.info("LatMod Team loaded");
			}
			else LC.logger.info("LatMod Team failed to load");
		}
		catch(Exception ex)
		{ ex.printStackTrace(); }
		
		thread = null;
	}
}