package ftb.utils.mod.client.gui.friends;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ftb.lib.api.PlayerAction;
import ftb.lib.api.gui.PlayerActionRegistry;
import ftb.lib.mod.client.gui.info.GuiInfo;
import ftb.utils.mod.client.FTBUActions;
import ftb.utils.world.LMWorldClient;

/**
 * Created by LatvianModder on 24.03.2016.
 */
@SideOnly(Side.CLIENT)
public class InfoFriendsGUISelfPage extends InfoFriendsGUIPage
{
	public InfoFriendsGUISelfPage()
	{
		super(LMWorldClient.inst.clientPlayer);
	}
	
	@Override
	public void refreshGui(GuiInfo gui)
	{
		clear();
		
		text.add(new InfoPlayerViewLine(this, playerLM));
		
		if(!playerLM.clientInfo.isEmpty())
		{
			for(String s : playerLM.clientInfo)
				printlnText(s);
			
			text.add(null);
		}
		
		text.add(new InfoPlayerActionLine(this, playerLM, FTBUActions.my_server_settings));
		text.add(null);
		
		for(PlayerAction a : PlayerActionRegistry.getPlayerActions(PlayerAction.Type.SELF, LMWorldClient.inst.clientPlayer, LMWorldClient.inst.clientPlayer, true, true))
		{
			if(a != FTBUActions.my_server_settings)
			{
				text.add(new InfoPlayerActionLine(this, playerLM, a));
			}
		}
	}
}