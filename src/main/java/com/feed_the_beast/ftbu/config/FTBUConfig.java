package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.config.impl.ConfigFile;
import com.feed_the_beast.ftbu.dims.FTBUDimConfig;
import com.latmod.lib.util.LMUtils;
import net.minecraft.util.text.TextComponentString;

import java.io.File;

public class FTBUConfig // FTBU
{
    static final ConfigFile FILE = new ConfigFile();

    public static void load()
    {
        FTBUConfig.FILE.addAll("ftbu.world", FTBUConfigWorld.class, null);
        FTBUConfig.FILE.addAll("ftbu.backups", FTBUConfigBackups.class, null);
        FTBUConfig.FILE.addAll("ftbu.general", FTBUConfigGeneral.class, null);
        FTBUConfig.FILE.addAll("ftbu.login", FTBUConfigLogin.class, null);
        FTBUConfig.FILE.addAll("ftbu.webapi", FTBUConfigWebAPI.class, null);

        //Ranks.instance().reload();
        FTBUDimConfig.load();

        FILE.setFile(new File(LMUtils.folderLocal, "ftbu/config.json"));
        FTBLibAPI.get().getRegistries().registerConfigFile("ftbu", FILE, new TextComponentString("FTBUtilities"));
        FILE.load();
    }
}