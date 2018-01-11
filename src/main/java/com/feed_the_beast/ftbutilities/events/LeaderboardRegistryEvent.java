package com.feed_the_beast.ftbutilities.events;

import com.feed_the_beast.ftbutilities.data.Leaderboard;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class LeaderboardRegistryEvent extends FTBUtilitiesEvent
{
	private final Consumer<Leaderboard> callback;

	public LeaderboardRegistryEvent(Consumer<Leaderboard> c)
	{
		callback = c;
	}

	public void register(Leaderboard entry)
	{
		callback.accept(entry);
	}
}