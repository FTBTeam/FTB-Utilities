package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.lib.net.LMNetworkWrapper;
import com.feed_the_beast.ftbl.lib.net.MessageToClient;
import com.feed_the_beast.ftbl.lib.util.LMNetUtils;
import com.feed_the_beast.ftbu.FTBLibIntegration;
import com.feed_the_beast.ftbu.gui.GuiWarps;
import com.feed_the_beast.ftbu.world.FTBUPlayerData;
import com.feed_the_beast.ftbu.world.FTBUUniverseData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageSendWarpList extends MessageToClient<MessageSendWarpList>
{
    private List<String> warps, homes;

    public MessageSendWarpList()
    {
    }

    MessageSendWarpList(EntityPlayer player)
    {
        FTBUUniverseData universeData = FTBUUniverseData.get();
        FTBUPlayerData playerData = FTBUPlayerData.get(FTBLibIntegration.API.getUniverse().getPlayer(player));

        if(universeData != null && playerData != null)
        {
            warps = new ArrayList<>(universeData.listWarps());
            homes = new ArrayList<>(playerData.listHomes());
            Collections.sort(warps);
            Collections.sort(homes);
        }
        else
        {
            warps = new ArrayList<>();
            homes = new ArrayList<>();
        }
    }

    @Override
    public LMNetworkWrapper getWrapper()
    {
        return FTBUNetHandler.NET;
    }

    @Override
    public void fromBytes(ByteBuf io)
    {
        int s = io.readUnsignedByte();

        homes = new ArrayList<>(s);

        while(--s >= 0)
        {
            homes.add(LMNetUtils.readString(io));
        }

        s = io.readUnsignedByte();

        warps = new ArrayList<>(s);

        while(--s >= 0)
        {
            warps.add(LMNetUtils.readString(io));
        }
    }

    @Override
    public void toBytes(ByteBuf io)
    {
        int s = Math.min(255, homes.size());

        io.writeByte(s);

        for(int i = 0; i < s; i++)
        {
            LMNetUtils.writeString(io, homes.get(i));
        }

        s = Math.min(255, warps.size());

        io.writeByte(s);

        for(int i = 0; i < s; i++)
        {
            LMNetUtils.writeString(io, warps.get(i));
        }
    }

    @Override
    public void onMessage(MessageSendWarpList m, EntityPlayer player)
    {
        GuiWarps gui = GuiWarps.INSTANCE;

        if(gui != null)
        {
            gui.homes = m.homes;
            gui.warps = m.warps;
            gui.hasLoaded = true;
        }
    }
}