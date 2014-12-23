package latmod.core.event;

import latmod.core.waila.*;
import mcp.mobius.waila.api.IWailaRegistrar;

public class RegisterWailaEvent extends EventLM
{
	public final IWailaRegistrar registry;
	
	public RegisterWailaEvent(IWailaRegistrar i)
	{ registry = i; }
	
	public void register(Class<?> block, BasicWailaHandler h)
	{
		if(h.types.contains(WailaType.STACK)) registry.registerStackProvider(h, block);
		if(h.types.contains(WailaType.HEAD)) registry.registerHeadProvider(h, block);
		if(h.types.contains(WailaType.BODY)) registry.registerBodyProvider(h, block);
		if(h.types.contains(WailaType.TAIL)) registry.registerTailProvider(h, block);
	}
	
	public static void registerHandlers(IWailaRegistrar i)
	{ new RegisterWailaEvent(i).post(); }
}