package latmod.ftbu.core.api;

import latmod.ftbu.core.OtherMods;
import latmod.ftbu.core.tile.IWailaTile;
import latmod.ftbu.core.util.FastList;
import latmod.ftbu.core.waila.*;
import mcp.mobius.waila.api.IWailaRegistrar;
import cpw.mods.fml.common.Optional;

public class RegisterWailaEvent extends EventLM
{
	private static final FastList<WailaRegEntry> registry = new FastList<WailaRegEntry>();
	
	public void register(Class<?> block, BasicWailaHandler h)
	{ for(WailaType t : h.types) registry.add(new WailaRegEntry(block, h, t)); }
	
	@Optional.Method(modid = OtherMods.WAILA)
	public static void registerHandlers(IWailaRegistrar i)
	{
		RegisterWailaEvent e = new RegisterWailaEvent();
		e.register(IWailaTile.Stack.class, new WailaLMTile(e, WailaType.STACK));
		e.register(IWailaTile.Head.class, new WailaLMTile(e, WailaType.HEAD));
		e.register(IWailaTile.Body.class, new WailaLMTile(e, WailaType.BODY));
		e.register(IWailaTile.Tail.class, new WailaLMTile(e, WailaType.TAIL));
		e.post();
		
		for(WailaRegEntry wre : registry)
		{
			if(wre.type == WailaType.STACK) i.registerStackProvider(new WailaDataProvider(wre.handler), wre.block);
			if(wre.type == WailaType.HEAD) i.registerHeadProvider(new WailaDataProvider(wre.handler), wre.block);
			if(wre.type == WailaType.BODY) i.registerBodyProvider(new WailaDataProvider(wre.handler), wre.block);
			if(wre.type == WailaType.TAIL) i.registerTailProvider(new WailaDataProvider(wre.handler), wre.block);
		}
	}
	
	private class WailaRegEntry
	{
		public final Class<?> block;
		public final BasicWailaHandler handler;
		public final WailaType type;
		
		public WailaRegEntry(Class<?> c, BasicWailaHandler h, WailaType t)
		{ block = c; handler = h; type = t; }
	}
}