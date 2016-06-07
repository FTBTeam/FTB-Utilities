package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.api.ForgePlayer;
import com.feed_the_beast.ftbl.api.config.ConfigEntryBool;
import com.feed_the_beast.ftbl.util.BlockDimPos;
import com.feed_the_beast.ftbl.util.LMNBTUtils;
import com.feed_the_beast.ftbu.FTBUPermissions;
import latmod.lib.Bits;
import latmod.lib.IntMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LatvianModder on 23.02.2016.
 */
public class FTBUPlayerDataMP extends FTBUPlayerData implements INBTSerializable<NBTTagCompound>
{
    public final ConfigEntryBool chatLinks;
    private Map<String, BlockDimPos> homes;

    public FTBUPlayerDataMP()
    {
        chatLinks = new ConfigEntryBool("chat_links", true);
    }

    @Override
    public FTBUPlayerDataMP toMP()
    {
        return this;
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag)
    {
        byte flags = tag.getByte("Flags");

        renderBadge.set(Bits.getBit(flags, (byte) 0));
        chatLinks.set(Bits.getBit(flags, (byte) 1));

        if(tag.hasKey("Homes"))
        {
            homes = new HashMap<>();

            NBTTagCompound tag1 = (NBTTagCompound) tag.getTag("Homes");

            if(tag1 != null && !tag1.hasNoTags())
            {
                for(String s1 : LMNBTUtils.getMapKeys(tag1))
                {
                    setHome(s1, new BlockDimPos(tag1.getIntArray(s1)));
                }
            }
        }
        else
        {
            homes = null;
        }
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound tag = new NBTTagCompound();

        byte flags = 0;

        flags = Bits.setBit(flags, (byte) 0, renderBadge.getAsBoolean());
        flags = Bits.setBit(flags, (byte) 1, chatLinks.getAsBoolean());

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

        return tag;
    }

    @Override
    public void writeSyncData(ForgePlayer player, NBTTagCompound tag, boolean self)
    {
        IntMap map = new IntMap();

        if(renderBadge.getAsBoolean())
        {
            map.put(0, 1);
        }

        if(self)
        {
            map.putIfNot0(10, FTBUWorldDataMP.chunks.getClaimedChunks(player.getProfile().getId()));
            map.putIfNot0(11, FTBUWorldDataMP.chunks.getLoadedChunks(player.getProfile().getId()));
            map.putIfNot0(12, FTBUPermissions.claims_max_chunks.get(player.getProfile()));
            map.putIfNot0(13, FTBUPermissions.chunkloader_max_chunks.get(player.getProfile()));
        }

        if(!map.list.isEmpty())
        {
            tag.setIntArray("F", map.toArray());
        }
    }

    public Collection<String> listHomes()
    {
        if(homes == null || homes.isEmpty())
        {
            return Collections.EMPTY_SET;
        }

        return homes.keySet();
    }

    public BlockDimPos getHome(String s)
    {
        return homes == null ? null : homes.get(s);
    }

    public boolean setHome(String s, BlockDimPos pos)
    {
        if(pos == null)
        {
            return homes.remove(s) != null;
        }

        return homes.put(s, pos.copy()) == null;
    }

    public int homesSize()
    {
        return homes == null ? 0 : homes.size();
    }
}
