package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.api.IForgeTeam;
import com.feed_the_beast.ftbl.api.config.IConfigKey;
import com.feed_the_beast.ftbl.api.config.IConfigTree;
import com.feed_the_beast.ftbl.api.security.EnumTeamPrivacyLevel;
import com.feed_the_beast.ftbl.api_impl.config.ConfigKey;
import com.feed_the_beast.ftbl.api_impl.config.PropertyBool;
import com.feed_the_beast.ftbl.api_impl.config.PropertyEnum;
import com.feed_the_beast.ftbl.api_impl.config.PropertyEnumAbstract;
import com.feed_the_beast.ftbu.FTBUCapabilities;
import com.latmod.lib.EnumNameMap;
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
    private static final EnumTeamPrivacyLevel DEF_BLOCKS_LEVEL = EnumTeamPrivacyLevel.ALLIES;
    private static final IConfigKey DISABLE_EXPLOSIONS = new ConfigKey("ftbu.disable_explosions", new PropertyBool(false));
    private static final IConfigKey FAKE_PLAYERS = new ConfigKey("ftbu.fake_players", new PropertyBool(false));
    private static final IConfigKey BLOCKS = new ConfigKey("ftbu.blocks", new PropertyEnum<>(EnumTeamPrivacyLevel.NAME_MAP, DEF_BLOCKS_LEVEL));
    private static final byte FLAG_DISABLE_EXPLOSIONS = 1;
    private static final byte FLAG_FAKE_PLAYERS = 2;

    private byte flags = (byte) (DEF_BLOCKS_LEVEL.ordinal() << 2);
    private final PropertyEnumAbstract<EnumTeamPrivacyLevel> blocks;

    public FTBUTeamData()
    {
        blocks = new PropertyEnumAbstract<EnumTeamPrivacyLevel>()
        {
            @Override
            public EnumNameMap<EnumTeamPrivacyLevel> getNameMap()
            {
                return EnumTeamPrivacyLevel.NAME_MAP;
            }

            @Nullable
            @Override
            public EnumTeamPrivacyLevel get()
            {
                return EnumTeamPrivacyLevel.VALUES[(flags >> 2) & 3];
            }

            @Override
            public void set(@Nullable EnumTeamPrivacyLevel enumLevel)
            {
                if(enumLevel == null)
                {
                    enumLevel = DEF_BLOCKS_LEVEL;
                }

                flags &= 0xFFFF00FF;
                flags |= enumLevel.ordinal() << 2;
            }
        };
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
        flags = tag.getByte("Flags");
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound tag = new NBTTagCompound();

        if(flags != 0)
        {
            tag.setByte("Flags", flags);
        }

        return tag;
    }

    public EnumTeamPrivacyLevel getBlocks()
    {
        return (EnumTeamPrivacyLevel) blocks.getValue();
    }

    public boolean disableExplosions()
    {
        return Bits.getFlag(flags, FLAG_DISABLE_EXPLOSIONS);
    }

    public boolean allowFakePlayers()
    {
        return Bits.getFlag(flags, FLAG_FAKE_PLAYERS);
    }

    public void addConfig(IConfigTree tree)
    {
        tree.add(DISABLE_EXPLOSIONS, new PropertyBool(true)
        {
            @Override
            public boolean getBoolean()
            {
                return disableExplosions();
            }

            @Override
            public void set(boolean v)
            {
                flags = (byte) Bits.setFlag(flags, FLAG_DISABLE_EXPLOSIONS, v);
            }
        });

        tree.add(FAKE_PLAYERS, new PropertyBool(true)
        {
            @Override
            public boolean getBoolean()
            {
                return allowFakePlayers();
            }

            @Override
            public void set(boolean v)
            {
                flags = (byte) Bits.setFlag(flags, FLAG_FAKE_PLAYERS, v);
            }
        });

        tree.add(BLOCKS, blocks);

        //tree.add("ftbu.blocks", blocks);
        //tree.add("ftbu.disable_explosions", disableExplosions);
        //tree.add("ftbu.fake_players", fakePlayers);
    }
}