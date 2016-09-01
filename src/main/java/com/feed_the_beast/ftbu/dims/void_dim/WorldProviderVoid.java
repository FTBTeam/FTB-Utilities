package com.feed_the_beast.ftbu.dims.void_dim;

import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.chunk.IChunkGenerator;

/**
 * Created by LatvianModder on 08.07.2016.
 */
class WorldProviderVoid extends WorldProvider
{
    @Override
    public DimensionType getDimensionType()
    {
        return DimConfigVoid.dimensionType;
    }

    @Override
    protected void generateLightBrightnessTable()
    {
        for(int i = 0; i <= 15; ++i)
        {
            float f1 = 1F - (float) i / 15F;
            lightBrightnessTable[i] = (1F - f1) / (f1 * 3F + 1F) * 1F + 0F;
        }
    }

    @Override
    public void createBiomeProvider()
    {
        biomeProvider = new BiomeProviderSingle(Biomes.PLAINS);
    }

    @Override
    public IChunkGenerator createChunkGenerator()
    {
        return new ChunkProviderVoid(worldObj);
    }

    @Override
    public boolean canCoordinateBeSpawn(int x, int z)
    {
        return x == 0 && z == 0;
    }

    @Override
    public float calculateCelestialAngle(long worldTime, float partialTicks)
    {
        return 0.5F;
    }

    @Override
    public boolean isSurfaceWorld()
    {
        return false;
    }

    @Override
    public boolean canRespawnHere()
    {
        return false;
    }

    @Override
    public String getSaveFolder()
    {
        return "DIM_FTBU_VOID";
    }

    @Override
    public String getWelcomeMessage()
    {
        return "Entering the Void";
    }

    @Override
    public String getDepartMessage()
    {
        return "Leaving the Void";
    }

    @Override
    public BlockPos getRandomizedSpawnPoint()
    {
        return getSpawnPoint();
    }

    @Override
    public Biome getBiomeForCoords(BlockPos pos)
    {
        return Biomes.PLAINS;
    }

    @Override
    public void calculateInitialWeather()
    {
        worldObj.calculateInitialWeatherBody();
    }

    @Override
    public void updateWeather()
    {
        if(worldObj.isRaining())
        {
            resetRainAndThunder();
        }
    }

    @Override
    public BlockPos getSpawnPoint()
    {
        return new BlockPos(0, DimConfigVoid.platform_y.getAsInt() + 1, 0);
    }
}