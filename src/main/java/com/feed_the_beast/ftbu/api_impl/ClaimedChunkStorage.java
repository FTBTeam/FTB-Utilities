package com.feed_the_beast.ftbu.api_impl;

import com.feed_the_beast.ftbl.api.EnumTeamStatus;
import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.IForgeTeam;
import com.feed_the_beast.ftbl.api.gui.IMouseButton;
import com.feed_the_beast.ftbl.lib.math.BlockDimPos;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbl.lib.util.LMStringUtils;
import com.feed_the_beast.ftbu.FTBLibIntegration;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunk;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunkStorage;
import com.feed_the_beast.ftbu.world.FTBUTeamData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
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
    public boolean canPlayerInteract(EntityPlayerMP entityPlayer, BlockPos pos, IMouseButton button)
    {
        if(entityPlayer.capabilities.isCreativeMode)
        {
            return true;
        }

        ChunkDimPos chunkDimPos = new BlockDimPos(pos, entityPlayer.dimension).toChunkPos();

        IClaimedChunk chunk = getChunk(chunkDimPos);

        if(chunk == null)
        {
            //return FTBUPermissions.allowDimension(entityPlayer.getGameProfile(), chunkDimPos.dim);
            return true;
        }

        IForgePlayer player = FTBLibIntegration.API.getUniverse().getPlayer(entityPlayer);

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
        /*else if(p.isOnline() && PermissionAPI.hasPermission(p.getProfile(), FTBLibPermissions.INTERACT_SECURE, false, new Context(p.getPlayer(), pos)))
        {
            return true;
        }*/

        return team.hasStatus(player, EnumTeamStatus.ALLY) || team.hasStatus(player, EnumTeamStatus.MEMBER) || team.hasStatus(player, EnumTeamStatus.OWNER);
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();

        Map<UUID, Collection<IClaimedChunk>> map1 = new HashMap<>();

        MAP.forEach((key, value) ->
        {
            Collection<IClaimedChunk> c = map1.get(value.getOwner().getProfile().getId());

            if(c == null)
            {
                c = new ArrayList<>();
                map1.put(value.getOwner().getProfile().getId(), c);
            }

            c.add(value);
        });

        map1.forEach((key, value) ->
        {
            NBTTagList list = new NBTTagList();

            for(IClaimedChunk c : value)
            {
                ChunkDimPos p = c.getPos();
                boolean loaded = c.isLoaded();
                int ai[] = loaded ? new int[4] : new int[3];
                ai[0] = p.dim;
                ai[1] = p.posX;
                ai[2] = p.posZ;

                if(loaded)
                {
                    ai[3] = 1;
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
                        ClaimedChunk chunk = new ClaimedChunk(new ChunkDimPos(ai[1], ai[2], ai[0]), player);

                        if(ai.length >= 4 && ai[3] != 0)
                        {
                            chunk.setLoaded(true);
                        }

                        MAP.put(chunk.getPos(), chunk);
                    }
                }
            }
        }
    }
}
