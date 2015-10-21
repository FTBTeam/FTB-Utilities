package latmod.ftbu.api;

import ftb.lib.Phase;
import ftb.lib.api.EventLM;

public class EventFTBUInit extends EventLM
{
	public final Phase phase;
	
	public EventFTBUInit(Phase p)
	{ phase = p; }
}