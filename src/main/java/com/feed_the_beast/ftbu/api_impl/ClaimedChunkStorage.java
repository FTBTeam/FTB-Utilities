package com.feed_the_beast.ftbu.api_impl;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.gui.IMouseButton;
import com.feed_the_beast.ftbl.api.rankconfig.RankConfigAPI;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunkStorage;
import com.feed_the_beast.ftbu.world.FTBUTeamData;
import com.latmod.lib.math.BlockDimPos;
import com.latmod.lib.math.ChunkDimPos;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by LatvianModder on 07.06.2016.
 */
public class ClaimedChunkStorage implements IClaimedChunkStorage
{
    public static ClaimedChunkStorage INSTANCE;

    private final Map<ChunkDimPos, IForgePlayer> map = new HashMap<>();
    private final Map<IForgePlayer, Collection<ChunkDimPos>> invertedMap = new HashMap<>();

    @Override
    public Set<Map.Entry<ChunkDimPos, IForgePlayer>> getAllChunks()
    {
        return map.entrySet();
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
            IForgePlayer owner0 = map.remove(pos);

            if(owner0 != null)
            {
                Collection<ChunkDimPos> c = invertedMap.get(owner0);

                if(c != null)
                {
                    c.remove(pos);
                }
            }
        }
        else
        {
            map.put(pos, owner);

            Collection<ChunkDimPos> c = invertedMap.get(owner);

            if(c == null)
            {
                c = new HashSet<>();
                invertedMap.put(owner, c);
            }

            c.add(pos);
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

        Collection<ChunkDimPos> c = invertedMap.get(player);
        return (c == null) ? Collections.emptyList() : c;
    }

    @Override
    public int getClaimedChunks(IForgePlayer playerID)
    {
        Collection<ChunkDimPos> c = invertedMap.get(playerID);
        return (c == null) ? 0 : c.size();
    }

    @Override
    public boolean canPlayerInteract(EntityPlayerMP entityPlayer, BlockPos pos, IMouseButton button)
    {
        if(entityPlayer.capabilities.isCreativeMode)
        {
            return true;
        }

        if(button.isLeft())
        {
            for(String e : (List<String>) RankConfigAPI.getRankConfig(entityPlayer, FTBUPermissions.CLAIMS_BREAK_WHITELIST).getValue())
            {
                if(e.equals(Block.REGISTRY.getNameForObject(entityPlayer.worldObj.getBlockState(pos).getBlock()).toString()))
                {
                    return true;
                }
            }
        }

        ChunkDimPos chunkDimPos = new BlockDimPos(pos, entityPlayer.dimension).toChunkPos();

        IForgePlayer owner = getChunkOwner(chunkDimPos);

        if(owner == null)
        {
            return true;
        }

        IForgePlayer player = FTBLibAPI.get().getUniverse().getPlayer(entityPlayer);

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
    public NBTTagList serializeNBT()
    {
        NBTTagList nbt = new NBTTagList();
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagList nbt)
    {
    }
}
