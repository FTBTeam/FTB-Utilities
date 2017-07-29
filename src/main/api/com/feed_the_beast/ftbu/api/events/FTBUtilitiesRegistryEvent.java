package com.feed_the_beast.ftbu.api.events;

import com.feed_the_beast.ftbu.api.IFTBUtilitiesRegistry;

/**
 * @author LatvianModder
 */
public class FTBUtilitiesRegistryEvent extends FTBUtilitiesEvent
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