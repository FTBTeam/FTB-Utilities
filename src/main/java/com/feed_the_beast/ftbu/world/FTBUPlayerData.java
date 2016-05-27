package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.api.ForgePlayer;
import com.feed_the_beast.ftbl.util.PrivacyLevel;
import com.feed_the_beast.ftbu.FTBUCapabilities;
import latmod.lib.Bits;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

/**
 * Created by LatvianModder on 11.02.2016.
 */
public abstract class FTBUPlayerData implements ICapabilityProvider
{
    public static final byte RENDER_BADGE = 1;
    public static final byte CHAT_LINKS = 2;
    public static final byte EXPLOSIONS = 3;
    public static final byte FAKE_PLAYERS = 4;
    public PrivacyLevel blocks;
    protected byte flags = 0;

    public FTBUPlayerData()
    {
        blocks = PrivacyLevel.TEAM;
    }

    public static FTBUPlayerData get(ForgePlayer p)
    {
        return p.hasCapability(FTBUCapabilities.FTBU_PLAYER_DATA, null) ? p.getCapability(FTBUCapabilities.FTBU_PLAYER_DATA, null) : null;
    }

    public boolean getFlag(byte f)
    {
        return Bits.getBit(flags, f);
    }

    public void setFlag(byte f, boolean b)
    {
        flags = Bits.setBit(flags, f, b);
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
    public final boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        return capability == FTBUCapabilities.FTBU_PLAYER_DATA;
    }

    @Override
    public final <T> T getCapability(Capability<T> capability, EnumFacing facing)
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