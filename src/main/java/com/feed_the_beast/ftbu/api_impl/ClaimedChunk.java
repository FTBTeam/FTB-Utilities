package com.feed_the_beast.ftbu.api_impl;

import com.feed_the_beast.ftbl.api.EnumTeamStatus;
import com.feed_the_beast.ftbl.api.IForgeTeam;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.api.chunks.ChunkUpgrade;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunk;
import com.feed_the_beast.ftbu.util.FTBUTeamData;
import com.feed_the_beast.ftbu.util.FTBUUniverseData;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

/**
 * @author LatvianModder
 */
public final class ClaimedChunk implements IClaimedChunk
{
	private final ChunkDimPos pos;
	public final FTBUTeamData team;
	private final IntOpenHashSet upgrades;
	private boolean invalid;

	public ClaimedChunk(ChunkDimPos c, FTBUTeamData t)
	{
		pos = c;
		team = t;
		upgrades = new IntOpenHashSet();
	}

	@Override
	public boolean isInvalid()
	{
		return invalid;
	}

	public void setInvalid()
	{
		invalid = true;
	}

	@Override
	public ChunkDimPos getPos()
	{
		return pos;
	}

	@Override
	public IForgeTeam getTeam()
	{
		return team.team;
	}

	@Override
	public boolean hasUpgrade(ChunkUpgrade upgrade)
	{
		if (upgrade == ChunkUpgrades.NO_EXPLOSIONS)
		{
			return !team.explosions.getBoolean() && team.team.anyPlayerHasPermission(FTBUPermissions.CLAIMS_ALLOW_DISABLE_EXPLOSIONS, EnumTeamStatus.MEMBER);
		}

		return !upgrade.isInternal() && upgrades.contains(FTBUUniverseData.getUpgradeId(upgrade));
	}

	@Override
	public boolean setHasUpgrade(ChunkUpgrade upgrade, boolean v)
	{
		if (upgrade.isInternal())
		{
			return false;
		}

		boolean changed;

		if (v)
		{
			changed = upgrades.add(FTBUUniverseData.getUpgradeId(upgrade));
		}
		else
		{
			changed = upgrades.remove(FTBUUniverseData.getUpgradeId(upgrade));
		}

		if (changed)
		{
			ClaimedChunks.INSTANCE.markDirty();
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