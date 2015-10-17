package latmod.ftbu.notification;

import java.io.File;
import java.net.URI;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.api.client.EventClickAction;
import latmod.ftbu.net.ClientAction;
import latmod.ftbu.util.client.LatCoreMCClient;
import latmod.ftbu.world.LMPlayerClient;
import latmod.lib.LMUtils;
import net.minecraft.client.gui.GuiChat;

@SideOnly(Side.CLIENT)
public class ClickActionHandler // Notification
{
	public static void onClicked(ClickAction c, LMPlayerClient p)
	{
		if(c.ID.equals(ClickAction.CMD))
		{
			LatCoreMCClient.execClientCommand(c.stringVal());
		}
		else if(c.ID.equals(ClickAction.SHOW_CMD))
		{
			LatCoreMCClient.mc.displayGuiScreen(new GuiChat(c.stringVal()));
		}
		else if(c.ID.equals(ClickAction.URL))
		{
			try { LMUtils.openURI(new URI(c.stringVal())); }
			catch(Exception ex) { ex.printStackTrace(); }
		}
		else if(c.ID.equals(ClickAction.FILE))
		{
			try { LMUtils.openURI(new File(c.stringVal()).toURI()); }
			catch(Exception ex) { ex.printStackTrace(); }
		}
		else if(c.ID.equals(ClickAction.GUI))
		{
		}
		else if(c.ID.equals(ClickAction.FRIEND_ADD))
		{
			ClientAction.ACTION_ADD_FRIEND.send(c.intVal());
		}
		else if(c.ID.equals(ClickAction.FRIEND_ADD_ALL))
		{
			ClientAction.ACTION_ADD_FRIEND.send(0);
		}
		else new EventClickAction(c, p).post();
	}
}
