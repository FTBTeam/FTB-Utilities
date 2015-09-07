package latmod.ftbu.mod.client.gui.friends;

import latmod.ftbu.core.util.FastList;
import latmod.ftbu.mod.player.ClientNotifications;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class PanelNotifications extends PanelFriendsGui
{
	public final FastList<ButtonNotification> notificationButtons;
	
	public PanelNotifications(GuiFriends g)
	{
		super(g);
		notificationButtons = new FastList<ButtonNotification>();
	}
	
	public void addWidgets()
	{
		notificationButtons.clear();
		
		for(ClientNotifications.Perm n : ClientNotifications.Perm.list)
		{
			ButtonNotification b = new ButtonNotification(this, n);
			add(b);
			notificationButtons.add(b);
		}
	}
	
	public void renderWidget()
	{
	}
}