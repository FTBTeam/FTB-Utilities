package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.api.net.LMNetworkWrapper;
import com.feed_the_beast.ftbl.api.net.MessageToClient;
import com.feed_the_beast.ftbu.client.CachedClientData;
import com.latmod.lib.util.LMNetUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

public class MessageSendBadge extends MessageToClient<MessageSendBadge>
{
    private UUID playerID;
    private String badgeID;

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
        playerID = LMNetUtils.readUUID(io);
        badgeID = LMNetUtils.readString(io);
    }

    @Override
    public void toBytes(ByteBuf io)
    {
        LMNetUtils.writeUUID(io, playerID);
        LMNetUtils.writeString(io, badgeID);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onMessage(MessageSendBadge m, Minecraft ctx)
    {
        CachedClientData.LOCAL_BADGES.badgePlayerMap.put(m.playerID, CachedClientData.LOCAL_BADGES.badgeMap.get(m.badgeID));
    }
}