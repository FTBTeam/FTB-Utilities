package latmod.ftbu.mod.client.gui.friends;

import ftb.lib.TextureCoords;
import ftb.lib.api.PlayerAction;
import ftb.lib.api.gui.GuiIcons;
import ftb.lib.gui.widgets.ButtonPopupMenu;

import java.util.List;

public class ButtonAction extends ButtonPopupMenu
{
	public final PlayerAction action;
	
	public ButtonAction(PanelPopupPlayerActions p, PlayerAction a)
	{
		super(p, GuiIcons.right, a.getDisplayName());
		action = a;
		object = action;
	}
	
	public TextureCoords getIcon()
	{ return action.icon; }
	
	public void addMouseOverText(List<String> l)
	{ action.addMouseOverText(l); }
}