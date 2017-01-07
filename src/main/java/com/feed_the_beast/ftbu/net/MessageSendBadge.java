package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.lib.net.LMNetworkWrapper;
import com.feed_the_beast.ftbl.lib.net.MessageToClient;
import com.feed_the_beast.ftbl.lib.util.LMNetUtils;
import com.feed_the_beast.ftbu.client.CachedClientData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

public class MessageSendBadge extends MessageToClient<MessageSendBadge>
{
    private UUID playerID;
    private String badgeURL;

    public MessageSendBadge()
    {
    }

    public MessageSendBadge(UUID player, String url)
    {
        playerID = player;
        badgeURL = url;
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
        badgeURL = LMNetUtils.readString(io);
    }

    @Override
    public void toBytes(ByteBuf io)
    {
        LMNetUtils.writeUUID(io, playerID);
        LMNetUtils.writeString(io, badgeURL);
    }

    @Override
    public void onMessage(MessageSendBadge m, EntityPlayer player)
    {
        CachedClientData.setBadge(m.playerID, m.badgeURL);
    }
}