package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.api.IForgeTeam;
import com.feed_the_beast.ftbl.api.config.IConfigKey;
import com.feed_the_beast.ftbl.api.config.IConfigTree;
import com.feed_the_beast.ftbl.api.security.EnumTeamPrivacyLevel;
import com.feed_the_beast.ftbl.lib.EnumNameMap;
import com.feed_the_beast.ftbl.lib.config.ConfigKey;
import com.feed_the_beast.ftbl.lib.config.PropertyBool;
import com.feed_the_beast.ftbl.lib.config.PropertyEnum;
import com.feed_the_beast.ftbl.lib.config.PropertyEnumAbstract;
import com.feed_the_beast.ftbl.lib.io.Bits;
import com.feed_the_beast.ftbu.FTBLibIntegration;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

/**
 * Created by LatvianModder on 11.02.2016.
 */
public class FTBUTeamData implements INBTSerializable<NBTBase>
{
    private static final EnumTeamPrivacyLevel DEF_BLOCKS_LEVEL = EnumTeamPrivacyLevel.ALLIES;
    private static final IConfigKey DISABLE_EXPLOSIONS = new ConfigKey("ftbu.disable_explosions", new PropertyBool(false));
    private static final IConfigKey FAKE_PLAYERS = new ConfigKey("ftbu.fake_players", new PropertyBool(false));
    private static final IConfigKey BLOCKS = new ConfigKey("ftbu.blocks", new PropertyEnum<>(EnumTeamPrivacyLevel.NAME_MAP, DEF_BLOCKS_LEVEL));
    private static final byte FLAG_DISABLE_EXPLOSIONS = 1;
    private static final byte FLAG_FAKE_PLAYERS = 2;

    @Nullable
    public static FTBUTeamData get(IForgeTeam t)
    {
        return (FTBUTeamData) t.getData(FTBLibIntegration.FTBU_DATA);
    }

    private byte flags = 0;

    public FTBUTeamData()
    {
        setBlocksLevel(EnumTeamPrivacyLevel.ALLIES);
        flags |= FLAG_FAKE_PLAYERS;
    }

    @Override
    public NBTBase serializeNBT()
    {
        return new NBTTagByte(flags);
    }

    @Override
    public void deserializeNBT(NBTBase nbt0)
    {
        if(nbt0 instanceof NBTTagCompound)
        {
            flags = ((NBTTagCompound) nbt0).getByte("Flags");
        }
        else
        {
            flags = ((NBTPrimitive) nbt0).getByte();
        }
    }

    public void setBlocksLevel(@Nullable EnumTeamPrivacyLevel level)
    {
        if(level == null)
        {
            level = DEF_BLOCKS_LEVEL;
        }

        flags &= 0xF3; // 11110011
        flags |= level.ordinal() << 2;
    }

    public EnumTeamPrivacyLevel getBlocks()
    {
        return EnumTeamPrivacyLevel.VALUES[(flags >> 2) & 3];
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
            public void setBoolean(boolean v)
            {
                flags = Bits.setFlag(flags, FLAG_DISABLE_EXPLOSIONS, v);
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
            public void setBoolean(boolean v)
            {
                flags = Bits.setFlag(flags, FLAG_FAKE_PLAYERS, v);
            }
        });

        tree.add(BLOCKS, new PropertyEnumAbstract<EnumTeamPrivacyLevel>()
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
                return getBlocks();
            }

            @Override
            public void set(@Nullable EnumTeamPrivacyLevel enumLevel)
            {
                setBlocksLevel(enumLevel);
            }
        });

        //tree.add("ftbu.blocks", blocks);
        //tree.add("ftbu.disable_explosions", disableExplosions);
        //tree.add("ftbu.fake_players", fakePlayers);
    }
}