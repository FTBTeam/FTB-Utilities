package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.net.LMNetworkWrapper;
import com.feed_the_beast.ftbl.api.net.MessageToServer;
import com.feed_the_beast.ftbl.gui.GuiLoading;
import com.feed_the_beast.ftbl.net.MessageDisplayInfo;
import com.feed_the_beast.ftbu.FTBLibIntegration;
import com.feed_the_beast.ftbu.world.ServerInfoFile;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

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
    public IMessage onMessage(MessageRequestServerInfo m, MessageContext ctx)
    {
        IForgePlayer owner = FTBLibIntegration.API.getUniverse().getPlayer(ctx.getServerHandler().playerEntity);
        return new MessageDisplayInfo(new ServerInfoFile(owner));
    }
}