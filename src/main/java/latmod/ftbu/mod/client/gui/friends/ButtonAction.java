package latmod.ftbu.mod.client.gui.friends;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.core.gui.*;
import latmod.ftbu.core.util.FastList;

@SideOnly(Side.CLIENT)
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