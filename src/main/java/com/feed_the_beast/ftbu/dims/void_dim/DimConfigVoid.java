package com.feed_the_beast.ftbu.dims.void_dim;

import com.feed_the_beast.ftbl.api.config.impl.PropertyBool;
import com.feed_the_beast.ftbl.api.config.impl.PropertyInt;
import com.feed_the_beast.ftbu.dims.ConfigEntryBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.Constants;

/**
 * Created by LatvianModder on 30.08.2016.
 */
public class DimConfigVoid
{
    private static final PropertyBool ENABLED = new PropertyBool(false);
    private static final PropertyInt DIMENSION_ID = new PropertyInt(8);

    static final ConfigEntryBlockState BOTTOM_LAYER = new ConfigEntryBlockState(Blocks.BARRIER.getDefaultState());
    static final ConfigEntryBlockState PLATFORM_BLOCK = new ConfigEntryBlockState(Blocks.STONE.getDefaultState());

    static final PropertyInt PLATFORM_RADIUS = new PropertyInt(Constants.NBT.TAG_BYTE, 2).setMin(0).setMax(64);
    static final PropertyInt PLATFORM_Y = new PropertyInt(Constants.NBT.TAG_BYTE, 64).setMin(0).setMax(255);

    public static DimensionType dimensionType;

    public static void init()
    {
        if(ENABLED.getBoolean())
        {
            dimensionType = DimensionType.register("FTBU Void", "_ftbu_void", DIMENSION_ID.getInt(), WorldProviderVoid.class, false);
            DimensionManager.registerDimension(dimensionType.getId(), dimensionType);
        }
    }
}