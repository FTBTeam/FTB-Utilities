package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.api.ForgeWorldMP;
import com.feed_the_beast.ftbl.api.net.LMNetworkWrapper;
import com.feed_the_beast.ftbl.api.net.MessageToServer;
import com.feed_the_beast.ftbu.badges.Badge;
import com.feed_the_beast.ftbu.world.FTBUWorldDataMP;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.UUID;

public class MessageRequestBadge extends MessageToServer<MessageRequestBadge>
{
    public UUID playerID;

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
        playerID = readUUID(io);
    }

    @Override
    public void toBytes(ByteBuf io)
    {
        writeUUID(io, playerID);
    }

    @Override
    public void onMessage(MessageRequestBadge m, EntityPlayerMP ep)
    {
        Badge b = FTBUWorldDataMP.getServerBadge(ForgeWorldMP.inst.getPlayer(m.playerID));

        if(b != null)
        {
            new MessageSendBadge(m.playerID, b.getID()).sendTo(ep);
        }
    }
}