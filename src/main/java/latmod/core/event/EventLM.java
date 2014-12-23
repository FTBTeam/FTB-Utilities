package latmod.core.event;

import latmod.core.LatCoreMC;
import cpw.mods.fml.common.eventhandler.Event;

public class EventLM extends Event
{
	public final void post()
	{ LatCoreMC.EVENT_BUS.post(this); }
}