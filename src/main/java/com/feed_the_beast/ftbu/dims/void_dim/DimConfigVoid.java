package com.feed_the_beast.ftbu.dims.void_dim;

import com.feed_the_beast.ftbl.api.config.ConfigEntryBool;
import com.feed_the_beast.ftbl.api.config.ConfigEntryInt;
import com.feed_the_beast.ftbu.dims.ConfigEntryBlockState;
import com.latmod.lib.annotations.NumberBounds;
import net.minecraft.init.Blocks;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;

/**
 * Created by LatvianModder on 30.08.2016.
 */
public class DimConfigVoid
{
    private static final ConfigEntryBool enabled = new ConfigEntryBool(false);
    private static final ConfigEntryInt dimension_id = new ConfigEntryInt(8);

    static final ConfigEntryBlockState bottom_layer = new ConfigEntryBlockState(Blocks.BARRIER.getDefaultState());
    static final ConfigEntryBlockState platform_block = new ConfigEntryBlockState(Blocks.STONE.getDefaultState());

    @NumberBounds(min = 0, max = 64)
    static final ConfigEntryInt platform_radius = new ConfigEntryInt(2);

    @NumberBounds(min = 0, max = 255)
    static final ConfigEntryInt platform_y = new ConfigEntryInt(64);

    public static DimensionType dimensionType;

    public static void init()
    {
        if(enabled.getAsBoolean())
        {
            dimensionType = DimensionType.register("FTBU Void", "_ftbu_void", dimension_id.getAsInt(), WorldProviderVoid.class, false);
            DimensionManager.registerDimension(dimensionType.getId(), dimensionType);
        }
    }
}