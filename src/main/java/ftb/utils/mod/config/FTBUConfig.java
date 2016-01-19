package ftb.utils.mod.config;

import ftb.lib.FTBLib;
import ftb.lib.api.config.ConfigRegistry;
import ftb.utils.world.ranks.Ranks;
import latmod.lib.config.*;

import java.io.File;

public class FTBUConfig // FTBU
{
	private static ConfigFile configFile;
	
	public static void load()
	{
		configFile = new ConfigFile("ftbu", new File(FTBLib.folderLocal, "ftbu/config.json"));
		configFile.configGroup.setName("FTBUtilities");
		
		configFile.add(new ConfigGroup("backups").addAll(FTBUConfigBackups.class, null, false));
		configFile.add(new ConfigGroup("commands").addAll(FTBUConfigCmd.class, null, false));
		configFile.add(new ConfigGroup("general").addAll(FTBUConfigGeneral.class, null, false));
		configFile.add(new ConfigGroup("login").addAll(FTBUConfigLogin.class, null, false));
		configFile.add(new ConfigGroup("tops").addAll(FTBUConfigTops.class, null, false));
		Ranks.load(configFile);
		
		ConfigRegistry.add(configFile);
		configFile.load();
	}
}