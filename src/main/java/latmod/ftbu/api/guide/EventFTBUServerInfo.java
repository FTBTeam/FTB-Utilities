package latmod.ftbu.api.guide;

import ftb.lib.api.EventLM;
import latmod.ftbu.world.LMPlayerServer;
import latmod.lib.FastList;

public class EventFTBUServerInfo extends EventLM
{
	public final GuideFile file;
	public final FastList<Top> tops;
	public final LMPlayerServer player;
	
	public EventFTBUServerInfo(GuideFile f, FastList<Top> l, LMPlayerServer s)
	{ file = f; tops = l; player = s; }
}