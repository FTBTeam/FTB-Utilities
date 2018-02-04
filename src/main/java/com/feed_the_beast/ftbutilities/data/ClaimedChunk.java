package com.feed_the_beast.ftbutilities.data;

import com.feed_the_beast.ftblib.lib.data.ForgeTeam;
import com.feed_the_beast.ftblib.lib.math.ChunkDimPos;

/**
 * @author LatvianModder
 */
public final class ClaimedChunk
{
	private final ChunkDimPos pos;
	private final FTBUTeamData teamData;
	private boolean loaded;
	private boolean invalid;
	public Boolean forced;

	public ClaimedChunk(ChunkDimPos c, FTBUTeamData t)
	{
		pos = c;
		teamData = t;
		loaded = false;
		invalid = false;
		forced = null;
	}

	public boolean isInvalid()
	{
		return invalid || teamData.team.getOwner() == null;
	}

	public void setInvalid()
	{
		invalid = true;
	}

	public ChunkDimPos getPos()
	{
		return pos;
	}

	public ForgeTeam getTeam()
	{
		return teamData.team;
	}

	public FTBUTeamData getData()
	{
		return teamData;
	}

	public boolean setLoaded(boolean v)
	{
		if (loaded != v)
		{
			loaded = v;

			if (ClaimedChunks.instance != null)
			{
				ClaimedChunks.instance.markDirty();
			}
		}

		return false;
	}

	public boolean isLoaded()
	{
		return loaded;
	}

	public boolean hasExplosions()
	{
		return teamData.explosions.getBoolean();
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