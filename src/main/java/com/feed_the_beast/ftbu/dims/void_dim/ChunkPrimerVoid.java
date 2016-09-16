package com.feed_the_beast.ftbu.dims.void_dim;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.ChunkPrimer;

/**
 * Created by LatvianModder on 30.08.2016.
 */
class ChunkPrimerVoid extends ChunkPrimer
{
    public static final ChunkPrimerVoid INSTANCE = new ChunkPrimerVoid();
    private static final IBlockState AIR = Blocks.AIR.getDefaultState();

    @Override
    public IBlockState getBlockState(int x, int y, int z)
    {
        if(y == DimConfigVoid.PLATFORM_Y.getInt())
        {
            int r = DimConfigVoid.PLATFORM_RADIUS.getInt();

            if(x <= r && x >= -r && z <= r && z >= -r)
            {
                return DimConfigVoid.PLATFORM_BLOCK.getBlockState();
            }
        }

        return y == 0 ? DimConfigVoid.BOTTOM_LAYER.getBlockState() : AIR;
    }

    @Override
    public void setBlockState(int x, int y, int z, IBlockState state)
    {
    }

    @Override
    public int findGroundBlockIdx(int x, int z)
    {
        int r = DimConfigVoid.PLATFORM_RADIUS.getInt();

        if(x <= r && x >= -r && z <= r && z >= -r)
        {
            return DimConfigVoid.PLATFORM_Y.getInt();
        }

        return 0;
    }
}
