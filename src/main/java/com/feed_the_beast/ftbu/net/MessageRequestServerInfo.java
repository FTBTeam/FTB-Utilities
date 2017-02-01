package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.lib.net.LMNetworkWrapper;
import com.feed_the_beast.ftbl.lib.net.MessageToServer;
import com.feed_the_beast.ftbu.FTBLibIntegration;
import com.feed_the_beast.ftbu.ServerInfoPage;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class MessageRequestServerInfo extends MessageToServer<MessageRequestServerInfo>
{
    public MessageRequestServerInfo()
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
    public void onMessage(MessageRequestServerInfo m, EntityPlayer player)
    {
        FTBLibIntegration.API.displayInfoGui(player, ServerInfoPage.getPageForPlayer(player));
    }
}