package com.feed_the_beast.ftbu.api.events.registry;

import com.feed_the_beast.ftbu.api.chunks.IChunkUpgrade;
import com.feed_the_beast.ftbu.api.events.FTBUtilitiesEvent;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class RegisterChunkUpgradesEvent extends FTBUtilitiesEvent
{
	private final Consumer<IChunkUpgrade> callback;

	public RegisterChunkUpgradesEvent(Consumer<IChunkUpgrade> c)
	{
		callback = c;
	}

	public void register(IChunkUpgrade entry)
	{
		callback.accept(entry);
	}
}