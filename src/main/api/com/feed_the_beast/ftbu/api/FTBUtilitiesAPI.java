package com.feed_the_beast.ftbu.api;

import com.feed_the_beast.ftbl.lib.config.ConfigValue;
import com.feed_the_beast.ftbl.lib.guide.GuidePage;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunks;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author LatvianModder
 */
public abstract class FTBUtilitiesAPI
{
	public static FTBUtilitiesAPI API;

	public abstract IClaimedChunks getClaimedChunks();

	public abstract IRank getRank(GameProfile profile);

	public abstract ConfigValue getRankConfig(GameProfile profile, String id);

	public ConfigValue getRankConfig(EntityPlayer player, String id)
	{
		return getRankConfig(player.getGameProfile(), id);
	}

	public abstract void displayGuide(EntityPlayer player, GuidePage page);
}