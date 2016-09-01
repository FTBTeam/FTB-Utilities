package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.api.IForgeTeam;
import com.feed_the_beast.ftbl.api.config.ConfigEntryBool;
import com.feed_the_beast.ftbl.api.config.ConfigEntryEnum;
import com.feed_the_beast.ftbl.api.config.ConfigGroup;
import com.feed_the_beast.ftbl.api.security.EnumTeamPrivacyLevel;
import com.feed_the_beast.ftbu.FTBUCapabilities;
import com.latmod.lib.io.Bits;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nullable;

/**
 * Created by LatvianModder on 11.02.2016.
 */
public class FTBUTeamData implements ICapabilitySerializable<NBTTagCompound>
{
    private final ConfigEntryEnum<EnumTeamPrivacyLevel> blocks;
    private final ConfigEntryBool disableExplosions;
    private final ConfigEntryBool fakePlayers;

    public FTBUTeamData()
    {
        blocks = new ConfigEntryEnum<>(EnumTeamPrivacyLevel.ALLIES, EnumTeamPrivacyLevel.NAME_MAP);
        disableExplosions = new ConfigEntryBool(false);
        fakePlayers = new ConfigEntryBool(false);
    }

    public static FTBUTeamData get(IForgeTeam t)
    {
        return t.hasCapability(FTBUCapabilities.FTBU_TEAM_DATA, null) ? t.getCapability(FTBUCapabilities.FTBU_TEAM_DATA, null) : null;
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

    @Override
    public void deserializeNBT(NBTTagCompound tag)
    {
        byte flags = tag.getByte("Flags");

        disableExplosions.set(Bits.getFlag(flags, 1));
        fakePlayers.set(Bits.getFlag(flags, 2));

        blocks.setIndex(tag.hasKey("BlockSecurity") ? tag.getByte("BlockSecurity") : blocks.defValue);
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound tag = new NBTTagCompound();

        int flags = 0;

        flags = Bits.setFlag(flags, (byte) 1, disableExplosions.getAsBoolean());
        flags = Bits.setFlag(flags, (byte) 2, fakePlayers.getAsBoolean());

        if(flags != 0)
        {
            tag.setByte("Flags", (byte) flags);
        }

        if(!blocks.isDefault())
        {
            tag.setByte("BlockSecurity", (byte) blocks.getIndex());
        }

        return tag;
    }

    public EnumTeamPrivacyLevel getBlocks()
    {
        return blocks.get();
    }

    public boolean disableExplosions()
    {
        return disableExplosions.getAsBoolean();
    }

    public boolean allowFakePlayers()
    {
        return fakePlayers.getAsBoolean();
    }

    public ConfigGroup createConfigGroup()
    {
        ConfigGroup group = new ConfigGroup();
        group.add("blocks", blocks);
        group.add("disable_explosions", disableExplosions);
        group.add("fake_players", fakePlayers);
        return group;
    }
}