package latmod.ftbu.util;

import latmod.lib.FastMap;

public class LMGuiHandlerRegistry
{
	private static final FastMap<String, LMGuiHandler> guiHandlers = new FastMap<String, LMGuiHandler>();
	
	public static void add(LMGuiHandler h)
	{ if(h != null && !guiHandlers.keys.contains(h.ID)) guiHandlers.put(h.ID, h); }
	
	public static LMGuiHandler getLMGuiHandler(String id)
	{ return guiHandlers.get(id); }
}