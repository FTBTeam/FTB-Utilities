package latmod.ftbu.mod.config;

import java.io.File;

import latmod.ftbu.api.config.ConfigListRegistry;
import latmod.ftbu.api.readme.*;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.util.LatCoreMC;
import latmod.lib.config.ConfigFile;

public class FTBUConfig // FTBU
{
	private static ConfigFile configFile;
	
	public static void load()
	{
		configFile = new ConfigFile(FTBU.mod.modID, new File(LatCoreMC.configFolder, "LatMod/FTBU.txt"), true);
		FTBUConfigGeneral.load(configFile);
		FTBUConfigLogin.load(configFile);
		FTBUConfigBackups.load(configFile);
		ConfigListRegistry.add(configFile);
		configFile.load();
	}
	
	public static void save()
	{
		configFile.save();
	}
	
	public static void saveReadme(ReadmeFile file)
	{
		file.add(new ReadmeCategory("latmod/ftbu/config.txt/" + FTBUConfigGeneral.group.toString()).addFromClass(FTBUConfigGeneral.class));
		file.add(new ReadmeCategory("latmod/ftbu/config.txt/" + FTBUConfigLogin.group.toString()).addFromClass(FTBUConfigLogin.class));
		file.add(new ReadmeCategory("latmod/ftbu/config.txt/" + FTBUConfigBackups.group.toString()).addFromClass(FTBUConfigBackups.class));
	}
}