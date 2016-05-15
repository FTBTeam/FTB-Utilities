package com.feed_the_beast.ftbu.api;

import com.feed_the_beast.ftbl.api.ForgePlayerMP;
import com.feed_the_beast.ftbu.api.guide.ServerGuideFile;
import net.minecraftforge.fml.common.eventhandler.Event;

public class EventFTBUServerGuide extends Event
{
	public final ServerGuideFile file;
	public final ForgePlayerMP player;
	public final boolean isOP;
	
	public EventFTBUServerGuide(ServerGuideFile f, ForgePlayerMP p, boolean o)
	{
		file = f;
		player = p;
		isOP = o;
	}
}