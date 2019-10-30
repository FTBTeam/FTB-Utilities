package com.feed_the_beast.ftbutilities.ranks;

import com.feed_the_beast.ftblib.lib.util.misc.Node;

import java.util.UUID;

/**
 * @author LatvianModder
 */
public class RankCacheKey
{
	public final UUID playerId;
	public final Node node;

	public RankCacheKey(UUID id, Node n)
	{
		playerId = id;
		node = n;
	}

	@Override
	public int hashCode()
	{
		return playerId.hashCode() * 31 + node.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}
		else if (o instanceof RankCacheKey)
		{
			RankCacheKey k = (RankCacheKey) o;
			return playerId.equals(k.playerId) && node.equals(k.node);
		}

		return false;
	}

	@Override
	public String toString()
	{
		return playerId + ":" + node;
	}
}