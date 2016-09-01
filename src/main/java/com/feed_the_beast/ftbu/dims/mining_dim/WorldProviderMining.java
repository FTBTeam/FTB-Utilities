package com.feed_the_beast.ftbu.dims.mining_dim;

import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;

/**
 * Created by LatvianModder on 08.07.2016.
 */
class WorldProviderMining extends WorldProvider
{
    @Override
    public DimensionType getDimensionType()
    {
        return DimConfigMining.dimensionType;
    }

    @Override
    public String getSaveFolder()
    {
        return "DIM_FTBU_MINING";
    }

    @Override
    public String getWelcomeMessage()
    {
        return "Entering the Mining Dimension";
    }

    @Override
    public String getDepartMessage()
    {
        return "Leaving the Mining Dimension";
    }
}