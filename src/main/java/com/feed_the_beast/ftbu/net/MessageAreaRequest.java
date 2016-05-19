package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.api.ForgePlayerMP;
import com.feed_the_beast.ftbl.api.ForgeWorldMP;
import com.feed_the_beast.ftbl.api.net.LMNetworkWrapper;
import com.feed_the_beast.ftbl.api.net.MessageToServer;
import io.netty.buffer.ByteBuf;
import latmod.lib.MathHelperLM;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.DimensionType;

public class MessageAreaRequest extends MessageToServer<MessageAreaRequest>
{
    public int chunkX, chunkY, sizeX, sizeY;

    public MessageAreaRequest() { }

    public MessageAreaRequest(int x, int y, int w, int h)
    {
        chunkX = x;
        chunkY = y;
        sizeX = MathHelperLM.clampInt(w, 1, 255);
        sizeY = MathHelperLM.clampInt(h, 1, 255);
    }

    @Override
    public LMNetworkWrapper getWrapper()
    { return FTBUNetHandler.NET; }

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
    public void onMessage(MessageAreaRequest m, EntityPlayerMP ep)
    {
        ForgePlayerMP p = ForgeWorldMP.inst.getPlayer(ep);
        new MessageAreaUpdate(p, m.chunkX, m.chunkY, DimensionType.getById(p.getPlayer().dimension), m.sizeX, m.sizeY).sendTo(ep);
    }
}