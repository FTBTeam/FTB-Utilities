package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.api.ForgePlayerMP;
import com.feed_the_beast.ftbl.api.ForgeWorldMP;
import com.feed_the_beast.ftbl.api.net.LMNetworkWrapper;
import com.feed_the_beast.ftbl.api.net.MessageToServer;
import com.feed_the_beast.ftbl.util.ChunkDimPos;
import com.feed_the_beast.ftbl.util.LMAccessToken;
import com.feed_the_beast.ftbu.world.ClaimedChunk;
import com.feed_the_beast.ftbu.world.FTBUWorldDataMP;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

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

    public MessageClaimChunk()
    {
    }

    @Override
    public LMNetworkWrapper getWrapper()
    {
        return FTBUNetHandler.NET;
    }

    @Override
    public void fromBytes(ByteBuf io)
    {
        type = io.readByte();
        token = io.readLong();
        pos = new ChunkDimPos(io.readInt(), io.readInt(), io.readInt());
    }

    @Override
    public void toBytes(ByteBuf io)
    {
        io.writeByte(type);
        io.writeLong(token);
        io.writeInt(pos.dim);
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
                FTBUWorldDataMP.claimChunk(p, m.pos);
                new MessageAreaUpdate(m.pos.chunkXPos, m.pos.chunkZPos, m.pos.dim, 1, 1).sendTo(ep);
                return;
            case ID_UNCLAIM:
                if(m.token != 0L && LMAccessToken.equals(p.getPlayer(), m.token, false))
                {
                    ClaimedChunk c = FTBUWorldDataMP.chunks.getChunk(m.pos);
                    if(c != null)
                    {
                        FTBUWorldDataMP.unclaimChunk(p, m.pos);
                    }
                }
                else
                {
                    FTBUWorldDataMP.unclaimChunk(p, m.pos);
                }
                new MessageAreaUpdate(m.pos.chunkXPos, m.pos.chunkZPos, m.pos.dim, 1, 1).sendTo(ep);
                return;
            case ID_UNCLAIM_ALL:
                FTBUWorldDataMP.unclaimAllChunks(p, m.pos.dim);
                return;
            case ID_UNCLAIM_ALL_DIMS:
                FTBUWorldDataMP.unclaimAllChunks(p, null);
                return;
            case ID_LOAD:
                FTBUWorldDataMP.setLoaded(p, m.pos, true);
                return;
            case ID_UNLOAD:
                FTBUWorldDataMP.setLoaded(p, m.pos, false);
                return;
        }
    }
}