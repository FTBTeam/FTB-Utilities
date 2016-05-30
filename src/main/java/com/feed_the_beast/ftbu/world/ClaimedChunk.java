package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.api.ForgePlayer;
import com.feed_the_beast.ftbl.api.ForgePlayerMP;
import com.feed_the_beast.ftbl.api.ForgeWorld;
import com.feed_the_beast.ftbl.api.LangKey;
import com.feed_the_beast.ftbl.api.permissions.Context;
import com.feed_the_beast.ftbl.util.ChunkDimPos;
import com.feed_the_beast.ftbl.util.PrivacyLevel;
import com.feed_the_beast.ftbu.FTBUPermissions;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public final class ClaimedChunk
{
    public static final LangKey LANG_WILDERNESS = new LangKey("ftbu.chunktype.wilderness");
    public static final LangKey LANG_CLAIMED = new LangKey("ftbu.chunktype.claimed");
    public static final LangKey LANG_LOADED = new LangKey("ftbu.chunktype.loaded");

    public final ForgeWorld world;
    public final ChunkDimPos pos;
    public final UUID ownerID;
    public boolean loaded, forced;

    public ClaimedChunk(ForgeWorld w, UUID o, ChunkDimPos p)
    {
        world = w;
        pos = p;
        ownerID = o;
    }

    public ForgePlayer getOwner()
    {
        return world.getPlayer(ownerID);
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
        return p != null && p.equalsPlayer(getOwner());
    }

    public boolean canInteract(ForgePlayerMP p, boolean leftClick, BlockPos pos)
    {
        ForgePlayer chunkOwner = getOwner();

        if(chunkOwner.equals(p))
        {
            return true;
        }
        else if(p.isFake())
        {
            return FTBUPlayerData.get(chunkOwner).getFlag(FTBUPlayerData.FAKE_PLAYERS);
        }

        PrivacyLevel level = FTBUPermissions.claims_forced_security.get(p.getProfile());
        if(level == null)
        {
            level = FTBUPlayerData.get(chunkOwner).blocks;
        }

        return level.canInteract(chunkOwner, p, new Context(p.getPlayer(), pos));
    }
}