package com.feed_the_beast.ftbu.api_impl;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.io.Bits;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbu.FTBUConfig;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.api.chunks.IChunkUpgrade;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunk;
import com.feed_the_beast.ftbu.util.FTBUTeamData;
import net.minecraftforge.server.permission.PermissionAPI;

/**
 * @author LatvianModder
 */
public class ClaimedChunk implements IClaimedChunk
{
	private final ChunkDimPos pos;
	private final IForgePlayer owner;
	private int flags;
	public boolean dirty = true;

	public ClaimedChunk(ChunkDimPos c, IForgePlayer p, int f)
	{
		pos = c;
		owner = p;
		flags = f;
	}

	@Override
	public ChunkDimPos getPos()
	{
		return pos;
	}

	@Override
	public IForgePlayer getOwner()
	{
		return owner;
	}

	@Override
	public void markDirty()
	{
		dirty = true;
	}

	@Override
	public boolean hasUpgrade(IChunkUpgrade upgrade)
	{
		if (upgrade == ChunkUpgrade.SHOULD_FORCE)
		{
			if (!FTBUConfig.world.chunk_loading || !hasUpgrade(ChunkUpgrade.LOADED) || !FTBUPermissions.canUpgradeChunk(owner.getProfile(), ChunkUpgrade.LOADED))
			{
				return false;
			}
			else if (!owner.isOnline())
			{
				/*
				double max = FTBUtilitiesAPI_Impl.INSTANCE.getRankConfig(owner.getProfile(), FTBUPermissions.CHUNKLOADER_OFFLINE_TIMER).getDouble();

                if(max == 0 || (max > 0 && FTBLibStats.getLastSeenDeltaInHours(owner.stats(), false) > max))
                */
				if (!PermissionAPI.hasPermission(owner.getProfile(), FTBUPermissions.CHUNKLOADER_LOAD_OFFLINE, null))
				{
					return false;
				}
			}

			return true;
		}
		else if (upgrade == ChunkUpgrade.NO_EXPLOSIONS)
		{
			if (!FTBUPermissions.canUpgradeChunk(owner.getProfile(), ChunkUpgrade.NO_EXPLOSIONS))
			{
				return false;
			}

			return !FTBUTeamData.get(owner.getTeam()).explosions.getBoolean();
		}

		return Bits.getFlag(flags, 1 << upgrade.getId());
	}

	@Override
	public void setHasUpgrade(IChunkUpgrade upgrade, boolean v)
	{
		int flags0 = flags;
		flags = Bits.setFlag(flags, 1 << upgrade.getId(), v);

		if (flags0 != flags)
		{
			markDirty();
		}
	}
}