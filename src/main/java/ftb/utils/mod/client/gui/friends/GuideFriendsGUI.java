package ftb.utils.mod.client.gui.friends;

import cpw.mods.fml.relauncher.*;
import ftb.lib.api.friends.*;
import ftb.utils.api.guide.GuidePage;
import ftb.utils.mod.client.FTBUClient;
import ftb.utils.world.*;
import latmod.lib.LMColor;
import net.minecraft.util.ChatComponentText;

import java.util.*;

/**
 * Created by LatvianModder on 23.03.2016.
 */
@SideOnly(Side.CLIENT)
public class GuideFriendsGUI extends GuidePage
{
	public GuideFriendsGUI()
	{
		super("friends_gui");
		setTitle(new ChatComponentText("FriendsGUI"));
		
		List<LMPlayer> tempPlayerList = new ArrayList<>();
		tempPlayerList.addAll(LMWorldClient.inst.playerMap.values());
		
		tempPlayerList.remove(LMWorldClient.inst.clientPlayer);
		
		if(FTBUClient.sort_friends_az.getAsBoolean()) Collections.sort(tempPlayerList, LMPNameComparator.instance);
		else Collections.sort(tempPlayerList, new LMPStatusComparator(LMWorldClient.inst.clientPlayer));
		
		addSub(new GuideFriendsGUISelfPage());
		
		for(LMPlayer p : tempPlayerList)
		{
			addSub(new GuideFriendsGUIPage(p.toPlayerSP()));
		}
	}
	
	public LMColor getBackgroundColor()
	{ return new LMColor.RGB(30, 30, 30); }
	
	public LMColor getTextColor()
	{ return new LMColor.RGB(200, 200, 200); }
	
	public Boolean useUnicodeFont()
	{ return Boolean.FALSE; }
}