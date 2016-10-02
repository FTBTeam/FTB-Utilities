package com.feed_the_beast.ftbu.api.guide;

import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.Map;

public class ClientGuideEvent extends Event
{
    private Map<String, IGuide> map;

    public ClientGuideEvent(Map<String, IGuide> m)
    {
        map = m;
    }

    public void add(IGuide page)
    {
        map.put(page.getPage().getName(), page);
    }
}