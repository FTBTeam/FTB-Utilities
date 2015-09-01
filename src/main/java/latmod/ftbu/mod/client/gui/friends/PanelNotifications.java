package latmod.ftbu.mod.client.gui.friends;

import latmod.ftbu.core.gui.*;
import latmod.ftbu.core.util.FastList;

public class PanelNotifications extends PanelLM
{
	public final FastList<ButtonNotification> notificationButtons;
	
	public PanelNotifications(GuiLM g, int x, int y, int w, int h)
	{
		super(g, x, y, w, h);
		notificationButtons = new FastList<ButtonNotification>();
	}
	
	public void addWidgets()
	{
	}
	
	public void addNotificationButton(ButtonNotification a)
	{
		add(a);
		notificationButtons.add(a);
	}
}