package com.feed_the_beast.ftbu.api.events;

import com.feed_the_beast.ftbu.api.IFTBUtilitiesRegistry;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * @author LatvianModder
 */
public class FTBUtilitiesRegistryEvent extends Event
{
    private final IFTBUtilitiesRegistry reg;

    public FTBUtilitiesRegistryEvent(IFTBUtilitiesRegistry r)
    {
        reg = r;
    }

    public IFTBUtilitiesRegistry getRegistry()
    {
        return reg;
    }
}