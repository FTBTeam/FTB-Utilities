package com.feed_the_beast.ftbutilities.events.chunks;

import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbutilities.data.ClaimedChunk;
import com.feed_the_beast.ftbutilities.events.FTBUtilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public abstract class ChunkModifiedEvent extends FTBUtilitiesEvent
{
	private final ClaimedChunk chunk;
	private final ForgePlayer player;

	public ChunkModifiedEvent(ClaimedChunk c, @Nullable ForgePlayer p)
	{
		chunk = c;
		player = p;
	}

	public ClaimedChunk getChunk()
	{
		return chunk;
	}

	@Nullable
	public ForgePlayer getPlayer()
	{
		return player;
	}

	@Cancelable
	public static class Claim extends FTBUtilitiesEvent
	{
		private final ChunkDimPos chunkDimPos;
		private final ForgePlayer player;

		public Claim(ChunkDimPos c, ForgePlayer p)
		{
			chunkDimPos = c;
			player = p;
		}

		public ChunkDimPos getChunkDimPos()
		{
			return chunkDimPos;
		}

		public ForgePlayer getPlayer()
		{
			return player;
		}
	}

	public static class Claimed extends ChunkModifiedEvent
	{
		public Claimed(ClaimedChunk c, @Nullable ForgePlayer p)
		{
			super(c, p);
		}
	}

	public static class Unclaimed extends ChunkModifiedEvent
	{
		public Unclaimed(ClaimedChunk c, @Nullable ForgePlayer p)
		{
			super(c, p);
		}
	}

	public static class Loaded extends ChunkModifiedEvent
	{
		public Loaded(ClaimedChunk c, @Nullable ForgePlayer p)
		{
			super(c, p);
		}
	}

	public static class Unloaded extends ChunkModifiedEvent
	{
		public Unloaded(ClaimedChunk c, @Nullable ForgePlayer p)
		{
			super(c, p);
		}
	}
}