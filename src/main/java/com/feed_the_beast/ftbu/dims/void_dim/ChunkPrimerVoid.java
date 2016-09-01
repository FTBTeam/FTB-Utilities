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
        if(y == DimConfigVoid.platform_y.getAsInt())
        {
            int r = DimConfigVoid.platform_radius.getAsInt();

            if(x <= r && x >= -r && z <= r && z >= -r)
            {
                return DimConfigVoid.platform_block.getBlockState();
            }
        }

        return y == 0 ? DimConfigVoid.bottom_layer.getBlockState() : AIR;
    }

    @Override
    public void setBlockState(int x, int y, int z, IBlockState state)
    {
    }

    @Override
    public int findGroundBlockIdx(int x, int z)
    {
        int r = DimConfigVoid.platform_radius.getAsInt();

        if(x <= r && x >= -r && z <= r && z >= -r)
        {
            return DimConfigVoid.platform_y.getAsInt();
        }

        return 0;
    }
}
