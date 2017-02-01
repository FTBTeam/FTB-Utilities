package com.feed_the_beast.ftbu.api.guide;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.info.InfoPage;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ServerInfoEvent extends Event
{
    private final InfoPage file;
    private final IForgePlayer player;
    private final boolean isOP;

    public ServerInfoEvent(InfoPage f, IForgePlayer p, boolean o)
    {
        file = f;
        player = p;
        isOP = o;
    }

    public InfoPage getFile()
    {
        return file;
    }

    public IForgePlayer getPlayer()
    {
        return player;
    }

    public boolean isOP()
    {
        return isOP;
    }
}