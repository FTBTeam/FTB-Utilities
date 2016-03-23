package ftb.utils.mod.client.gui;

import cpw.mods.fml.relauncher.*;
import ftb.utils.api.guide.GuidePage;
import ftb.utils.world.LMPlayerClient;

/**
 * Created by LatvianModder on 23.03.2016.
 */
@SideOnly(Side.CLIENT)
public class GuideFriendsGUI extends GuidePage
{
	public GuideFriendsGUI(String id)
	{
		super(id);
	}
	
	public static class PlayerPage extends GuidePage
	{
		public final LMPlayerClient player;
		
		public PlayerPage(LMPlayerClient p)
		{
			super(p.getProfile().getName());
			player = p;
		}
		
		public void onPageClicked()
		{
		}
	}
}