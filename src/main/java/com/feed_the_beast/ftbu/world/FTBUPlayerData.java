package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.api.ForgePlayer;
import com.feed_the_beast.ftbl.api.config.ConfigEntryBool;
import com.feed_the_beast.ftbu.FTBUCapabilities;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

/**
 * Created by LatvianModder on 11.02.2016.
 */
public abstract class FTBUPlayerData implements ICapabilityProvider
{
    public final ConfigEntryBool renderBadge;

    public FTBUPlayerData()
    {
        renderBadge = new ConfigEntryBool(true);
    }

    public static FTBUPlayerData get(ForgePlayer p)
    {
        return p.hasCapability(FTBUCapabilities.FTBU_PLAYER_DATA, null) ? p.getCapability(FTBUCapabilities.FTBU_PLAYER_DATA, null) : null;
    }

    public FTBUPlayerDataMP toMP()
    {
        return null;
    }

    public FTBUPlayerDataSP toSP()
    {
        return null;
    }

    @Override
    public final boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == FTBUCapabilities.FTBU_PLAYER_DATA;
    }

    @Override
    public final <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if(capability == FTBUCapabilities.FTBU_PLAYER_DATA)
        {
            return (T) this;
        }

        return null;
    }

    public void writeSyncData(ForgePlayer player, NBTTagCompound tag, boolean self)
    {
    }

    public void readSyncData(ForgePlayer player, NBTTagCompound tag, boolean self)
    {
    }
}