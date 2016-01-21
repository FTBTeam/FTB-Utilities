package ftb.utils.api;

import ftb.lib.api.EventLM;
import ftb.utils.world.LMWorldClient;

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