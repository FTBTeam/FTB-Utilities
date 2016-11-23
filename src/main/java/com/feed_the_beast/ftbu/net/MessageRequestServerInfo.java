package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.lib.gui.misc.GuiLoading;
import com.feed_the_beast.ftbl.lib.net.LMNetworkWrapper;
import com.feed_the_beast.ftbl.lib.net.MessageToServer;
import com.feed_the_beast.ftbu.FTBLibIntegration;
import com.feed_the_beast.ftbu.ServerInfoFile;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayerMP;

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

    public GuiScreen openGui()
    {
        sendToServer();
        return new GuiLoading().getWrapper();
    }

    @Override
    public void onMessage(MessageRequestServerInfo m, EntityPlayerMP player)
    {
        FTBLibIntegration.API.displayInfoGui(player, new ServerInfoFile(player));
    }
}