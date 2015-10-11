package latmod.ftbu.mod.client.gui.friends;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.util.client.ClientNotifications;
import latmod.ftbu.util.gui.SliderLM;
import latmod.lib.FastList;

@SideOnly(Side.CLIENT)
public class PanelNotifications extends PanelFriendsGui
{
	public final SliderLM scrollBar;
	public final FastList<ButtonNotification> notificationButtons;
	
	public PanelNotifications(GuiFriends g)
	{
		super(g);
		width = 120;
		
		scrollBar = new SliderLM(g, 0, 0, 16, 0, 8)
		{
			public boolean isEnabled()
			{ return parentPanel.mouseOver() || mouseOver(); }
		};
		scrollBar.displayMax = 0;
		scrollBar.isVertical = true;
		
		notificationButtons = new FastList<ButtonNotification>();
	}
	
	public boolean isEnabled()
	{ return gui.panelPopupMenu == null; }
	
	public void addWidgets()
	{
		//add(scrollBar);
		
		notificationButtons.clear();
		width = 0;
		for(ClientNotifications.Perm p : ClientNotifications.Perm.list)
		{
			ButtonNotification b = new ButtonNotification(this, p);
			notificationButtons.add(b);
			width = Math.max(width, b.width);
		}
		
		for(ButtonNotification b : notificationButtons)
		{ b.width = width; add(b); }
	}
	
	public void renderWidget()
	{
		for(int i = 0; i < notificationButtons.size(); i++)
			notificationButtons.get(i).renderWidget();
	}
}