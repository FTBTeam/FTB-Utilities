package latmod.ftbu.api;

import ftb.lib.Phase;

public class EventFTBUInit extends EventLM
{
	public final Phase phase;
	
	public EventFTBUInit(Phase p)
	{ phase = p; }
}