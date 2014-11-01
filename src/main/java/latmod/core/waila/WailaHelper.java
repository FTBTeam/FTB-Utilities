package latmod.core.waila;
import latmod.core.LatCoreMC;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.Event;

public class WailaHelper
{
	public static void registerHandlers(IWailaRegistrar r)
	{ new RegisterHandlersEvent(r).post(); }
	
	public static boolean isInstalled()
	{ return LatCoreMC.isModInstalled("Waila"); }
	
	public static class RegisterHandlersEvent extends Event
	{
		public final IWailaRegistrar registry;
		
		public RegisterHandlersEvent(IWailaRegistrar r)
		{ registry = r; }
		
		public void post()
		{ MinecraftForge.EVENT_BUS.post(this); }
		
		public void addHandler(Class<?> block, BasicWailaHandler i)
		{
			if(i.registerStack) registry.registerStackProvider(i, block);
			if(i.registerHead) registry.registerHeadProvider(i, block);
			if(i.registerBody) registry.registerBodyProvider(i, block);
			if(i.registerTail) registry.registerTailProvider(i, block);
		}
		
		public void addConfig(String cat, String key)
		{ registry.addConfig(cat, key); }
		
		public void addConfig(String cat, String key, String val)
		{ registry.addConfig(cat, key, val); }
	}
}