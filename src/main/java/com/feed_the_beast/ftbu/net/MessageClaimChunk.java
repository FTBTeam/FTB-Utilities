package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.api.ForgePlayerMP;
import com.feed_the_beast.ftbl.api.ForgeWorldMP;
import com.feed_the_beast.ftbl.api.net.LMNetworkWrapper;
import com.feed_the_beast.ftbl.api.net.MessageToServer;
import com.feed_the_beast.ftbl.util.ChunkDimPos;
import com.feed_the_beast.ftbl.util.LMAccessToken;
import com.feed_the_beast.ftbu.world.ClaimedChunk;
import com.feed_the_beast.ftbu.world.FTBUPlayerDataMP;
import com.feed_the_beast.ftbu.world.FTBUWorldDataMP;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.DimensionType;

public class MessageClaimChunk extends MessageToServer<MessageClaimChunk>
{
    public static final byte ID_CLAIM = 0;
    public static final byte ID_UNCLAIM = 1;
    public static final byte ID_UNCLAIM_ALL = 2;
    public static final byte ID_UNCLAIM_ALL_DIMS = 3;
    public static final byte ID_LOAD = 4;
    public static final byte ID_UNLOAD = 5;

    public byte type;
    public ChunkDimPos pos;
    public long token;

    public MessageClaimChunk() { }

    @Override
    public LMNetworkWrapper getWrapper()
    { return FTBUNetHandler.NET; }

    @Override
    public void fromBytes(ByteBuf io)
    {
        type = io.readByte();
        token = io.readLong();
        pos = new ChunkDimPos(DimensionType.getById(io.readInt()), io.readInt(), io.readInt());
    }

    @Override
    public void toBytes(ByteBuf io)
    {
        io.writeByte(type);
        io.writeLong(token);
        io.writeInt(pos.dim.getId());
        io.writeInt(pos.chunkXPos);
        io.writeInt(pos.chunkZPos);
    }

    @Override
    public void onMessage(MessageClaimChunk m, EntityPlayerMP ep)
    {
        ForgePlayerMP p = ForgeWorldMP.inst.getPlayer(ep);

        switch(m.type)
        {
            case ID_CLAIM:
            {
                FTBUPlayerDataMP.claimChunk(p, m.pos);
                new MessageAreaUpdate(p, m.pos.chunkXPos, m.pos.chunkZPos, m.pos.dim, 1, 1).sendTo(ep);
            }
            case ID_UNCLAIM:
            {
                if(m.token != 0L && LMAccessToken.equals(p.getPlayer(), m.token, false))
                {
                    ClaimedChunk c = FTBUWorldDataMP.get().getChunk(m.pos);
                    if(c != null)
                    {
                        ForgePlayerMP p1 = ForgeWorldMP.inst.getPlayer(c.ownerID);
                        FTBUPlayerDataMP d1 = FTBUPlayerDataMP.get(p1);
                        FTBUPlayerDataMP.unclaimChunk(p, m.pos);
                    }
                }
                else { FTBUPlayerDataMP.unclaimChunk(p, m.pos); }
                new MessageAreaUpdate(p, m.pos.chunkXPos, m.pos.chunkZPos, m.pos.dim, 1, 1).sendTo(ep);
            }
            case ID_UNCLAIM_ALL:
            {
                FTBUPlayerDataMP.unclaimAllChunks(p, m.pos.dim);
            }
            case ID_UNCLAIM_ALL_DIMS:
            {
                FTBUPlayerDataMP.unclaimAllChunks(p, null);
            }
            case ID_LOAD:
            {
                FTBUPlayerDataMP.setLoaded(p, m.pos, true);
            }
            case ID_UNLOAD:
            {
                FTBUPlayerDataMP.setLoaded(p, m.pos, false);
            }
        }
    }
}