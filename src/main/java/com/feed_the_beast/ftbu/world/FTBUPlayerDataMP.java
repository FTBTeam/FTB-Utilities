package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.api.ForgePlayer;
import com.feed_the_beast.ftbl.api.ForgeWorldMP;
import com.feed_the_beast.ftbl.api.config.ConfigEntryBool;
import com.feed_the_beast.ftbl.util.BlockDimPos;
import com.feed_the_beast.ftbl.util.ChunkDimPos;
import com.feed_the_beast.ftbl.util.LMNBTUtils;
import com.feed_the_beast.ftbu.FTBUPermissions;
import latmod.lib.IntMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
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

        if(tag.hasKey("ClaimedChunks"))
        {
            NBTTagList tag1 = tag.getTagList("ClaimedChunks", Constants.NBT.TAG_INT_ARRAY);

            for(int i = 0; i < tag1.tagCount(); i++)
            {
                int[] ai = tag1.getIntArrayAt(i);

                if(ai.length >= 3)
                {
                    ClaimedChunk chunk = new ClaimedChunk(ForgeWorldMP.inst, ForgeWorldMP.currentPlayer, new ChunkDimPos(ai[0], ai[1], ai[2]));

                    if(ai.length >= 4)
                    {
                        chunk.loaded = ai[3] != 0;
                    }

                    ClaimedChunks.inst.putChunk(chunk);
                }
            }
        }
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound tag = new NBTTagCompound();

        byte flags = 0;

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

        Collection<ClaimedChunk> chunks = ClaimedChunks.inst.getChunks(ForgeWorldMP.currentPlayer.getProfile().getId(), null);

        if(!chunks.isEmpty())
        {
            NBTTagList tag1 = new NBTTagList();

            for(ClaimedChunk c : chunks)
            {
                int ai[] = c.loaded ? new int[4] : new int[3];

                ai[0] = c.pos.dim;
                ai[1] = c.pos.chunkXPos;
                ai[2] = c.pos.chunkZPos;

                if(c.loaded)
                {
                    ai[3] = flags & 0xFF;
                }

                tag1.appendTag(new NBTTagIntArray(ai));
            }

            tag.setTag("ClaimedChunks", tag1);
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
            map.putIfNot0(10, ClaimedChunks.inst.getClaimedChunks(player.getProfile().getId()));
            map.putIfNot0(11, ClaimedChunks.inst.getLoadedChunks(player.getProfile().getId(), true));
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
