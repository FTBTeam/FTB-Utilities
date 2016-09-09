package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.config.ConfigFile;
import com.feed_the_beast.ftbu.dims.FTBUDimConfig;
import com.latmod.lib.util.LMUtils;
import net.minecraft.util.text.TextComponentString;

import java.io.File;

public class FTBUConfig // FTBU
{
    public static final ConfigFile configFile = new ConfigFile();

    public static void load()
    {
        configFile.setFile(new File(LMUtils.folderLocal, "ftbu/config.json"));
        configFile.setDisplayName(new TextComponentString("FTBUtilities"));
        configFile.addGroup("world", FTBUConfigWorld.class);
        configFile.addGroup("backups", FTBUConfigBackups.class);
        configFile.addGroup("general", FTBUConfigGeneral.class);
        configFile.addGroup("login", FTBUConfigLogin.class);
        configFile.addGroup("webapi", FTBUConfigWebAPI.class);
        //Ranks.instance().reload();

        FTBLibAPI.get().getRegistries().configFiles().register("ftbu", configFile);
        configFile.load();

        FTBUDimConfig.load();
    }
}