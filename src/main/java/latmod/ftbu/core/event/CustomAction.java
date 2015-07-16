package latmod.ftbu.core.event;

import latmod.ftbu.core.net.*;
import latmod.ftbu.core.util.FastMap;

public class CustomAction
{
	public static final FastMap<String, CustomActionFromClient> cHandlers = new FastMap<String, CustomActionFromClient>();
	public static final FastMap<String, CustomActionFromServer> sHandlers = new FastMap<String, CustomActionFromServer>();
	
	public static void register(String s, CustomActionFromClient i)
	{ if(!cHandlers.keys.contains(s)) cHandlers.put(s, i); }
	
	public static void register(String s, CustomActionFromServer i)
	{ if(!sHandlers.keys.contains(s)) sHandlers.put(s, i); }
}