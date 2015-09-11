package latmod.ftbu.mod.client.gui.friends;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.core.gui.PanelLM;

@SideOnly(Side.CLIENT)
public abstract class PanelFriendsGui extends PanelLM
{
	public final GuiFriends gui;
	
	public PanelFriendsGui(GuiFriends g)
	{
		super(g, 0, 0, 0, 0);
		gui = g;
	}
}