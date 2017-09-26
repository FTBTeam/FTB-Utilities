package com.feed_the_beast.ftbu.api.guide;

import com.feed_the_beast.ftbl.api.FTBLibEvent;

import java.util.function.BiConsumer;

/**
 * @author LatvianModder
 */
public class RegisterGuideLineProvidersEvent extends FTBLibEvent
{
	private final BiConsumer<String, IGuideTextLineProvider> callback;

	public RegisterGuideLineProvidersEvent(BiConsumer<String, IGuideTextLineProvider> c)
	{
		callback = c;
	}

	public void register(String mod, IGuideTextLineProvider data)
	{
		callback.accept(mod, data);
	}
}