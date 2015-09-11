package latmod.ftbu.core.api;

import cpw.mods.fml.common.eventhandler.Event;
import latmod.ftbu.core.EnumBusType;

public class EventLM extends Event
{
	public final void post()
	{ EnumBusType.FORGE.eventBus.post(this); }
}