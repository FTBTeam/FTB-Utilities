package latmod.ftbu.notification;

import java.io.File;
import java.net.URI;

import cpw.mods.fml.relauncher.*;
import ftb.lib.client.FTBLibClient;
import latmod.ftbu.net.ClientAction;
import latmod.ftbu.world.LMPlayerClient;
import latmod.lib.*;
import latmod.lib.util.FinalIDObject;
import net.minecraft.client.gui.GuiChat;

public abstract class ClickAction extends FinalIDObject
{
	public final PrimitiveType type;
	
	public ClickAction(String s, PrimitiveType t)
	{ super(s); type = t; }
	
	@SideOnly(Side.CLIENT)
	public abstract void onClicked(MouseAction c, LMPlayerClient p);
	
	// Static //
	
	public static final ClickAction CMD = new ClickAction("cmd", PrimitiveType.STRING)
	{
		@SideOnly(Side.CLIENT)
		public void onClicked(MouseAction c, LMPlayerClient p)
		{ FTBLibClient.execClientCommand(c.stringVal()); }
	};
	
	public static final ClickAction SHOW_CMD = new ClickAction("show_cmd", PrimitiveType.STRING)
	{
		@SideOnly(Side.CLIENT)
		public void onClicked(MouseAction c, LMPlayerClient p)
		{ FTBLibClient.mc.displayGuiScreen(new GuiChat(c.stringVal())); }
	};
	
	public static final ClickAction URL = new ClickAction("url", PrimitiveType.STRING)
	{
		@SideOnly(Side.CLIENT)
		public void onClicked(MouseAction c, LMPlayerClient p)
		{
			try { LMUtils.openURI(new URI(c.stringVal())); }
			catch(Exception ex) { ex.printStackTrace(); }
		}
	};
	
	public static final ClickAction FILE = new ClickAction("file", PrimitiveType.STRING)
	{
		@SideOnly(Side.CLIENT)
		public void onClicked(MouseAction c, LMPlayerClient p)
		{
			try { LMUtils.openURI(new File(c.stringVal()).toURI()); }
			catch(Exception ex) { ex.printStackTrace(); }
		}
	};
	
	public static final ClickAction GUI = new ClickAction("gui", PrimitiveType.STRING)
	{
		@SideOnly(Side.CLIENT)
		public void onClicked(MouseAction c, LMPlayerClient p)
		{
		}
	};
	
	public static final ClickAction FRIEND_ADD = new ClickAction("friend_add", PrimitiveType.INT)
	{
		@SideOnly(Side.CLIENT)
		public void onClicked(MouseAction c, LMPlayerClient p)
		{ ClientAction.ACTION_ADD_FRIEND.send(c.intVal()); }
	};
	
	public static final ClickAction FRIEND_ADD_ALL = new ClickAction("friend_add_all", PrimitiveType.NULL)
	{
		@SideOnly(Side.CLIENT)
		public void onClicked(MouseAction c, LMPlayerClient p)
		{ ClientAction.ACTION_ADD_FRIEND.send(0); }
	};
}