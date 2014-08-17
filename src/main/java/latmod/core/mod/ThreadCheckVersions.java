package latmod.core.mod;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import latmod.core.LMUtils;

public class ThreadCheckVersions implements Runnable
{
	private Thread thread;
	
	public static void init()
	{
		ThreadCheckVersions t = new ThreadCheckVersions();
		t.thread = new Thread(t, "LatMod_CheckVersions");
		t.thread.start();
	}
	
	public void run()
	{
		try
		{
			InputStream is = new URL("http://pastebin.com/raw.php?i=kCiNXdH0").openStream();
			byte[] b = new byte[is.available()];
			is.read(b);
			String s = new String(b);
			
			if(s.length() > 0 && s.startsWith("{") && s.endsWith("}"))
			{
				Map<String, String> map = LMUtils.fromJson(s, LMUtils.getMapType(String.class, String.class));
				LC.latmodVersions.putAll(map);
				
				LC.logger.info("Versions file loaded");
			}
			else LC.logger.info("Failed to check versions");
		}
		catch(Exception ex)
		{ ex.printStackTrace(); }
		
		thread = null;
	}
}