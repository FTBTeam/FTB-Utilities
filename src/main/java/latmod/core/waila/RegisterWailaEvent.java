package latmod.core.waila;

import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.Event;

public class RegisterWailaEvent extends Event
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
	
	public void post()
	{ MinecraftForge.EVENT_BUS.post(this); }
	
	public static void registerHandlers(IWailaRegistrar i)
	{ new RegisterWailaEvent(i).post(); }
}