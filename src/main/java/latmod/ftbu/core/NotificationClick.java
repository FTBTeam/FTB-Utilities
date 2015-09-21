package latmod.ftbu.core;

import java.io.File;
import java.net.URI;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.core.client.LatCoreMCClient;
import latmod.ftbu.core.util.*;
import latmod.ftbu.core.world.LMPlayerClient;
import net.minecraft.client.gui.GuiChat;
import net.minecraftforge.client.ClientCommandHandler;

public class NotificationClick
{
	public static final String CMD = "cmd";
	public static final String SHOW_CMD = "show_cmd";
	public static final String URL = "url";
	public static final String FILE = "file";
	
	public final String ID;
	public final byte[] val;
	
	public NotificationClick(String id, byte[] v)
	{ ID = id; val = v; }
	
	public NotificationClick(String id, String v)
	{ this(id, (v == null) ? null : v.getBytes()); }
	
	@SideOnly(Side.CLIENT)
	public boolean onClicked(Notification n, LMPlayerClient p)
	{
		Handler h = Registry.getHandler(ID);
		if(h != null) h.onClicked(ID, val, n, p);
		return h != null;
	}
	
	@SideOnly(Side.CLIENT)
	public static class Registry
	{
		private static final FastMap<String, Handler> handlers = new FastMap<String, Handler>();
		
		public static void add(String s, Handler h)
		{ handlers.put(s, h); }
		
		public static Handler getHandler(String s)
		{
			if(s == null || s.isEmpty()) return null;
			else if(s.equals(CMD)) return HandlerCmd.inst;
			else if(s.equals(SHOW_CMD)) return HandlerShowCmd.inst;
			else if(s.equals(URL)) return HandlerUrl.inst;
			else if(s.equals(FILE)) return HandlerFile.inst;
			return handlers.get(s);
		}
		
		private static class HandlerCmd implements Handler
		{
			private static final HandlerCmd inst = new HandlerCmd();
			
			public void onClicked(String id, byte[] val, Notification n, LMPlayerClient p)
			{
				String v = new String(val);
				LatCoreMCClient.mc.ingameGUI.getChatGUI().addToSentMessages(v);
		        if(ClientCommandHandler.instance.executeCommand(LatCoreMCClient.mc.thePlayer, v) != 0) return;
		        LatCoreMCClient. mc.thePlayer.sendChatMessage(v);
			}
		}
		
		private static class HandlerShowCmd implements Handler
		{
			private static final HandlerShowCmd inst = new HandlerShowCmd();
			
			public void onClicked(String id, byte[] val, Notification n, LMPlayerClient p)
			{
				LatCoreMCClient.mc.displayGuiScreen(new GuiChat(new String(val)));
			}
		}
		
		private static class HandlerUrl implements Handler
		{
			private static final HandlerUrl inst = new HandlerUrl();
			
			public void onClicked(String id, byte[] val, Notification n, LMPlayerClient p)
			{
				try { LMUtils.openURI(new URI(new String(val))); }
				catch (Exception ex) { ex.printStackTrace(); }
			}
		}
		
		private static class HandlerFile implements Handler
		{
			private static final HandlerFile inst = new HandlerFile();
			
			public void onClicked(String id, byte[] val, Notification n, LMPlayerClient p)
			{
				try { LMUtils.openURI(new File(new String(val)).toURI()); }
				catch (Exception ex) { ex.printStackTrace(); }
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	public abstract interface Handler
	{
		@SideOnly(Side.CLIENT)
		public void onClicked(String id, byte[] val, Notification n, LMPlayerClient p);
	}
}