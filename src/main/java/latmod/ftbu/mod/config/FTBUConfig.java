package latmod.ftbu.mod.config;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

import latmod.core.util.LMJsonUtils;
import latmod.ftbu.api.IServerConfig;
import latmod.ftbu.api.readme.*;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.util.LatCoreMC;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

public class FTBUConfig implements IServerConfig // FTBU
{
	public static final FTBUConfig instance = new FTBUConfig();
	
	public static ConfigGeneral general;
	public static ConfigLogin login;
	public static ConfigBackups backups;
	
	public String getConfigName()
	{ return FTBU.mod.modID; }
	
	public void load()
	{
		ConfigGeneral.load();
		ConfigLogin.load();
		ConfigBackups.load();
		
		int overrides = loadOverrides();
		LatCoreMC.logger.info("Config loaded with " + overrides + " overrides");
		
		saveAll();
	}
	
	public static void saveAll()
	{
		ConfigGeneral.save();
		ConfigLogin.save();
		ConfigBackups.save();
	}
	
	public static void saveReadme(ReadmeFile file)
	{
		file.add(new ReadmeCategory("latmod/ftbu/general.txt").addFromClass(ConfigGeneral.class));
		file.add(new ReadmeCategory("latmod/ftbu/login.txt").addFromClass(ConfigLogin.class));
		file.add(new ReadmeCategory("latmod/ftbu/backups.txt").addFromClass(ConfigBackups.class));
	}
	
	public void readConfig(NBTTagCompound tag)
	{
		general.allowCreativeInteractSecure = tag.getBoolean("CS");
		login.customBadges = tag.getString("CB");
	}
	
	public void writeConfig(NBTTagCompound tag, EntityPlayerMP ep)
	{
		tag.setBoolean("CS", general.allowCreativeInteractSecure);
		if(!login.customBadges.isEmpty())
			tag.setString("CB", login.customBadges);
	}
	
	private static class Overrides
	{
		public Map<String, Map<String, Object>> overrides;
		
		private static Object getFromFile(String s)
		{
			if(s.equals("general")) return general;
			else if(s.equals("login")) return login;
			else if(s.equals("backups")) return backups;
			else return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static int loadOverrides()
	{
		int loaded = 0;
		
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
		
		return loaded;
	}
}