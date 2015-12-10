package latmod.ftbu.mod.client.gui.friends;

import ftb.lib.api.PlayerAction;
import ftb.lib.gui.widgets.*;
import latmod.ftbu.mod.client.FTBUActions;
import latmod.ftbu.world.LMPlayerClient;
import latmod.lib.FastList;

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
	
	public void add(PlayerAction a)
	{ }
	
	public void addItems()
	{
		FastList<PlayerAction> list = FTBUActions.getActionsFor(playerLM);
		for(PlayerAction pa : list) menuButtons.add(new ButtonAction(this, pa));
	}
	
	public void onClosed(ButtonPopupMenu b, int mb)
	{
		if(b != null && mb == 0 && b.object instanceof PlayerAction)
			((PlayerAction)b.object).onClicked(gui.panelPlayerView.selectedPlayer.playerLM.playerID);
		if(mb == 0) gui.panelPopupMenu = null;
	}
}