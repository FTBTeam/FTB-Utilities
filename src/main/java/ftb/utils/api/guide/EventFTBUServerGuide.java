package ftb.utils.api.guide;

import ftb.lib.api.EventLM;
import ftb.utils.world.LMPlayerServer;

public class EventFTBUServerGuide extends EventLM
{
	public final ServerInfoFile file;
	public final LMPlayerServer player;
	public final boolean isOP;
	
	public EventFTBUServerGuide(ServerInfoFile f, LMPlayerServer p)
	{
		file = f;
		player = p;
		isOP = p.isOP();
	}
}