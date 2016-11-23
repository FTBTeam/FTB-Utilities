package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.lib.net.LMNetworkWrapper;
import com.feed_the_beast.ftbl.lib.net.MessageToServer;
import com.feed_the_beast.ftbl.lib.util.LMNetUtils;
import com.feed_the_beast.ftbu.FTBLibIntegration;
import com.feed_the_beast.ftbu.world.FTBUUniverseData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.UUID;

public class MessageRequestBadge extends MessageToServer<MessageRequestBadge>
{
    private UUID playerID;

    public MessageRequestBadge()
    {
    }

    public MessageRequestBadge(UUID player)
    {
        playerID = player;
    }

    @Override
    public LMNetworkWrapper getWrapper()
    {
        return FTBUNetHandler.NET;
    }

    @Override
    public void fromBytes(ByteBuf io)
    {
        playerID = LMNetUtils.readUUID(io);
    }

    @Override
    public void toBytes(ByteBuf io)
    {
        LMNetUtils.writeUUID(io, playerID);
    }

    @Override
    public void onMessage(MessageRequestBadge m, EntityPlayerMP player)
    {
        new MessageSendBadge(m.playerID, FTBUUniverseData.getServerBadge(FTBLibIntegration.API.getUniverse().getPlayer(m.playerID))).sendTo(player);
    }
}