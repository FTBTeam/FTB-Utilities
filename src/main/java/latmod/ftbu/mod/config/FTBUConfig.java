package latmod.ftbu.mod.config;

import java.io.File;

import latmod.ftbu.api.config.ConfigListRegistry;
import latmod.ftbu.api.guide.*;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.util.LatCoreMC;
import latmod.lib.config.*;

public class FTBUConfig // FTBU
{
	private static ConfigFile configFile;
	
	public static void load()
	{
		configFile = new ConfigFile(FTBU.mod.modID, new File(LatCoreMC.localConfigFolder, "ftbu/config.json"), true);
		FTBUConfigGeneral.load(configFile);
		FTBUConfigLogin.load(configFile);
		FTBUConfigBackups.load(configFile);
		FTBUConfigClaims.load(configFile);
		
		ConfigListRegistry.add(configFile);
		configFile.load();
	}
	
	public static void save()
	{
		configFile.save();
	}
	
	public static void saveReadme(GuideFile file)
	{
		saveReadmeGroup(file, FTBUConfigGeneral.class, FTBUConfigGeneral.group);
		saveReadmeGroup(file, FTBUConfigLogin.class, FTBUConfigLogin.group);
		saveReadmeGroup(file, FTBUConfigBackups.class, FTBUConfigBackups.group);
		saveReadmeGroup(file, FTBUConfigClaims.class, FTBUConfigClaims.group);
	}
	
	private static void saveReadmeGroup(GuideFile file, Class<?> c, ConfigGroup g)
	{ file.add(new GuideCategory("config_local/ftbu/config.json/" + g.toString()).addFromClass(c)); }
}