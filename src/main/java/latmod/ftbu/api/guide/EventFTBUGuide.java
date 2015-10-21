package latmod.ftbu.api.guide;

import ftb.lib.api.EventLM;

public class EventFTBUGuide extends EventLM
{
	public final GuideFile file;
	
	public EventFTBUGuide(GuideFile f)
	{ file = f; }
}