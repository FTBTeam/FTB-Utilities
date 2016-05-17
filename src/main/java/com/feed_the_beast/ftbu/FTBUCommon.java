package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbl.api.events.ForgeWorldDataEvent;
import com.feed_the_beast.ftbu.world.FTBUWorldDataMP;

public class FTBUCommon // FTBUClient
{
	public void preInit()
	{
	}
	
	public void postInit()
	{
	}
	
	public void addWorldData(ForgeWorldDataEvent event)
	{
		if(event.world.side.isServer())
		{
			event.add(FTBUWorldDataMP.get());
		}
	}
}