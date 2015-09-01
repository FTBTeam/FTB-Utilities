package latmod.ftbu.core.api;

import latmod.ftbu.core.EnumBusType;
import cpw.mods.fml.common.eventhandler.Event;

public class EventLM extends Event
{
	public final void post()
	{ EnumBusType.FORGE.eventBus.post(this); }
}