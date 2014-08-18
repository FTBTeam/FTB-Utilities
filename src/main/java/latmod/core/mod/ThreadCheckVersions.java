package latmod.core.mod;

import java.io.InputStream;
import java.net.URL;
import java.util.*;

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
			InputStream is = new URL("http://pastebin.com/raw.php?i=N8gUpQj8").openStream();
			byte[] b = new byte[is.available()];
			is.read(b);
			String s = new String(b);
			
			if(s.length() > 0 && s.startsWith("{") && s.endsWith("}"))
			{
				LC.versionsFile = LMUtils.fromJson(s, LMUtils.getMapType(String.class, LMUtils.getMapType(String.class, String.class)));
				LC.logger.info("Versions file loaded");
			}
			else LC.logger.info("Failed to check versions");
		}
		catch(Exception ex)
		{ ex.printStackTrace(); LC.versionsFile = new HashMap<String, Map<String, String>>(); }
		
		thread = null;
	}
	
	public static final VersionComparator comparator = new VersionComparator();
	
	public static class VersionComparator implements Comparator<String>
	{
		public int compare(String s1, String s2)
		{ return -compareCorrectOrder(s1, s2); }
		
		public int compareCorrectOrder(String s1, String s2)
		{
			String[] s1_s = s1.split("\\.");
			String[] s2_s = s2.split("\\.");
			
			if(s1_s.length == 3 && s2_s.length == 3)
			{
				int v0 = Integer.parseInt(s1_s[0]);
				int v1 = Integer.parseInt(s2_s[0]);
				
				if(v0 == v1)
				{
					v0 = Integer.parseInt(s1_s[1]);
					v1 = Integer.parseInt(s2_s[1]);
					
					if(v0 == v1)
					{
						v0 = Integer.parseInt(s1_s[2]);
						v1 = Integer.parseInt(s2_s[2]);
					}
				}
				
				return Integer.compare(v0, v1);
			}
			else if(s1_s.length == 4 && s2_s.length == 3)
				return 1;
			else if(s1_s.length == 4 && s2_s.length == 4)
				return s1_s[3].compareTo(s2_s[3]);
			
			return s1.compareTo(s2);
		}
	}
}