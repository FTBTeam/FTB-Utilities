package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.lib.net.LMNetworkWrapper;
import com.feed_the_beast.ftbl.lib.net.MessageToClient;
import com.feed_the_beast.ftbl.lib.util.LMNetUtils;
import com.feed_the_beast.ftbu.client.CachedClientData;
import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MessageSendFTBUClientFlags extends MessageToClient<MessageSendFTBUClientFlags>
{
    private Map<UUID, Integer> map;

    public MessageSendFTBUClientFlags()
    {
    }

    public MessageSendFTBUClientFlags(Map<UUID, Integer> m)
    {
        map = new HashMap<>(m);
    }

    @Override
    public LMNetworkWrapper getWrapper()
    {
        return FTBUNetHandler.NET;
    }

    @Override
    public void fromBytes(ByteBuf io)
    {
        int s = io.readUnsignedShort();
        map = new HashMap<>(s);

        while(--s >= 0)
        {
            UUID id = LMNetUtils.readUUID(io);
            int flags = io.readInt();
            map.put(id, flags);
        }
    }

    @Override
    public void toBytes(ByteBuf io)
    {
        io.writeShort(map.size());
        map.forEach((key, value) ->
        {
            LMNetUtils.writeUUID(io, key);
            io.writeInt(value);
        });
    }

    @Override
    public void onMessage(MessageSendFTBUClientFlags m)
    {
        m.map.forEach(CachedClientData::setFlags);
    }
}