package latmod.ftbu.api;

import ftb.lib.api.EventLM;
import latmod.ftbu.world.LMWorldClient;

public class EventLMWorldClient extends EventLM
{
	public final LMWorldClient world;
	public final boolean closed;
	
	public EventLMWorldClient(LMWorldClient w, boolean c)
	{
		world = w;
		closed = c;
	}
}