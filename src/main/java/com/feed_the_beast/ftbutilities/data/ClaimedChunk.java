package com.feed_the_beast.ftbutilities.data;

import com.feed_the_beast.ftblib.lib.data.ForgeTeam;
import com.feed_the_beast.ftblib.lib.math.ChunkDimPos;

/**
 * @author LatvianModder
 */
public final class ClaimedChunk
{
	private final ChunkDimPos pos;
	private final FTBUtilitiesTeamData teamData;
	private boolean loaded;
	private boolean invalid;
	public Boolean forced;

	public ClaimedChunk(ChunkDimPos c, FTBUtilitiesTeamData t)
	{
		pos = c;
		teamData = t;
		loaded = false;
		invalid = false;
		forced = null;
	}

	public boolean isInvalid()
	{
		return invalid || !getTeam().isValid();
	}

	public void setInvalid()
	{
		if (!invalid)
		{
			invalid = true;
			getTeam().markDirty();
		}
	}

	public ChunkDimPos getPos()
	{
		return pos;
	}

	public ForgeTeam getTeam()
	{
		return teamData.team;
	}

	public FTBUtilitiesTeamData getData()
	{
		return teamData;
	}

	public boolean setLoaded(boolean v)
	{
		if (loaded != v)
		{
			loaded = v;

			if (ClaimedChunks.isActive())
			{
				ClaimedChunks.instance.markDirty();
			}

			getTeam().markDirty();
			return true;
		}

		return false;
	}

	public boolean isLoaded()
	{
		return loaded;
	}

	public boolean hasExplosions()
	{
		return teamData.hasExplosions();
	}

	public String toString()
	{
		return pos.toString() + '+' + loaded;
	}

	public int hashCode()
	{
		return pos.hashCode();
	}

	public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}
		else if (o != null && o.getClass() == ClaimedChunk.class)
		{
			return pos.equalsChunkDimPos(((ClaimedChunk) o).pos);
		}

		return false;
	}
}