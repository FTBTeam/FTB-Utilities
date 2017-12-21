package com.feed_the_beast.ftbu.events.chunks;

import com.feed_the_beast.ftbu.data.ClaimedChunk;
import com.feed_the_beast.ftbu.events.FTBUtilitiesEvent;

/**
 * @author LatvianModder
 */
public abstract class ChunkModifiedEvent extends FTBUtilitiesEvent
{
	private final ClaimedChunk chunk;

	public ChunkModifiedEvent(ClaimedChunk c)
	{
		chunk = c;
	}

	public ClaimedChunk getChunk()
	{
		return chunk;
	}

	public static class Claimed extends ChunkModifiedEvent
	{
		public Claimed(ClaimedChunk c)
		{
			super(c);
		}
	}

	public static class Unclaimed extends ChunkModifiedEvent
	{
		public Unclaimed(ClaimedChunk c)
		{
			super(c);
		}
	}

	public static class Loaded extends ChunkModifiedEvent
	{
		public Loaded(ClaimedChunk c)
		{
			super(c);
		}
	}

	public static class Unloaded extends ChunkModifiedEvent
	{
		public Unloaded(ClaimedChunk c)
		{
			super(c);
		}
	}
}