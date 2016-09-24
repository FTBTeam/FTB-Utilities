package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.config.IConfigKey;
import com.feed_the_beast.ftbl.api.config.IConfigTree;
import com.feed_the_beast.ftbu.FTBUCapabilities;
import com.latmod.lib.config.ConfigKey;
import com.latmod.lib.config.PropertyBool;
import com.latmod.lib.io.Bits;
import com.latmod.lib.math.BlockDimPos;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LatvianModder on 11.02.2016.
 */
public class FTBUPlayerData implements ICapabilitySerializable<NBTTagCompound>
{
    private static final IConfigKey RENDER_BADGE = new ConfigKey("ftbu.render_badge", new PropertyBool(true));
    private static final IConfigKey CHAT_LINKS = new ConfigKey("ftbu.chat_links", new PropertyBool(true));
    private static final byte FLAG_RENDER_BADGE = 1;
    private static final byte FLAG_CHAT_LINKS = 2;

    private byte flags = FLAG_RENDER_BADGE | FLAG_CHAT_LINKS;
    public BlockDimPos lastDeath, lastSafePos;
    public IForgePlayer lastChunkOwner;
    private Map<String, BlockDimPos> homes;

    public static FTBUPlayerData get(IForgePlayer p)
    {
        return p.hasCapability(FTBUCapabilities.FTBU_PLAYER_DATA, null) ? p.getCapability(FTBUCapabilities.FTBU_PLAYER_DATA, null) : null;
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

    @Override
    public void deserializeNBT(NBTTagCompound tag)
    {
        flags = tag.getByte("Flags");

        if(tag.hasKey("Homes"))
        {
            homes = new HashMap<>();

            NBTTagCompound tag1 = (NBTTagCompound) tag.getTag("Homes");

            if(tag1 != null && !tag1.hasNoTags())
            {
                for(String s1 : tag1.getKeySet())
                {
                    setHome(s1.toLowerCase(), new BlockDimPos(tag1.getIntArray(s1)));
                }
            }
        }
        else
        {
            homes = null;
        }

        lastDeath = null;
        if(tag.hasKey("LastDeath"))
        {
            int[] ai = tag.getIntArray("LastDeath");
            lastDeath = (ai.length == 4) ? new BlockDimPos(ai) : null;
        }
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound tag = new NBTTagCompound();

        if(flags != 0)
        {
            tag.setByte("Flags", flags);
        }

        if(homes != null && !homes.isEmpty())
        {
            NBTTagCompound tag1 = new NBTTagCompound();

            for(Map.Entry<String, BlockDimPos> e : homes.entrySet())
            {
                tag1.setIntArray(e.getKey(), e.getValue().toIntArray());
            }

            tag.setTag("Homes", tag1);
        }

        if(lastDeath != null)
        {
            tag.setIntArray("LastDeath", lastDeath.toIntArray());
        }

        return tag;
    }

    public Collection<String> listHomes()
    {
        if(homes == null || homes.isEmpty())
        {
            return Collections.emptySet();
        }

        return homes.keySet();
    }

    public BlockDimPos getHome(String s)
    {
        return homes == null ? null : homes.get(s.toLowerCase());
    }

    public boolean setHome(String s, BlockDimPos pos)
    {
        if(pos == null)
        {
            return homes != null && homes.remove(s) != null;
        }

        if(homes == null)
        {
            homes = new HashMap<>();
        }

        return homes.put(s, pos.copy()) == null;
    }

    public int homesSize()
    {
        return homes == null ? 0 : homes.size();
    }

    public boolean renderBadge()
    {
        return Bits.getFlag(flags, FLAG_RENDER_BADGE);
    }

    public boolean chatLinks()
    {
        return Bits.getFlag(flags, FLAG_CHAT_LINKS);
    }

    public void addConfig(IConfigTree tree)
    {
        tree.add(RENDER_BADGE, new PropertyBool(true)
        {
            @Override
            public boolean getBoolean()
            {
                return renderBadge();
            }

            @Override
            public void setBoolean(boolean v)
            {
                flags = (byte) Bits.setFlag(flags, FLAG_RENDER_BADGE, v);
            }
        });

        tree.add(CHAT_LINKS, new PropertyBool(true)
        {
            @Override
            public boolean getBoolean()
            {
                return chatLinks();
            }

            @Override
            public void setBoolean(boolean v)
            {
                flags = (byte) Bits.setFlag(flags, FLAG_CHAT_LINKS, v);
            }
        });
    }
}