package com.feed_the_beast.ftbu.api_impl;

import com.feed_the_beast.ftbl.lib.config.ConfigValue;
import com.feed_the_beast.ftbl.lib.guide.GuidePage;
import com.feed_the_beast.ftbu.FTBU;
import com.feed_the_beast.ftbu.api.FTBUtilitiesAPI;
import com.feed_the_beast.ftbu.api.IRank;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunks;
import com.feed_the_beast.ftbu.net.MessageDisplayGuide;
import com.feed_the_beast.ftbu.ranks.Ranks;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author LatvianModder
 */
public class FTBUtilitiesAPI_Impl extends FTBUtilitiesAPI
{
	@Override
	public IClaimedChunks getClaimedChunks()
	{
		return ClaimedChunks.INSTANCE;
	}

	@Override
	public IRank getRank(GameProfile profile)
	{
		return Ranks.getRank(profile);
	}

	@Override
	public ConfigValue getRankConfig(GameProfile profile, String id)
	{
		return getRank(profile).getConfig(id);
	}

	@Override
	public void displayGuide(EntityPlayer player, GuidePage page)
	{
		if (player.world.isRemote)
		{
			FTBU.PROXY.displayGuide(page);
		}
		else
		{
			new MessageDisplayGuide(page).sendTo(player);
		}
	}
}