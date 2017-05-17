package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.lib.net.MessageToClient;
import com.feed_the_beast.ftbl.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbu.gui.GuiClaimedChunks;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author LatvianModder
 */
public class MessageOpenClaimedChunksGui extends MessageToClient<MessageOpenClaimedChunksGui>
{
    @Override
    public NetworkWrapper getWrapper()
    {
        return FTBUNetHandler.NET;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
    }

    @Override
    public void onMessage(MessageOpenClaimedChunksGui m, EntityPlayer player)
    {
        GuiClaimedChunks.instance = new GuiClaimedChunks();
        GuiClaimedChunks.instance.openGui();
    }
}