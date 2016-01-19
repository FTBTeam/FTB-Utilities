package ftb.utils.api.guide;

import ftb.lib.api.EventLM;
import ftb.utils.world.LMPlayerServer;

public class EventFTBUServerGuide extends EventLM
{
	public final ServerGuideFile file;
	public final LMPlayerServer player;
	public final boolean isOP;
	
	public EventFTBUServerGuide(ServerGuideFile f, LMPlayerServer p, boolean o)
	{
		file = f;
		player = p;
		isOP = o;
	}
}