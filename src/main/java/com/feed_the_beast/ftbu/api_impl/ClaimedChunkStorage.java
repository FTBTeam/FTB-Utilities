package com.feed_the_beast.ftbu.api_impl;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.IForgeTeam;
import com.feed_the_beast.ftbl.lib.math.BlockPosContainer;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbl.lib.util.LMStringUtils;
import com.feed_the_beast.ftbu.FTBLibIntegration;
import com.feed_the_beast.ftbu.FTBUCommon;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.api.chunks.BlockInteractionType;
import com.feed_the_beast.ftbu.api.chunks.IChunkUpgrade;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunk;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunkStorage;
import com.feed_the_beast.ftbu.world.FTBUTeamData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by LatvianModder on 07.06.2016.
 */
public enum ClaimedChunkStorage implements IClaimedChunkStorage, INBTSerializable<NBTTagCompound>
{
    INSTANCE;

    private static final Map<ChunkDimPos, IClaimedChunk> MAP = new HashMap<>();
    private static final Map<ChunkDimPos, IClaimedChunk> MAP_MIRROR = Collections.unmodifiableMap(MAP);

    public void init()
    {
        clear();
    }

    public void clear()
    {
        MAP.clear();
    }

    @Override
    @Nullable
    public IClaimedChunk getChunk(ChunkDimPos pos)
    {
        return MAP.get(pos);
    }

    @Override
    public void setChunk(ChunkDimPos pos, @Nullable IClaimedChunk chunk)
    {
        if(chunk == null)
        {
            MAP.remove(pos);
        }
        else
        {
            MAP.put(pos, chunk);
        }
    }

    @Override
    public Collection<IClaimedChunk> getChunks(@Nullable IForgePlayer owner)
    {
        if(MAP.isEmpty())
        {
            return Collections.emptyList();
        }
        else if(owner == null)
        {
            return MAP_MIRROR.values();
        }

        Collection<IClaimedChunk> c = new ArrayList<>();

        MAP.forEach((key, value) ->
        {
            if(value.getOwner().equalsPlayer(owner))
            {
                c.add(value);
            }
        });

        return c;
    }

    @Override
    public boolean canPlayerInteract(EntityPlayerMP ep, EnumHand hand, BlockPosContainer block, BlockInteractionType type)
    {
        if(FTBUPermissions.canModifyBlock(ep, hand, block, type))
        {
            return true;
        }

        IClaimedChunk chunk = getChunk(new ChunkDimPos(block.getPos(), ep.dimension));

        if(chunk == null)
        {
            return true;
        }

        IForgePlayer player = FTBLibIntegration.API.getUniverse().getPlayer(ep);

        if(chunk.getOwner().equalsPlayer(player))
        {
            return true;
        }

        IForgeTeam team = chunk.getOwner().getTeam();

        if(team == null)
        {
            return true;
        }
        else if(player.isFake())
        {
            return FTBUTeamData.get(team).allowFakePlayers();
        }

        return team.canInteract(player.getId(), FTBUTeamData.get(team).getBlocks());
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();

        Map<UUID, Collection<IClaimedChunk>> map1 = new HashMap<>();

        MAP.forEach((key, value) ->
        {
            Collection<IClaimedChunk> c = map1.get(value.getOwner().getId());

            if(c == null)
            {
                c = new ArrayList<>();
                map1.put(value.getOwner().getId(), c);
            }

            c.add(value);
        });

        map1.forEach((key, value) ->
        {
            NBTTagList list = new NBTTagList();

            for(IClaimedChunk c : value)
            {
                ChunkDimPos p = c.getPos();

                int flags = 0;

                for(IChunkUpgrade upgrade : FTBUCommon.CHUNK_UPGRADES)
                {
                    if(upgrade != null && c.hasUpgrade(upgrade))
                    {
                        flags |= (1 << upgrade.getId());
                    }
                }

                int ai[] = flags != 0 ? new int[4] : new int[3];
                ai[0] = p.dim;
                ai[1] = p.posX;
                ai[2] = p.posZ;

                if(flags != 0)
                {
                    ai[3] = flags;
                }

                list.appendTag(new NBTTagIntArray(ai));
            }

            nbt.setTag(LMStringUtils.fromUUID(key), list);
        });

        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        MAP.clear();

        for(String s : nbt.getKeySet())
        {
            IForgePlayer player = FTBLibIntegration.API.getUniverse().getPlayer(LMStringUtils.fromString(s));

            if(player != null)
            {
                NBTTagList list = nbt.getTagList(s, Constants.NBT.TAG_INT_ARRAY);

                for(int i = 0; i < list.tagCount(); i++)
                {
                    int[] ai = list.getIntArrayAt(i);

                    if(ai.length >= 3)
                    {
                        ClaimedChunk chunk = new ClaimedChunk(new ChunkDimPos(ai[1], ai[2], ai[0]), player, ai.length >= 4 ? ai[3] : 0);
                        MAP.put(chunk.getPos(), chunk);
                    }
                }
            }
        }
    }
}