package latmod.ftbu.mod.config;

import java.io.File;

import ftb.lib.FTBLib;
import ftb.lib.api.config.ConfigRegistry;
import latmod.lib.config.ConfigFile;

public class FTBUConfig // FTBU
{
	private static ConfigFile configFile;
	
	public static void load()
	{
		configFile = new ConfigFile("ftbu", new File(FTBLib.folderLocal, "ftbu/config.json"));
		configFile.configGroup.setName("FTBUtilities");
		configFile.add(FTBUConfigGeneral.group.addAll(FTBUConfigGeneral.class));
		configFile.add(FTBUConfigLogin.group.addAll(FTBUConfigLogin.class));
		configFile.add(FTBUConfigBackups.group.addAll(FTBUConfigBackups.class));
		configFile.add(FTBUConfigClaims.group.addAll(FTBUConfigClaims.class));
		configFile.add(FTBUConfigCmd.group.addAll(FTBUConfigCmd.class));
		configFile.add(FTBUConfigTops.group.addAll(FTBUConfigTops.class));
		ConfigRegistry.add(configFile);
		configFile.load();
	}
}