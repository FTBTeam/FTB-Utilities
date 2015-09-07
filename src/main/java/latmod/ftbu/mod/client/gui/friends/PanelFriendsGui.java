package latmod.ftbu.mod.client.gui.friends;

import latmod.ftbu.core.gui.PanelLM;
import cpw.mods.fml.relauncher.*;

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