package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.api.IForgeTeam;
import com.feed_the_beast.ftbl.api.config.IConfigKey;
import com.feed_the_beast.ftbl.api.config.IConfigTree;
import com.feed_the_beast.ftbl.api.security.EnumTeamPrivacyLevel;
import com.feed_the_beast.ftbl.lib.EnumNameMap;
import com.feed_the_beast.ftbl.lib.INBTData;
import com.feed_the_beast.ftbl.lib.config.ConfigKey;
import com.feed_the_beast.ftbl.lib.config.PropertyBool;
import com.feed_the_beast.ftbl.lib.config.PropertyEnum;
import com.feed_the_beast.ftbl.lib.config.PropertyEnumAbstract;
import com.feed_the_beast.ftbl.lib.io.Bits;
import com.feed_the_beast.ftbu.FTBUFinals;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * Created by LatvianModder on 11.02.2016.
 */
public class FTBUTeamData implements INBTData
{
    public static final ResourceLocation ID = new ResourceLocation(FTBUFinals.MOD_ID, "data");
    private static final EnumTeamPrivacyLevel DEF_BLOCKS_LEVEL = EnumTeamPrivacyLevel.ALLIES;
    private static final IConfigKey DISABLE_EXPLOSIONS = new ConfigKey("ftbu.disable_explosions", new PropertyBool(false));
    private static final IConfigKey FAKE_PLAYERS = new ConfigKey("ftbu.fake_players", new PropertyBool(false));
    private static final IConfigKey BLOCKS = new ConfigKey("ftbu.blocks", new PropertyEnum<>(EnumTeamPrivacyLevel.NAME_MAP, DEF_BLOCKS_LEVEL));
    private static final byte FLAG_DISABLE_EXPLOSIONS = 1;
    private static final byte FLAG_FAKE_PLAYERS = 2;

    @Nullable
    public static FTBUTeamData get(IForgeTeam t)
    {
        return (FTBUTeamData) t.getData(ID);
    }

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

    @Override
    public ResourceLocation getID()
    {
        return ID;
    }

    @Override
    public void writeData(NBTTagCompound nbt)
    {
        if(flags != 0)
        {
            nbt.setByte("Flags", flags);
        }
    }

    @Override
    public void readData(NBTTagCompound nbt)
    {
        flags = nbt.getByte("Flags");
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

        tree.add(BLOCKS, blocks);

        //tree.add("ftbu.blocks", blocks);
        //tree.add("ftbu.disable_explosions", disableExplosions);
        //tree.add("ftbu.fake_players", fakePlayers);
    }
}