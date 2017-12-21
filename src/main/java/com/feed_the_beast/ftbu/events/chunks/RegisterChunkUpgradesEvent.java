package com.feed_the_beast.ftbu.events.chunks;

import com.feed_the_beast.ftbu.data.ChunkUpgrade;
import com.feed_the_beast.ftbu.events.FTBUtilitiesEvent;

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