package com.feed_the_beast.ftbu.world.gen;

import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;

/**
 * Created by LatvianModder on 08.07.2016.
 */
public class WorldProviderVoid extends WorldProvider
{
    public static final DimensionType DIMENSION_TYPE = DimensionType.register("Void", "_void", 0, WorldProviderVoid.class, false);

    @Override
    public DimensionType getDimensionType()
    {
        return DIMENSION_TYPE;
    }
}