package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.api.net.LMNetworkWrapper;
import com.feed_the_beast.ftbl.api.net.MessageToServer;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageRequestChunkData extends MessageToServer<MessageRequestChunkData>
{
    private int chunkX, chunkY, sizeX, sizeY;

    public MessageRequestChunkData()
    {
    }

    public MessageRequestChunkData(int x, int y, int w, int h)
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
    public IMessage onMessage(final MessageRequestChunkData m, MessageContext ctx)
    {
        return new MessageUpdateChunkData(ctx.getServerHandler().playerEntity, m.chunkX, m.chunkY, m.sizeX, m.sizeY);
    }
}