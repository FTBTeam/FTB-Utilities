package com.feed_the_beast.ftbu.dims;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api_impl.config.ConfigFile;
import com.feed_the_beast.ftbu.dims.mining_dim.DimConfigMining;
import com.feed_the_beast.ftbu.dims.void_dim.DimConfigVoid;
import com.latmod.lib.util.LMUtils;
import net.minecraft.util.text.TextComponentString;

import java.io.File;

public class FTBUDimConfig // FTBU
{
    public static final ConfigFile FILE = new ConfigFile();

    public static void load()
    {
        DimConfigVoid.init();
        DimConfigMining.init();

        FILE.setFile(new File(LMUtils.folderConfig, "FTBU_Dimensions.json"));
        FTBLibAPI.get().getRegistries().registerConfigFile("ftbu.dims", FILE, new TextComponentString("FTBUtilities Dimensions"));
        FILE.load();
    }
}