package com.feed_the_beast.ftbu.world.chunks;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbu.api.IClaimedChunk;
import com.feed_the_beast.ftbu.world.data.FTBUTeamData;
import com.latmod.lib.math.ChunkDimPos;
import net.minecraft.util.math.BlockPos;

public final class ClaimedChunk implements IClaimedChunk
{
    private final ChunkDimPos pos;
    private final IForgePlayer owner;
    private boolean loaded, forced;

    public ClaimedChunk(IForgePlayer o, ChunkDimPos p)
    {
        owner = o;
        pos = p;
    }

    @Override
    public boolean equals(Object o)
    {
        return o != null && (o == this || (o instanceof ClaimedChunk && pos.equalsChunkDimPos(((ClaimedChunk) o).pos)));
    }

    @Override
    public String toString()
    {
        return pos.toString();
    }

    @Override
    public int hashCode()
    {
        return pos.hashCode();
    }

    public ChunkDimPos getPos()
    {
        return pos;
    }

    public IForgePlayer getOwner()
    {
        return owner;
    }

    public boolean isLoaded()
    {
        return loaded;
    }

    public void setLoaded(boolean loaded)
    {
        this.loaded = loaded;
    }

    public boolean isForced()
    {
        return forced;
    }

    public void setForced(boolean forced)
    {
        this.forced = forced;
    }

    public boolean isChunkOwner(IForgePlayer p)
    {
        return p != null && p.equalsPlayer(owner);
    }

    public boolean canInteract(IForgePlayer p, boolean leftClick, BlockPos pos)
    {
        if(owner.equalsPlayer(p))
        {
            return true;
        }
        else if(owner.getTeam() == null)
        {
            return true;
        }
        else if(p.isFake())
        {
            return FTBUTeamData.get(owner.getTeam()).allowFakePlayers();
        }
        /*else if(p.isOnline() && PermissionAPI.hasPermission(p.getProfile(), FTBLibPermissions.INTERACT_SECURE, false, new Context(p.getPlayer(), pos)))
        {
            return true;
        }*/
        else if(p.isOnline() && p.getPlayer().capabilities.isCreativeMode)
        {
            return true;
        }

        return owner.getTeam().getStatus(p).isAlly();
    }
}