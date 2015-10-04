package latmod.ftbu.notification;

import java.io.File;
import java.net.URI;

import cpw.mods.fml.relauncher.*;
import latmod.core.util.LMUtils;
import latmod.ftbu.api.EventClickAction;
import latmod.ftbu.util.client.LatCoreMCClient;
import latmod.ftbu.world.LMPlayerClient;
import net.minecraft.client.gui.GuiChat;

@SideOnly(Side.CLIENT)
public class ClickActionHandler
{
	public static void onClicked(ClickAction c, LMPlayerClient p)
	{
		if(c.equals(ClickAction.CMD))
		{
			LatCoreMCClient.execClientCommand(c.stringVal());
		}
		else if(c.equals(ClickAction.SHOW_CMD))
		{
			LatCoreMCClient.mc.displayGuiScreen(new GuiChat(c.stringVal()));
		}
		else if(c.equals(ClickAction.URL))
		{
			try { LMUtils.openURI(new URI(c.stringVal())); }
			catch(Exception ex) { ex.printStackTrace(); }
		}
		else if(c.equals(ClickAction.FILE))
		{
			try { LMUtils.openURI(new File(c.stringVal()).toURI()); }
			catch(Exception ex) { ex.printStackTrace(); }
		}
		else if(c.equals(ClickAction.GUI))
		{
		}
		else if(c.equals(ClickAction.FRIEND_ADD))
		{
		}
		else if(c.equals(ClickAction.FRIEND_ADD_ALL))
		{
		}
		else new EventClickAction(c, p).post();
	}
}
