package latmod.ftbu.api.guide;

import ftb.lib.api.EventLM;

public class EventFTBUClientGuide extends EventLM
{
	public final ClientGuideFile file;
	
	public EventFTBUClientGuide(ClientGuideFile f)
	{ file = f; }
}