package latmod.ftbu.mod.config;

import java.io.File;

import ftb.lib.FTBLib;
import ftb.lib.api.config.ConfigRegistry;
import latmod.ftbu.world.ranks.*;
import latmod.lib.config.*;

public class FTBUConfig // FTBU
{
	private static ConfigFile configFile;
	
	public static void load()
	{
		configFile = new ConfigFile("ftbu", new File(FTBLib.folderLocal, "ftbu/config.json"));
		configFile.configGroup.setName("FTBUtilities");
		configFile.add(new ConfigGroup("backups").addAll(FTBUConfigBackups.class));
		configFile.add(new ConfigGroup("commands").addAll(FTBUConfigCmd.class).setInfo("Command name configs\nEditing is not recommended"));
		configFile.add(new ConfigGroup("general").addAll(FTBUConfigGeneral.class));
		configFile.add(new ConfigGroup("login").addAll(FTBUConfigLogin.class));
		configFile.add(new ConfigGroup("tops").addAll(FTBUConfigTops.class));
		
		configFile.add(new ConfigGroup("permissions_admin").addAll(RankConfig.class, Ranks.ADMIN.config));
		configFile.add(new ConfigGroup("permissions_player").addAll(RankConfig.class, Ranks.PLAYER.config));
		
		ConfigRegistry.add(configFile);
		configFile.load();
	}
}