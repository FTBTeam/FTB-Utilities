package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.api.ForgeTeam;
import com.feed_the_beast.ftbu.FTBUCapabilities;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

/**
 * Created by LatvianModder on 11.02.2016.
 */
public abstract class FTBUTeamData implements ICapabilityProvider
{
    public static FTBUTeamData get(ForgeTeam t)
    {
        return t.hasCapability(FTBUCapabilities.FTBU_TEAM_DATA, null) ? t.getCapability(FTBUCapabilities.FTBU_TEAM_DATA, null) : null;
    }

    public FTBUTeamDataMP toMP()
    {
        return null;
    }

    public FTBUTeamDataSP toSP()
    {
        return null;
    }

    @Override
    public final boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == FTBUCapabilities.FTBU_TEAM_DATA;
    }

    @Override
    public final <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if(capability == FTBUCapabilities.FTBU_TEAM_DATA)
        {
            return (T) this;
        }

        return null;
    }
}