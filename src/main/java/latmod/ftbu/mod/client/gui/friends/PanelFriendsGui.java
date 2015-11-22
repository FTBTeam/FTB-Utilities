package latmod.ftbu.mod.client.gui.friends;

import ftb.lib.gui.widgets.PanelLM;

public abstract class PanelFriendsGui extends PanelLM
{
	public final GuiFriends gui;
	
	public PanelFriendsGui(GuiFriends g)
	{
		super(g, 0, 0, 0, 0);
		gui = g;
	}
}