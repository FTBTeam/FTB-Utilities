package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.lib.net.LMNetworkWrapper;
import com.feed_the_beast.ftbl.lib.net.MessageToServer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public class MessageClaimedChunksRequest extends MessageToServer<MessageClaimedChunksRequest>
{
    private int startX, startZ;

    public MessageClaimedChunksRequest()
    {
    }

    public MessageClaimedChunksRequest(int sx, int sz)
    {
        startX = sx;
        startZ = sz;
    }

    @Override
    public LMNetworkWrapper getWrapper()
    {
        return FTBUNetHandler.NET;
    }

    @Override
    public void fromBytes(ByteBuf io)
    {
        startX = io.readInt();
        startZ = io.readInt();
    }

    @Override
    public void toBytes(ByteBuf io)
    {
        io.writeInt(startX);
        io.writeInt(startZ);
    }

    @Override
    public void onMessage(MessageClaimedChunksRequest m, EntityPlayerMP player)
    {
        new MessageClaimedChunksUpdate(m.startX, m.startZ, player).sendTo(player);
    }
}