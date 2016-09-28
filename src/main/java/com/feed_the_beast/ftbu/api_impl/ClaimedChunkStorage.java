package com.feed_the_beast.ftbu.api_impl;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.gui.IMouseButton;
import com.feed_the_beast.ftbl.lib.math.BlockDimPos;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbl.lib.util.LMStringUtils;
import com.feed_the_beast.ftbu.FTBLibIntegration;
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

/**
 * Created by LatvianModder on 07.06.2016.
 */
public class ClaimedChunkStorage implements IClaimedChunkStorage, INBTSerializable<NBTTagCompound>
{
    public static ClaimedChunkStorage INSTANCE;

    private final Map<ChunkDimPos, IForgePlayer> map = new HashMap<>();
    private final Map<ChunkDimPos, IForgePlayer> mapMirror = Collections.unmodifiableMap(map);

    public void clear()
    {
        map.clear();
    }

    @Override
    public Map<ChunkDimPos, IForgePlayer> getAllChunks()
    {
        return mapMirror;
    }

    @Override
    @Nullable
    public IForgePlayer getChunkOwner(ChunkDimPos pos)
    {
        return map.get(pos);
    }

    @Override
    public void setOwner(ChunkDimPos pos, @Nullable IForgePlayer owner)
    {
        if(owner == null)
        {
            map.remove(pos);
        }
        else
        {
            map.put(pos, owner);
        }
    }

    @Override
    public Collection<ChunkDimPos> getChunks(@Nullable IForgePlayer player)
    {
        if(map.isEmpty())
        {
            return Collections.emptyList();
        }
        else if(player == null)
        {
            return map.keySet();
        }

        Collection<ChunkDimPos> c = new ArrayList<>();

        map.forEach((key, value) ->
        {
            if(value.equalsPlayer(player))
            {
                c.add(key);
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

        IForgePlayer owner = getChunkOwner(chunkDimPos);

        if(owner == null)
        {
            return true;
        }

        IForgePlayer player = FTBLibIntegration.API.getUniverse().getPlayer(entityPlayer);

        if(owner.equalsPlayer(player))
        {
            return true;
        }
        else if(owner.getTeam() == null)
        {
            return true;
        }
        else if(player.isFake())
        {
            return FTBUTeamData.get(owner.getTeam()).allowFakePlayers();
        }
        /*else if(p.isOnline() && PermissionAPI.hasPermission(p.getProfile(), FTBLibPermissions.INTERACT_SECURE, false, new Context(p.getPlayer(), pos)))
        {
            return true;
        }*/

        return owner.getTeam().getStatus(player).isAlly();
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();

        Map<IForgePlayer, Collection<ChunkDimPos>> map1 = new HashMap<>();

        map.forEach((key, value) ->
        {
            Collection<ChunkDimPos> c = map1.get(value);

            if(c == null)
            {
                c = new ArrayList<>();
                map1.put(value, c);
            }

            c.add(key);
        });

        map1.forEach((key, value) ->
        {
            NBTTagList list = new NBTTagList();

            for(ChunkDimPos c : value)
            {
                list.appendTag(new NBTTagIntArray(new int[] {c.dim, c.posX, c.posZ}));
            }

            nbt.setTag(LMStringUtils.fromUUID(key.getProfile().getId()), list);
        });

        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        map.clear();

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
                        map.put(new ChunkDimPos(ai[1], ai[2], ai[0]), player);
                    }
                }
            }
        }
    }
}