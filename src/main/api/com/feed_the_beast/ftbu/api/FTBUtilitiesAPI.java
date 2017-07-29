package com.feed_the_beast.ftbu.api;

import com.feed_the_beast.ftbl.api.config.IConfigValue;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunkStorage;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author LatvianModder
 */
public abstract class FTBUtilitiesAPI
{
	public static FTBUtilitiesAPI API;

	public abstract IClaimedChunkStorage getClaimedChunks();

	public abstract IRank getRank(GameProfile profile);

	public abstract IConfigValue getRankConfig(GameProfile profile, String id);

	public IConfigValue getRankConfig(EntityPlayer player, String id)
	{
		return getRankConfig(player.getGameProfile(), id);
	}
}