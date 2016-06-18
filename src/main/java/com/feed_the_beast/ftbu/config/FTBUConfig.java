package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.config.ConfigFile;
import com.feed_the_beast.ftbl.api.config.ConfigRegistry;
import com.feed_the_beast.ftbl.util.FTBLib;
import net.minecraft.util.text.TextComponentString;

import java.io.File;

public class FTBUConfig // FTBU
{
    public static final ConfigFile configFile = new ConfigFile();

    public static void load()
    {
        configFile.setFile(new File(FTBLib.folderLocal, "ftbu/config.json"));
        configFile.setDisplayName(new TextComponentString("FTBUtilities"));
        configFile.addGroup("modules", FTBUConfigModules.class);
        configFile.addGroup("backups", FTBUConfigBackups.class);
        configFile.addGroup("general", FTBUConfigGeneral.class);
        configFile.addGroup("login", FTBUConfigLogin.class);
        configFile.addGroup("webapi", FTBUConfigWebAPI.class);
        //Ranks.instance().reload();

        ConfigRegistry.add("ftbu", configFile);
        configFile.load();
    }
}