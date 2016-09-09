package com.feed_the_beast.ftbu.dims;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.config.ConfigFile;
import com.feed_the_beast.ftbu.dims.mining_dim.DimConfigMining;
import com.feed_the_beast.ftbu.dims.void_dim.DimConfigVoid;
import com.latmod.lib.util.LMUtils;
import net.minecraft.util.text.TextComponentString;

import java.io.File;

public class FTBUDimConfig // FTBU
{
    public static final ConfigFile configFile = new ConfigFile();

    public static void load()
    {
        configFile.setFile(new File(LMUtils.folderConfig, "FTBU_Dimensions.json"));
        configFile.setDisplayName(new TextComponentString("FTBUtilities Dimensions"));
        configFile.addGroup("void", DimConfigVoid.class);
        configFile.addGroup("mining", DimConfigMining.class);
        FTBLibAPI.get().getRegistries().configFiles().register("ftbu_dims", configFile);
        configFile.load();
    }
}