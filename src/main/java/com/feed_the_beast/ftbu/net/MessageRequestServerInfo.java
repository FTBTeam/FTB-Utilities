package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.api.ForgePlayerMP;
import com.feed_the_beast.ftbl.api.ForgeWorldMP;
import com.feed_the_beast.ftbl.api.net.LMNetworkWrapper;
import com.feed_the_beast.ftbl.api.net.MessageToServer;
import com.feed_the_beast.ftbl.gui.GuiLoading;
import com.feed_the_beast.ftbu.gui.guide.ServerInfoFile;
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
    public void onMessage(MessageRequestServerInfo m, EntityPlayerMP ep)
    {
        ForgePlayerMP owner = ForgeWorldMP.inst.getPlayer(ep);
        new ServerInfoFile(owner).displayGuide(owner.getPlayer());
    }
}