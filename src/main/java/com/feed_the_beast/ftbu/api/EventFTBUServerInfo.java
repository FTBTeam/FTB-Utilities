package com.feed_the_beast.ftbu.api;

import com.feed_the_beast.ftbl.api.ForgePlayerMP;
import com.feed_the_beast.ftbu.api.guide.ServerInfoFile;
import net.minecraftforge.fml.common.eventhandler.Event;

public class EventFTBUServerInfo extends Event
{
	public final ServerInfoFile file;
	public final ForgePlayerMP player;
	public final boolean isOP;
	
	public EventFTBUServerInfo(ServerInfoFile f, ForgePlayerMP p, boolean o)
	{
		file = f;
		player = p;
		isOP = o;
	}
}