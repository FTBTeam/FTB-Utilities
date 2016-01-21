package ftb.utils.mod.client.gui.friends;

import ftb.lib.api.gui.widgets.PanelLM;

public abstract class PanelFriendsGui extends PanelLM
{
	public final GuiFriends gui;
	
	public PanelFriendsGui(GuiFriends g)
	{
		super(g, 0, 0, 0, 0);
		gui = g;
	}
}