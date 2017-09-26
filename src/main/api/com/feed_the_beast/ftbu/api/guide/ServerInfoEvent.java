package com.feed_the_beast.ftbu.api.guide;

import com.feed_the_beast.ftbl.api.FTBLibEvent;
import com.feed_the_beast.ftbl.api.IForgePlayer;

/**
 * @author LatvianModder
 */
public class ServerInfoEvent extends FTBLibEvent
{
	private final IGuidePage page;
	private final IForgePlayer player;
	private final boolean isOP;

	public ServerInfoEvent(IGuidePage p, IForgePlayer pl, boolean o)
	{
		page = p;
		player = pl;
		isOP = o;
	}

	public IGuidePage getPage()
	{
		return page;
	}

	public IForgePlayer getPlayer()
	{
		return player;
	}

	public boolean isOP()
	{
		return isOP;
	}
}