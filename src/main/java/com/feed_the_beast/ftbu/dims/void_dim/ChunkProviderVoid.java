package com.feed_the_beast.ftbu.dims.void_dim;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkGenerator;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by LatvianModder on 30.08.2016.
 */
class ChunkProviderVoid implements IChunkGenerator
{
    private World worldObj;

    ChunkProviderVoid(World w)
    {
        worldObj = w;
    }

    @Override
    public Chunk provideChunk(int x, int z)
    {
        Chunk chunk = new Chunk(worldObj, ChunkPrimerVoid.INSTANCE, x, z);
        Arrays.fill(chunk.getBiomeArray(), (byte) Biome.getIdForBiome(Biomes.PLAINS));
        chunk.generateSkylightMap();
        return chunk;
    }

    @Override
    public void populate(int x, int z)
    {
    }

    @Override
    public boolean generateStructures(Chunk chunkIn, int x, int z)
    {
        return false;
    }

    @Override
    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos)
    {
        return Collections.emptyList();
    }

    @Nullable
    @Override
    public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position)
    {
        return null;
    }

    @Override
    public void recreateStructures(Chunk chunkIn, int x, int z)
    {
    }
}
