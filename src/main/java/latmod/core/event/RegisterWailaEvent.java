package latmod.core.event;

import latmod.core.tile.IWailaTile;
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
	{
		RegisterWailaEvent e = new RegisterWailaEvent(i);
		e.register(IWailaTile.Stack.class, new WailaLMTile(e, WailaType.STACK));
		e.register(IWailaTile.Head.class, new WailaLMTile(e, WailaType.HEAD));
		e.register(IWailaTile.Body.class, new WailaLMTile(e, WailaType.BODY));
		e.register(IWailaTile.Tail.class, new WailaLMTile(e, WailaType.TAIL));
		e.post();
	}
}