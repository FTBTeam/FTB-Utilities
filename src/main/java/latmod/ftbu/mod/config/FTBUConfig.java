package latmod.ftbu.mod.config;

import ftb.lib.FTBLib;
import ftb.lib.api.config.ConfigRegistry;
import latmod.ftbu.world.ranks.*;
import latmod.lib.config.*;
import net.minecraft.util.EnumChatFormatting;

import java.io.File;

public class FTBUConfig // FTBU
{
	private static ConfigFile configFile;
	
	public static void load()
	{
		configFile = new ConfigFile("ftbu", new File(FTBLib.folderLocal, "ftbu/config.json"));
		configFile.configGroup.setName("FTBUtilities");
		configFile.add(new ConfigGroup("backups").addAll(FTBUConfigBackups.class, null, false));
		configFile.add(new ConfigGroup("commands").addAll(FTBUConfigCmd.class, null, false).setInfo("Command name configs\nEditing is not recommended"));
		configFile.add(new ConfigGroup("general").addAll(FTBUConfigGeneral.class, null, false));
		configFile.add(new ConfigGroup("login").addAll(FTBUConfigLogin.class, null, false));
		configFile.add(new ConfigGroup("tops").addAll(FTBUConfigTops.class, null, false));
		
		Ranks.ADMIN.setDefaults();
		configFile.add(new ConfigGroup("permissions_admin").addAll(RankConfig.class, Ranks.ADMIN.config, false));
		Ranks.ADMIN.color.set(EnumChatFormatting.DARK_GREEN);
		
		Ranks.PLAYER.setDefaults();
		configFile.add(new ConfigGroup("permissions_player").addAll(RankConfig.class, Ranks.PLAYER.config, false));
		
		ConfigRegistry.add(configFile);
		configFile.load();
	}
}