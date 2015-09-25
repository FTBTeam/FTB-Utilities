package latmod.ftbu.mod.client.gui.friends;

import cpw.mods.fml.relauncher.*;
import latmod.core.util.FastList;
import latmod.ftbu.api.EventPlayerAction;
import latmod.ftbu.mod.FTBUFinals;
import latmod.ftbu.util.gui.*;
import latmod.ftbu.world.*;

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
		FastList<PlayerAction> list = new FastList<PlayerAction>();
		
		if(playerLM.equalsPlayer(LMWorldClient.inst.clientPlayer))
		{
			list.add(PlayerAction.settings);
			list.add(PlayerAction.minimap);
			
			if(FTBUFinals.DEV)
			{
				list.add(PlayerAction.notes);
			}
		}
		else
		{
			boolean isFriend = LMWorldClient.inst.clientPlayer.isFriendRaw(playerLM);
			if(!isFriend) list.add(PlayerAction.friend_add);
			
			if(FTBUFinals.DEV)
			{
				list.add(PlayerAction.mail);
				list.add(PlayerAction.trade);
			}
			
			if(isFriend) list.add(PlayerAction.friend_remove);
			else if(playerLM.isFriendRaw(LMWorldClient.inst.clientPlayer))
				list.add(PlayerAction.friend_deny);
		}
		
		new EventPlayerAction(list, playerLM).post();
		for(PlayerAction pa : list) menuButtons.add(new ButtonAction(this, pa));
	}
	
	public void onClosed(ButtonPopupMenu b, int mb)
	{
		if(b != null && mb == 0 && b.object instanceof PlayerAction)
			((PlayerAction)b.object).onClicked(gui);
		if(mb == 0) gui.panelPopupMenu = null;
	}
}