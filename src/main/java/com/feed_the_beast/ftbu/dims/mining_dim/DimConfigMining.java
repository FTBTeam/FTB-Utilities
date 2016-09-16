package com.feed_the_beast.ftbu.dims.mining_dim;

import com.feed_the_beast.ftbl.api.config.impl.PropertyBool;
import com.feed_the_beast.ftbl.api.config.impl.PropertyInt;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;

/**
 * Created by LatvianModder on 30.08.2016.
 */
public class DimConfigMining
{
    private static final PropertyBool ENABLED = new PropertyBool(false);
    private static final PropertyInt DIMENSION_ID = new PropertyInt(9);

    public static DimensionType dimensionType;

    public static void init()
    {
        if(ENABLED.getBoolean())
        {
            dimensionType = DimensionType.register("FTBU Mining", "_ftbu_mining", DIMENSION_ID.getInt(), WorldProviderMining.class, false);
            DimensionManager.registerDimension(dimensionType.getId(), dimensionType);
        }
    }
}
