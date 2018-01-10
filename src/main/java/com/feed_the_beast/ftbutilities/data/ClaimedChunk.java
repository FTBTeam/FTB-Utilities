package com.feed_the_beast.ftbutilities.data;

import com.feed_the_beast.ftblib.lib.data.ForgeTeam;
import com.feed_the_beast.ftblib.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbutilities.util.FTBUTeamData;

import java.util.Arrays;
import java.util.HashSet;

/**
 * @author LatvianModder
 */
public final class ClaimedChunk
{
	private final ChunkDimPos pos;
	private final FTBUTeamData teamData;
	private ChunkUpgrade[] upgrades;
	private boolean invalid;
	public Boolean forced;

	public ClaimedChunk(ChunkDimPos c, FTBUTeamData t)
	{
		pos = c;
		teamData = t;
		upgrades = null;
		invalid = false;
		forced = null;
	}

	@SuppressWarnings("ConstantConditions")
	public boolean isInvalid()
	{
		return invalid || teamData == null || teamData.team == null || teamData.team.getOwner() == null;
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

	public boolean hasUpgrade(ChunkUpgrade upgrade)
	{
		if (upgrade == ChunkUpgrades.NO_EXPLOSIONS)
		{
			return !teamData.explosions.getBoolean();
		}

		if (upgrades != null && !upgrade.isInternal())
		{
			for (ChunkUpgrade upgrade1 : upgrades)
			{
				if (upgrade1 == upgrade)
				{
					return true;
				}
			}
		}

		return false;
	}

	public boolean setHasUpgrade(ChunkUpgrade upgrade, boolean v)
	{
		if (upgrade.isInternal())
		{
			return false;
		}

		HashSet<ChunkUpgrade> upgradeSet = new HashSet<>();

		if (upgrades != null)
		{
			upgradeSet.addAll(Arrays.asList(upgrades));
		}

		boolean changed;

		if (v)
		{
			changed = upgradeSet.add(upgrade);
		}
		else
		{
			changed = upgradeSet.remove(upgrade);
		}

		if (changed)
		{
			upgrades = upgradeSet.isEmpty() ? null : upgradeSet.toArray(new ChunkUpgrade[0]);
			ClaimedChunks.get().markDirty();
		}

		return changed;
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