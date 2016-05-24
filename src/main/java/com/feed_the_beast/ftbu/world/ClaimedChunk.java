package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.api.ForgePlayerMP;
import com.feed_the_beast.ftbl.api.ForgeWorldMP;
import com.feed_the_beast.ftbl.util.ChunkDimPos;
import latmod.lib.Bits;
import latmod.lib.annotations.IFlagContainer;

import java.util.UUID;

public final class ClaimedChunk implements IFlagContainer
{
    public static final byte CHUNKLOADED = 0;
    public static final byte FORCED = 1;

    public final ChunkDimPos pos;
    public final UUID ownerID;
    public boolean isForced = false;
    public byte flags = 0;

    public ClaimedChunk(UUID o, ChunkDimPos p)
    {
        pos = p;
        ownerID = o;
    }

    public ForgePlayerMP getOwner()
    {
        return ForgeWorldMP.inst.getPlayer(ownerID);
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

    @Override
    public void setFlag(byte flag, boolean b)
    {
        flags = Bits.setBit(flags, flag, b);
    }

    @Override
    public boolean getFlag(byte flag)
    {
        return Bits.getBit(flags, flag);
    }
}