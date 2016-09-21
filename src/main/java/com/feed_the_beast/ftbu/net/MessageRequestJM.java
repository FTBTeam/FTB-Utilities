package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.api.net.LMNetworkWrapper;
import com.feed_the_beast.ftbl.api.net.MessageToServer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.MathHelper;

public class MessageRequestJM extends MessageToServer<MessageRequestJM>
{
    private int chunkX, chunkY, sizeX, sizeY;

    public MessageRequestJM()
    {
    }

    public MessageRequestJM(int x, int y, int w, int h)
    {
        chunkX = x;
        chunkY = y;
        sizeX = MathHelper.clamp_int(w, 1, 255);
        sizeY = MathHelper.clamp_int(h, 1, 255);
    }

    @Override
    public LMNetworkWrapper getWrapper()
    {
        return FTBUNetHandler.NET;
    }

    @Override
    public void fromBytes(ByteBuf io)
    {
        chunkX = io.readInt();
        chunkY = io.readInt();
        sizeX = io.readUnsignedByte();
        sizeY = io.readUnsignedByte();
    }

    @Override
    public void toBytes(ByteBuf io)
    {
        io.writeInt(chunkX);
        io.writeInt(chunkY);
        io.writeByte(sizeX);
        io.writeByte(sizeY);
    }

    @Override
    public void onMessage(MessageRequestJM m, EntityPlayerMP player)
    {
        new MessageUpdateJM(m.chunkX, m.chunkY, player.dimension, m.sizeX, m.sizeY).sendTo(player);
    }
}