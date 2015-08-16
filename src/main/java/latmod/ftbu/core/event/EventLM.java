package latmod.ftbu.core.event;

import latmod.ftbu.core.EnumBusType;
import cpw.mods.fml.common.eventhandler.Event;

public class EventLM extends Event
{
	public final void post()
	{ EnumBusType.FTBU_EVENT_BUS.post(this); }
}