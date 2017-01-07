package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.lib.net.LMNetworkWrapper;
import com.feed_the_beast.ftbl.lib.net.MessageToServer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class MessageRequestWarpList extends MessageToServer<MessageRequestWarpList>
{
    public MessageRequestWarpList()
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
    }

    @Override
    public void toBytes(ByteBuf io)
    {
    }

    @Override
    public void onMessage(MessageRequestWarpList m, EntityPlayer player)
    {
        new MessageSendWarpList(player).sendTo(player);
    }
}