package com.feed_the_beast.ftbu.dims.mining_dim;

import com.feed_the_beast.ftbl.api.config.ConfigEntryBool;
import com.feed_the_beast.ftbl.api.config.ConfigEntryInt;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;

/**
 * Created by LatvianModder on 30.08.2016.
 */
public class DimConfigMining
{
    private static final ConfigEntryBool enabled = new ConfigEntryBool(false);
    private static final ConfigEntryInt dimension_id = new ConfigEntryInt(9);

    public static DimensionType dimensionType;

    public static void init()
    {
        if(enabled.getAsBoolean())
        {
            dimensionType = DimensionType.register("FTBU Mining", "_ftbu_mining", dimension_id.getAsInt(), WorldProviderMining.class, false);
            DimensionManager.registerDimension(dimensionType.getId(), dimensionType);
        }
    }
}
