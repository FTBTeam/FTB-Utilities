package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbu.FTBUCapabilities;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

/**
 * Created by LatvianModder on 18.05.2016.
 */
public abstract class FTBUWorldData implements ICapabilityProvider
{
    public void onLoaded()
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
}
