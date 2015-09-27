package latmod.ftbu.util;

import java.io.File;
import java.net.URI;

import cpw.mods.fml.relauncher.*;
import latmod.core.util.*;
import latmod.ftbu.api.NotificationClickHandler;
import latmod.ftbu.util.client.LatCoreMCClient;
import latmod.ftbu.world.LMPlayerClient;
import net.minecraft.client.gui.GuiChat;

public class NotificationClick
{
	public static final String CMD = "cmd";
	public static final String SHOW_CMD = "show_cmd";
	public static final String URL = "url";
	public static final String FILE = "file";
	
	public final String ID;
	private final Object val;
	
	NotificationClick(String id, Object v)
	{ ID = id; val = v; }
	
	public NotificationClick(String id, String v)
	{ ID = id; val = v; }
	
	public NotificationClick(String id, Number v)
	{ ID = id; val = v; }
	
	public NotificationClick(String id, boolean v)
	{ ID = id; val = v; }
	
	Object val()
	{ return val; }
	
	public String stringVal()
	{ return val().toString(); }
	
	public Number numVal()
	{ return (Number)val(); }
	
	public boolean boolVal()
	{ return ((Boolean)val()).booleanValue(); }
	
	@SideOnly(Side.CLIENT)
	public boolean onClicked(Notification n, LMPlayerClient p)
	{
		NotificationClickHandler h = Registry.getHandler(ID);
		if(h != null) h.onClicked(this, n, p);
		return h != null;
	}
	
	@SideOnly(Side.CLIENT)
	public static class Registry
	{
		private static final FastMap<String, NotificationClickHandler> handlers = new FastMap<String, NotificationClickHandler>();
		
		public static void add(String s, NotificationClickHandler h)
		{ handlers.put(s, h); }
		
		public static NotificationClickHandler getHandler(String s)
		{
			if(s == null || s.isEmpty()) return null;
			else if(s.equals(CMD)) return HandlerCmd.inst;
			else if(s.equals(SHOW_CMD)) return HandlerShowCmd.inst;
			else if(s.equals(URL)) return HandlerUrl.inst;
			else if(s.equals(FILE)) return HandlerFile.inst;
			return handlers.get(s);
		}
		
		private static class HandlerCmd implements NotificationClickHandler
		{
			private static final HandlerCmd inst = new HandlerCmd();
			
			public void onClicked(NotificationClick c, Notification n, LMPlayerClient p)
			{ LatCoreMCClient.execClientCommand(c.stringVal()); }
		}
		
		private static class HandlerShowCmd implements NotificationClickHandler
		{
			private static final HandlerShowCmd inst = new HandlerShowCmd();
			
			public void onClicked(NotificationClick c, Notification n, LMPlayerClient p)
			{
				LatCoreMCClient.mc.displayGuiScreen(new GuiChat(c.stringVal()));
			}
		}
		
		private static class HandlerUrl implements NotificationClickHandler
		{
			private static final HandlerUrl inst = new HandlerUrl();
			
			public void onClicked(NotificationClick c, Notification n, LMPlayerClient p)
			{
				try { LMUtils.openURI(new URI(c.stringVal())); }
				catch (Exception ex) { ex.printStackTrace(); }
			}
		}
		
		private static class HandlerFile implements NotificationClickHandler
		{
			private static final HandlerFile inst = new HandlerFile();
			
			public void onClicked(NotificationClick c, Notification n, LMPlayerClient p)
			{
				try { LMUtils.openURI(new File(c.stringVal()).toURI()); }
				catch (Exception ex) { ex.printStackTrace(); }
			}
		}
	}
}