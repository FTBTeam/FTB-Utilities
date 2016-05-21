package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.api.ForgeWorld;
import com.feed_the_beast.ftbu.FTBUCapabilities;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

/**
 * Created by LatvianModder on 18.05.2016.
 */
public abstract class FTBUWorldData implements ICapabilityProvider
{
    public static boolean isLoadedW(ForgeWorld world)
    {
        return world != null && world.hasCapability(FTBUCapabilities.FTBU_WORLD_DATA, null);
    }

    public static FTBUWorldData getW(ForgeWorld world)
    {
        return isLoadedW(world) ? world.getCapability(FTBUCapabilities.FTBU_WORLD_DATA, null) : null;
    }

    public void onLoaded()
    {
    }

    public void onClosed()
    {
    }

    @Override
    public final boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        return capability == FTBUCapabilities.FTBU_WORLD_DATA;
    }

    @Override
    public final <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        return (T) this;
    }

    public FTBUWorldDataMP toMP()
    {
        return null;
    }
}
