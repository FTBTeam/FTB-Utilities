package latmod.ftbu.mod.config;

import java.io.File;

import ftb.lib.FTBLib;
import ftb.lib.api.config.ConfigListRegistry;
import latmod.ftbu.api.guide.ServerGuideFile;
import latmod.ftbu.mod.FTBU;
import latmod.lib.config.ConfigFile;

public class FTBUConfig // FTBU
{
	private static ConfigFile configFile;
	private static ConfigFile configFileModpack;
	
	public static void load()
	{
		configFile = new ConfigFile(FTBU.mod.modID, new File(FTBLib.folderLocal, "ftbu/config.json"), true);
		FTBUConfigGeneral.load(configFile);
		FTBUConfigLogin.load(configFile);
		FTBUConfigBackups.load(configFile);
		FTBUConfigClaims.load(configFile);
		FTBUConfigCmd.load(configFile);
		ConfigListRegistry.add(configFile);
		configFile.load();
	}
	
	public static void save()
	{
		configFile.save();
		configFileModpack.save();
	}
	
	public static void onGuideEvent(ServerGuideFile file)
	{
		addGuideGroup(file, "General", FTBUConfigGeneral.class);
		addGuideGroup(file, "Login", FTBUConfigLogin.class);
		addGuideGroup(file, "Backups", FTBUConfigBackups.class);
		addGuideGroup(file, "Claims", FTBUConfigClaims.class);
	}
	
	private static void addGuideGroup(ServerGuideFile file, String s, Class<?> c)
	{ file.addConfigFromClass("FTBUtilities", s, c); }
}