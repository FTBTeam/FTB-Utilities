package com.feed_the_beast.ftbu.api.chunks;

import com.feed_the_beast.ftbu.api.FTBUtilitiesEvent;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class RegisterChunkUpgradesEvent extends FTBUtilitiesEvent
{
	private final Consumer<ChunkUpgrade> callback;

	public RegisterChunkUpgradesEvent(Consumer<ChunkUpgrade> c)
	{
		callback = c;
	}

	public void register(ChunkUpgrade entry)
	{
		callback.accept(entry);
	}
}