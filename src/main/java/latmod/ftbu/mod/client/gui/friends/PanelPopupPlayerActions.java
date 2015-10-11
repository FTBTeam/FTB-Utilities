package latmod.ftbu.mod.client.gui.friends;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.util.gui.*;
import latmod.ftbu.world.LMPlayerClient;
import latmod.lib.FastList;

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
	
	public void add(PlayerAction a)
	{ }
	
	public void addItems()
	{
		FastList<PlayerAction> list = PlayerAction.getActionsFor(playerLM);
		for(PlayerAction pa : list) menuButtons.add(new ButtonAction(this, pa));
	}
	
	public void onClosed(ButtonPopupMenu b, int mb)
	{
		if(b != null && mb == 0 && b.object instanceof PlayerAction)
			((PlayerAction)b.object).onClicked(gui.panelPlayerView.selectedPlayer.playerLM);
		if(mb == 0) gui.panelPopupMenu = null;
	}
}