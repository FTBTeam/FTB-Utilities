package latmod.ftbu.api;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraftforge.common.MinecraftForge;

public class EventLM extends Event
{
	public final void post()
	{ MinecraftForge.EVENT_BUS.post(this); }
}