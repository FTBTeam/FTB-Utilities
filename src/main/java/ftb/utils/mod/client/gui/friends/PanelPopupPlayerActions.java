package ftb.utils.mod.client.gui.friends;

import ftb.lib.api.PlayerAction;
import ftb.lib.api.friends.*;
import ftb.lib.api.gui.PlayerActionRegistry;
import ftb.lib.api.gui.widgets.*;

public class PanelPopupPlayerActions extends PanelPopupMenu
{
	public final GuiFriends gui;
	public final LMPlayerSP playerLM;
	
	public PanelPopupPlayerActions(GuiFriends g, int x, int y, LMPlayerSP p)
	{
		super(g, x, y, 18);
		gui = g;
		playerLM = p;
	}
	
	public void add(PlayerAction a)
	{
	}
	
	public void addItems()
	{
		for(PlayerAction a : PlayerActionRegistry.getPlayerActions((LMWorldSP.inst.clientPlayer == playerLM) ? PlayerAction.Type.SELF : PlayerAction.Type.OTHER, LMWorldSP.inst.clientPlayer, playerLM, true))
			menuButtons.add(new ButtonAction(this, a));
	}
	
	public void onClosed(ButtonPopupMenu b, int mb)
	{
		if(b != null && mb == 0 && b.object instanceof PlayerAction)
			((PlayerAction) b.object).onClicked(LMWorldSP.inst.clientPlayer, gui.panelPlayerView.selectedPlayer.playerLM);
		if(mb == 0) gui.panelPopupMenu = null;
	}
}