package ftb.utils.mod.client.gui.friends;

import com.google.gson.JsonPrimitive;
import cpw.mods.fml.relauncher.*;
import ftb.lib.PrivacyLevel;
import ftb.lib.api.PlayerAction;
import ftb.lib.api.gui.PlayerActionRegistry;
import ftb.lib.api.notification.*;
import ftb.lib.mod.FTBLibMod;
import ftb.utils.api.guide.GuidePage;
import ftb.utils.api.guide.lines.GuideExtendedTextLine;
import ftb.utils.mod.FTBU;
import ftb.utils.world.*;
import net.minecraft.util.*;

/**
 * Created by LatvianModder on 24.03.2016.
 */
@SideOnly(Side.CLIENT)
public class GuideFriendsGUISelfPage extends GuideFriendsGUIPage
{
	public GuideFriendsGUISelfPage()
	{
		super(LMWorldClient.inst.clientPlayer);
		onClientDataChanged();
	}
	
	public void onClientDataChanged()
	{
		clear();
		
		for(PlayerAction a : PlayerActionRegistry.getPlayerActions(PlayerAction.Type.SELF, LMWorldClient.inst.clientPlayer, LMWorldClient.inst.clientPlayer, true, true))
		{
			text.add(new GuidePlayerActionLine(this, playerLM, a));
		}
		
		GuidePage page = getSub("info").setTitle(new ChatComponentTranslation("ftbl.button.info"));
		
		page.text.add(new GuidePlayerViewLine(this, playerLM));
		
		if(!playerLM.clientInfo.isEmpty())
		{
			for(String s : playerLM.clientInfo)
				page.printlnText(s);
			
			page.text.add(null);
		}
		
		page = new GuidePage("settings")
		{
			public void onClientDataChanged()
			{
				clear();
				
				PersonalSettings ps = LMWorldClient.inst.clientPlayer.getSettings();
				
				booleanCommand("chat_links", ps.get(PersonalSettings.CHAT_LINKS));
				booleanCommand("render_badge", LMWorldClient.inst.clientPlayer.renderBadge);
				booleanCommand("explosions", ps.get(PersonalSettings.EXPLOSIONS));
				booleanCommand("fake_players", ps.get(PersonalSettings.FAKE_PLAYERS));
				
				IChatComponent text1 = FTBLibMod.mod.chatComponent("security." + ps.blocks.uname);
				text1.getChatStyle().setColor(ps.blocks == PrivacyLevel.FRIENDS ? EnumChatFormatting.BLUE : (ps.blocks == PrivacyLevel.PUBLIC ? EnumChatFormatting.GREEN : EnumChatFormatting.RED));
				GuideExtendedTextLine line = new GuideExtendedTextLine(this, FTBU.mod.chatComponent("player_setting.security_level").appendText(": ").appendSibling(text1));
				line.setClickAction(new ClickAction(ClickActionType.CMD, new JsonPrimitive("lmplayer_settings block_security toggle")));
				text.add(line);
			}
			
			private void booleanCommand(String s, boolean current)
			{
				ChatComponentText text1 = new ChatComponentText(Boolean.toString(current));
				text1.getChatStyle().setColor(current ? EnumChatFormatting.GREEN : EnumChatFormatting.RED);
				GuideExtendedTextLine line = new GuideExtendedTextLine(this, FTBU.mod.chatComponent("player_setting." + s).appendText(": ").appendSibling(text1));
				line.setClickAction(new ClickAction(ClickActionType.CMD, new JsonPrimitive("lmplayer_settings " + s + " toggle")));
				text.add(line);
			}
		};
		
		page.setTitle(new ChatComponentTranslation("ftbl.button.settings"));
		page.onClientDataChanged();
		addSub(page);
	}
}