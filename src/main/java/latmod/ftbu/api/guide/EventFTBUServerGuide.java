package latmod.ftbu.api.guide;

import ftb.lib.api.EventLM;
import latmod.ftbu.world.LMPlayerServer;

public class EventFTBUServerGuide extends EventLM
{
	public final ServerGuideFile file;
	public final LMPlayerServer player;
	
	public EventFTBUServerGuide(ServerGuideFile f, LMPlayerServer s)
	{ file = f; player = s; }
}