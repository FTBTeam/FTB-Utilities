package latmod.ftbu.mod.client.gui.friends;

import latmod.ftbu.core.client.FTBULang;
import latmod.ftbu.core.gui.*;
import latmod.ftbu.core.world.*;
import latmod.ftbu.mod.client.minimap.Waypoints;

public class PanelActionButtons extends PanelLM
{
	public final GuiFriends gui;
	public final LMPlayerClient playerLM;
	
	public PanelActionButtons(GuiFriends g, int x, int y, LMPlayerClient p)
	{
		super(g, x, y, 0, 18);
		gui = g;
		playerLM = p;
	}
	
	public void add(PlayerAction a, String s)
	{
		ButtonAction b = new ButtonAction(gui, height, a, s);
		add(b);
		width = Math.max(width, b.width);
		height += b.height + 1;
	}
	
	public void addWidgets()
	{
		width = 0;
		height = 0;
		
		if(playerLM.equalsPlayer(LMWorldClient.inst.clientPlayer))
		{
			add(PlayerAction.settings, FTBULang.client_config);
			add(PlayerAction.notifications, FTBULang.Friends.notifications);
			add(PlayerAction.waypoints, Waypoints.clientConfig.getIDS());
			add(PlayerAction.minimap, FTBULang.Friends.claimed_chunks);
			//actionButtons.add(new ActionButton(this, PlayerAction.notes, "Notes"));
		}
		else
		{
			boolean isFriend = LMWorldClient.inst.clientPlayer.isFriendRaw(playerLM);
			add(PlayerAction.friend_toggle, isFriend ? FTBULang.Friends.button_rem_friend : FTBULang.Friends.button_add_friend);
			
			if(!isFriend && playerLM.isFriendRaw(LMWorldClient.inst.clientPlayer))
				add(PlayerAction.friend_deny, FTBULang.Friends.button_deny_friend);
		}
		
		for(WidgetLM b : widgets)
			b.width = width;
	}

	public void render()
	{
		for(WidgetLM w : widgets)
			w.renderWidget();
	}
	
	public void mousePressed(int b)
	{
		super.mousePressed(b);
		if(b == 0) GuiFriends.actionButtonPanel = null;
	}
}