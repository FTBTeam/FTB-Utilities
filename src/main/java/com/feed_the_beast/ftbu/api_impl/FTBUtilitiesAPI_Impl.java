package com.feed_the_beast.ftbu.api_impl;

import com.feed_the_beast.ftbl.api.config.IConfigValue;
import com.feed_the_beast.ftbu.api.FTBUtilitiesAPI;
import com.feed_the_beast.ftbu.api.IRank;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunkStorage;
import com.feed_the_beast.ftbu.ranks.Ranks;
import com.mojang.authlib.GameProfile;

/**
 * @author LatvianModder
 */
public class FTBUtilitiesAPI_Impl extends FTBUtilitiesAPI
{
	@Override
	public IClaimedChunkStorage getClaimedChunks()
	{
		return ClaimedChunkStorage.INSTANCE;
	}

	@Override
	public IRank getRank(GameProfile profile)
	{
		return Ranks.getRank(profile);
	}

	@Override
	public IConfigValue getRankConfig(GameProfile profile, String id)
	{
		return getRank(profile).getConfig(id);
	}
}