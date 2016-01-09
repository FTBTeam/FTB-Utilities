package latmod.ftbu.api;

import ftb.lib.api.EventLM;
import latmod.ftbu.world.LMWorldServer;
import latmod.lib.util.Phase;

public class EventLMWorldServer extends EventLM
{
	public final LMWorldServer world;
	
	public EventLMWorldServer(LMWorldServer w)
	{ world = w; }
	
	public static class Loaded extends EventLMWorldServer
	{
		public final Phase phase;
		
		public Loaded(LMWorldServer w, Phase p)
		{
			super(w);
			phase = p;
		}
	}
	
	public static class Saved extends EventLMWorldServer
	{
		public Saved(LMWorldServer w)
		{ super(w); }
	}
}