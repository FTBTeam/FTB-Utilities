package latmod.ftbu.mod.config;

import java.io.File;

import latmod.ftbu.api.config.*;
import latmod.ftbu.api.readme.*;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.util.LatCoreMC;

public class FTBUConfig // FTBU
{
	private static ConfigFile configFile;
	
	public static void load()
	{
		configFile = new ConfigFile(FTBU.mod.modID, new File(LatCoreMC.latmodFolder, "ftbu/config.txt"), true);
		FTBUConfigGeneral.load(configFile);
		FTBUConfigLogin.load(configFile);
		FTBUConfigBackups.load(configFile);
		ConfigFileRegistry.add(configFile);
		configFile.load();
	}
	
	public static void save()
	{
		configFile.save();
	}
	
	public static void saveReadme(ReadmeFile file)
	{
		file.add(new ReadmeCategory("latmod/ftbu/config.txt/" + FTBUConfigGeneral.group.ID).addFromClass(FTBUConfigGeneral.class));
		file.add(new ReadmeCategory("latmod/ftbu/config.txt/" + FTBUConfigLogin.group.ID).addFromClass(FTBUConfigLogin.class));
		file.add(new ReadmeCategory("latmod/ftbu/config.txt/" + FTBUConfigBackups.group.ID).addFromClass(FTBUConfigBackups.class));
	}
	
	/* FIXME: Overrides
	private static class Overrides
	{
		public Map<String, Object> overrides;
	}*/
	
	public static int loadOverrides()
	{
		int loaded = 0;
		
		/*
		try
		{
			File f = new File(LatCoreMC.configFolder, "LatMod/FTBU_Overrides.txt");
			Overrides overrides = LMJsonUtils.fromJsonFile(f, Overrides.class);
			if(overrides == null || overrides.overrides == null)
			{
				overrides = new Overrides();
				overrides.overrides = new HashMap<String, Map<String, Object>>();
				LMJsonUtils.toJsonFile(f, overrides);
				return 0;
			}
			
			if(overrides.overrides.isEmpty()) return 0;
			
			for(String cat : overrides.overrides.keySet())
			{
				Object c = Overrides.getFromFile(cat);
				
				if(c != null)
				{
					Map<String, Object> m = overrides.overrides.get(cat);
					
					if(m != null && !m.isEmpty())
					{
						for(String k : m.keySet())
						{
							Object v = m.get(k);
							
							if(v != null)
							{
								try
								{
									Field kf = c.getClass().getField(k);
									kf.setAccessible(true);
									
									Object v0 = kf.get(c);
									
									if(v0.getClass() == Float.class && v.getClass() == Double.class)
										v = ((Double)v).floatValue();
									
									if(List.class.isAssignableFrom(v.getClass()))
									{
										if(v0.getClass() == (new String[0]).getClass())
											v = ((List<String>)v).toArray(new String[0]);
									}
									
									kf.set(c, v);
									LatCoreMC.logger.info("Replaced " + v0 + " in '" + cat + "." + k + "' with " + v);
									loaded++;
								}
								catch(Exception e)
								{ e.printStackTrace(); }
							}
						}
					}
				}
			}
		}
		catch(Exception e)
		{ e.printStackTrace(); }
		*/
		
		return loaded;
	}
}