package latmod.ftbu.mod.client.gui.friends;

import latmod.ftbu.util.gui.*;
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