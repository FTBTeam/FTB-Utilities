package com.feed_the_beast.ftbu.api;

import com.feed_the_beast.ftbl.api.ForgePlayerMP;
import com.feed_the_beast.ftbu.gui.guide.ServerInfoFile;
import net.minecraftforge.fml.common.eventhandler.Event;

public class EventFTBUServerInfo extends Event
{
    private final ServerInfoFile file;
    private final ForgePlayerMP player;
    private final boolean isOP;

    public EventFTBUServerInfo(ServerInfoFile f, ForgePlayerMP p, boolean o)
    {
        file = f;
        player = p;
        isOP = o;
    }

    public ServerInfoFile getFile()
    {
        return file;
    }

    public ForgePlayerMP getPlayer()
    {
        return player;
    }

    public boolean isOP()
    {
        return isOP;
    }
}