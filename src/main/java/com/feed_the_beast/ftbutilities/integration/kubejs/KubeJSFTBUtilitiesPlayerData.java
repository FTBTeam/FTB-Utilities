package com.feed_the_beast.ftbutilities.integration.kubejs;

import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftbutilities.FTBUtilitiesConfig;
import com.feed_the_beast.ftbutilities.data.ClaimedChunk;
import com.feed_the_beast.ftbutilities.data.ClaimedChunks;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesPlayerData;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesTeamData;
import com.feed_the_beast.ftbutilities.ranks.Rank;
import com.feed_the_beast.ftbutilities.ranks.Ranks;
import dev.latvian.kubejs.player.PlayerDataJS;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.OptionalInt;
import java.util.Set;

/**
 * @author LatvianModder
 */
public class KubeJSFTBUtilitiesPlayerData
{
	private final PlayerDataJS playerData;
	private FTBUtilitiesPlayerData cached;

	public KubeJSFTBUtilitiesPlayerData(PlayerDataJS d)
	{
		playerData = d;
	}

	public FTBUtilitiesPlayerData getWrappedPlayerData()
	{
		if (cached == null)
		{
			cached = FTBUtilitiesPlayerData.get(Universe.get().getPlayer(playerData.getPlayerEntity()));
		}

		return cached;
	}

	@Nullable
	public Rank getRank()
	{
		return Ranks.isActive() ? Ranks.INSTANCE.getPlayerRank(playerData.getProfile()) : null;
	}

	public Set<ClaimedChunk> getClaimedChunks()
	{
		if (ClaimedChunks.isActive())
		{
			return ClaimedChunks.instance.getTeamChunks(getWrappedPlayerData().player.team, OptionalInt.empty());
		}

		return Collections.emptySet();
	}

	public boolean getAfk()
	{
		return getWrappedPlayerData().afkTime >= FTBUtilitiesConfig.afk.getNotificationTimer();
	}

	public boolean getEnablePVP()
	{
		return getWrappedPlayerData().enablePVP();
	}

	public String getNickname()
	{
		return getWrappedPlayerData().getNickname();
	}

	public void setNickname(String nickname)
	{
		getWrappedPlayerData().setNickname(nickname);
	}

	public int getMaxClaimChunks()
	{
		return FTBUtilitiesTeamData.get(getWrappedPlayerData().player.team).getMaxClaimChunks();
	}

	public int getMaxChunkloaderChunks()
	{
		return FTBUtilitiesTeamData.get(getWrappedPlayerData().player.team).getMaxChunkloaderChunks();
	}
}