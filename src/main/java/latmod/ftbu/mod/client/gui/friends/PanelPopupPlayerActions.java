package latmod.ftbu.mod.client.gui.friends;

import latmod.ftbu.core.client.FTBULang;
import latmod.ftbu.core.gui.*;
import latmod.ftbu.core.world.*;
import latmod.ftbu.mod.client.minimap.Waypoints;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class PanelPopupPlayerActions extends PanelPopupMenu
{
	public final GuiFriends gui;
	public final LMPlayerClient playerLM;
	
	public PanelPopupPlayerActions(GuiFriends g, int x, int y, LMPlayerClient p)
	{
		super(g, x, y, 18);
		gui = g;
		playerLM = p;
	}
	
	public void add(PlayerAction a, String s)
	{ if(a != null) menuButtons.add(new ButtonAction(this, a, s)); }
	
	public void addItems()
	{
		if(playerLM.equalsPlayer(LMWorldClient.inst.clientPlayer))
		{
			add(PlayerAction.settings, FTBULang.client_config);
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
			
			add(PlayerAction.mail, "Mail"); //LANG
			add(PlayerAction.trade, "Trade"); //LANG
		}
	}
	
	public void onClosed(ButtonPopupMenu b, int mb)
	{
		if(b != null && mb == 0 && b.object instanceof PlayerAction)
			((PlayerAction)b.object).onClicked(gui);
		if(mb == 0) gui.panelPopupMenu = null;
	}
}