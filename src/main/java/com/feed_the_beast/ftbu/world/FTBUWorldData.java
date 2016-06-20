package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.api.ForgeWorld;
import com.feed_the_beast.ftbu.FTBUCapabilities;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

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

    public void onLoadedBeforePlayers()
    {
    }

    public void onClosed()
    {
    }

    @Override
    public final boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == FTBUCapabilities.FTBU_WORLD_DATA;
    }

    @Override
    public final <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if(capability == FTBUCapabilities.FTBU_WORLD_DATA)
        {
            return (T) this;
        }

        return null;
    }

    public FTBUWorldDataMP toMP()
    {
        return null;
    }
}
