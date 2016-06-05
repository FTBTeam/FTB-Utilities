package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.api.ForgePlayer;
import com.feed_the_beast.ftbl.api.ForgePlayerMP;
import com.feed_the_beast.ftbl.api.ForgeWorld;
import com.feed_the_beast.ftbl.api.LangKey;
import com.feed_the_beast.ftbl.util.ChunkDimPos;
import net.minecraft.util.math.BlockPos;

public final class ClaimedChunk
{
    public static final LangKey LANG_WILDERNESS = new LangKey("ftbu.chunktype.wilderness");
    public static final LangKey LANG_CLAIMED = new LangKey("ftbu.chunktype.claimed");
    public static final LangKey LANG_LOADED = new LangKey("ftbu.chunktype.loaded");

    public final ForgeWorld world;
    public final ChunkDimPos pos;
    public final ForgePlayer owner;
    public boolean loaded, forced;

    public ClaimedChunk(ForgeWorld w, ForgePlayer o, ChunkDimPos p)
    {
        world = w;
        pos = p;
        owner = o;
    }

    @Override
    public boolean equals(Object o)
    {
        return o != null && (o == this || (o instanceof ClaimedChunk && pos.equalsChunk(((ClaimedChunk) o).pos)));
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

    public boolean isChunkOwner(ForgePlayer p)
    {
        return p != null && p.equalsPlayer(owner);
    }

    public boolean canInteract(ForgePlayerMP p, boolean leftClick, BlockPos pos)
    {
        if(owner.equalsPlayer(p))
        {
            return true;
        }
        else if(!owner.hasTeam())
        {
            return true;
        }
        else if(p.isFake())
        {
            return FTBUTeamData.get(owner.getTeam()).toMP().fakePlayers.getAsBoolean();
        }
        /*else if(p.isOnline() && PermissionAPI.hasPermission(p.getProfile(), FTBLibPermissions.INTERACT_SECURE, false, new Context(p.getPlayer(), pos)))
        {
            return true;
        }*/

        return owner.getTeam().getStatus(p).isAlly();
    }
}