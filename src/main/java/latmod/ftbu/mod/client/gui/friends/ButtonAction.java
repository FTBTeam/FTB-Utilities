package latmod.ftbu.mod.client.gui.friends;

import ftb.lib.api.PlayerAction;
import ftb.lib.api.gui.GuiIcons;
import ftb.lib.client.TextureCoords;
import ftb.lib.gui.widgets.ButtonPopupMenu;
import latmod.lib.FastList;

public class ButtonAction extends ButtonPopupMenu
{
	public final PlayerAction action;
	
	public ButtonAction(PanelPopupPlayerActions p, PlayerAction a)
	{
		super(p, GuiIcons.right, a.getTitle());
		action = a;
		object = action;
	}
	
	public TextureCoords getIcon()
	{ return action.icon; }
	
	public void addMouseOverText(FastList<String> l)
	{ action.addMouseOverText(l); }
}