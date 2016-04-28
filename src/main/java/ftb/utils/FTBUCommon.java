package ftb.utils;

import ftb.lib.api.events.ForgePlayerDataEvent;
import ftb.lib.api.events.ForgeWorldDataEvent;
import ftb.utils.world.FTBUPlayerDataMP;
import ftb.utils.world.FTBUWorldDataMP;

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
	
	public void addPlayerData(ForgePlayerDataEvent event)
	{
		if(event.player.getSide().isServer())
		{
			event.add(new FTBUPlayerDataMP(event.player.toPlayerMP()));
		}
	}
}