package latmod.ftbu.mod.client.gui.friends;

import latmod.ftbu.core.gui.*;
import latmod.ftbu.core.util.FastList;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class ButtonAction extends ButtonPopupMenu
{
	public final PlayerAction action;
	
	public ButtonAction(PanelPopupPlayerActions p, PlayerAction a, String s)
	{
		super(p, GuiIcons.right, s);
		action = a;
		object = action;
	}
	
	public TextureCoords getIcon()
	{ return action.getIcon((GuiFriends)parentPanel.gui); }
	
	public void addMouseOverText(FastList<String> l)
	{ action.addMouseOverText(l); }
}