package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.api.net.LMNetworkWrapper;
import com.feed_the_beast.ftbl.api.net.MessageToClient;
import com.feed_the_beast.ftbu.world.FTBUWorldDataSP;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

public class MessageSendBadge extends MessageToClient<MessageSendBadge>
{
    public UUID playerID;
    public String badgeID;

    public MessageSendBadge()
    {
    }

    public MessageSendBadge(UUID player, String id)
    {
        playerID = player;
        badgeID = id;
    }

    @Override
    public LMNetworkWrapper getWrapper()
    {
        return FTBUNetHandler.NET;
    }

    @Override
    public void fromBytes(ByteBuf io)
    {
        playerID = readUUID(io);
        badgeID = readString(io);
    }

    @Override
    public void toBytes(ByteBuf io)
    {
        writeUUID(io, playerID);
        writeString(io, badgeID);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onMessage(MessageSendBadge m, Minecraft ctx)
    {
        FTBUWorldDataSP.localBadges.badgePlayerMap.put(m.playerID, FTBUWorldDataSP.localBadges.badgeMap.get(m.badgeID));
    }
}