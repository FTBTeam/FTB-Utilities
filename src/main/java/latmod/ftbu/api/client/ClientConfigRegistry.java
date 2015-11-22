package latmod.ftbu.api.client;

import java.io.File;

import ftb.lib.FTBLib;
import latmod.lib.*;

public final class ClientConfigRegistry
{
	private static File configFile;
	public static final FastMap<String, ClientConfig> map = new FastMap<String, ClientConfig>();
	
	public static void add(ClientConfig c)
	{ map.put(c.id, c); }
	
	public static void init()
	{
		configFile = LMFileUtils.newFile(new File(FTBLib.folderLocal, "client/config.txt"));
	}
	
	public static void load()
	{
		try
		{
			FastList<String> l = LMFileUtils.load(configFile);
			
			for(String s : l) if(!s.isEmpty())
			{
				String[] s1 = s.split("=");
				if(s1.length == 2)
				{
					String[] s2 = s1[0].split(":");
					
					if(s2.length == 2)
					{
						ClientConfig c = map.get(s2[0]);
						
						if(c != null)
						{
							ClientConfigProperty p = c.map.get(s2[1]);
							if(p != null) p.value = Converter.toInt(s1[1], -1);
						}
					}
				}
			}
		}
		catch(Exception e)
		{ e.printStackTrace(); }
		
		save();
	}
	
	public static void save()
	{
		try
		{
			FastList<String> l = new FastList<String>();
			
			for(ClientConfig c : map.values)
				for(ClientConfigProperty e : c.map.values)
					l.add(c.id + ':' + e.id + '=' + e.getI());
			
			l.sort(null);
			LMFileUtils.save(configFile, l);
		}
		catch(Exception e)
		{ e.printStackTrace(); }
	}
}