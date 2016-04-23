package ftb.utils.mod.client.gui.friends;

import cpw.mods.fml.relauncher.*;
import ftb.lib.api.friends.*;
import ftb.lib.api.info.InfoPage;
import ftb.lib.mod.client.gui.info.GuiInfo;
import ftb.utils.mod.client.FTBUClient;
import ftb.utils.world.*;
import latmod.lib.LMColor;
import net.minecraft.util.ChatComponentText;

import java.util.*;

/**
 * Created by LatvianModder on 23.03.2016.
 */
@SideOnly(Side.CLIENT)
public class InfoFriendsGUI extends InfoPage
{
	public InfoFriendsGUI()
	{
		super("friends_gui");
		setTitle(new ChatComponentText("FriendsGUI"));
		backgroundColor = new LMColor.RGB(30, 30, 30);
		textColor = new LMColor.RGB(200, 200, 200);
		useUnicodeFont = Boolean.FALSE;
	}
	
	@Override
	public void refreshGui(GuiInfo gui)
	{
		clear();
		
		List<LMPlayer> tempPlayerList = new ArrayList<>();
		tempPlayerList.addAll(LMWorldClient.inst.playerMap.values());
		
		tempPlayerList.remove(LMWorldClient.inst.clientPlayer);
		
		if(FTBUClient.sort_friends_az.getAsBoolean()) Collections.sort(tempPlayerList, LMPNameComparator.instance);
		else Collections.sort(tempPlayerList, new LMPStatusComparator(LMWorldClient.inst.clientPlayer));
		
		addSub(new InfoFriendsGUISelfPage());
		
		for(LMPlayer p : tempPlayerList)
		{
			addSub(new InfoFriendsGUIPage(p.toPlayerSP()));
		}
	}
}