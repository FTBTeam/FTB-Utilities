package latmod.ftbu.core.event;

import latmod.ftbu.core.LatCoreMC;
import cpw.mods.fml.common.eventhandler.Event;

public class EventLM extends Event
{
	public final void post()
	{ LatCoreMC.EVENT_BUS.post(this); }
	
	public static enum Phase
	{
		PRE,
		POST;
		
		public boolean isPre()
		{ return this == PRE; }
		
		public boolean isPost()
		{ return this == POST; }
	}
}