package com.feed_the_beast.ftbutilities.data;

import java.util.Objects;

/**
 * @author LatvianModder
 */
public final class TicketKey
{
	public final int dimension;
	public final String teamId;

	public TicketKey(int dim, String team)
	{
		dimension = dim;
		teamId = team;
	}

	public String toString()
	{
		return teamId + '@' + dimension;
	}

	public int hashCode()
	{
		return Objects.hash(dimension, teamId);
	}

	public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}
		else if (o != null && o.getClass() == TicketKey.class)
		{
			TicketKey key = (TicketKey) o;
			return dimension == key.dimension && teamId.equals(key.teamId);
		}
		return false;
	}
}