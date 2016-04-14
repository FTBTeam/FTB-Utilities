package ftb.utils.mod.client.gui.friends;

import com.google.gson.JsonPrimitive;
import cpw.mods.fml.relauncher.*;
import ftb.lib.PrivacyLevel;
import ftb.lib.api.*;
import ftb.lib.api.gui.PlayerActionRegistry;
import ftb.lib.api.info.InfoPage;
import ftb.lib.api.info.lines.InfoExtendedTextLine;
import ftb.lib.api.notification.*;
import ftb.lib.mod.client.gui.info.GuiInfo;
import ftb.utils.world.*;
import net.minecraft.util.*;

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
	
	public void refreshGui(GuiInfo gui)
	{
		clear();
		
		for(PlayerAction a : PlayerActionRegistry.getPlayerActions(PlayerAction.Type.SELF, LMWorldClient.inst.clientPlayer, LMWorldClient.inst.clientPlayer, true, true))
		{
			text.add(new InfoPlayerActionLine(this, playerLM, a));
		}
		
		InfoPage page = getSub("info").setTitle(new ChatComponentTranslation("ftbl.button.info"));
		
		page.text.add(new InfoPlayerViewLine(this, playerLM));
		
		if(!playerLM.clientInfo.isEmpty())
		{
			for(String s : playerLM.clientInfo)
				page.printlnText(s);
			
			page.text.add(null);
		}
		
		page = new InfoPage("settings")
		{
			public void refreshGui(GuiInfo gui)
			{
				clear();
				
				PersonalSettings ps = LMWorldClient.inst.clientPlayer.getSettings();
				
				booleanCommand("chat_links", ps.get(PersonalSettings.CHAT_LINKS));
				booleanCommand("render_badge", LMWorldClient.inst.clientPlayer.renderBadge);
				booleanCommand("explosions", ps.get(PersonalSettings.EXPLOSIONS));
				booleanCommand("fake_players", ps.get(PersonalSettings.FAKE_PLAYERS));
				
				IChatComponent text1 = ps.blocks.lang.chatComponent();
				text1.getChatStyle().setColor(ps.blocks == PrivacyLevel.FRIENDS ? EnumChatFormatting.BLUE : (ps.blocks == PrivacyLevel.PUBLIC ? EnumChatFormatting.GREEN : EnumChatFormatting.RED));
				InfoExtendedTextLine line = new InfoExtendedTextLine(this, new ChatComponentTranslation("ftbu.player_setting.security_level").appendText(": ").appendSibling(text1));
				line.setClickAction(new ClickAction(ClickActionType.CMD, new JsonPrimitive("lmplayer_settings block_security toggle")));
				text.add(line);
			}
			
			private void booleanCommand(String s, boolean current)
			{
				ChatComponentText text1 = new ChatComponentText(Boolean.toString(current));
				text1.getChatStyle().setColor(current ? EnumChatFormatting.GREEN : EnumChatFormatting.RED);
				InfoExtendedTextLine line = new InfoExtendedTextLine(this, new ChatComponentTranslation("ftbu.player_setting." + s).appendText(": ").appendSibling(text1));
				line.setClickAction(new ClickAction(ClickActionType.CMD, new JsonPrimitive("lmplayer_settings " + s + " toggle")));
				text.add(line);
			}
		};
		
		page.setTitle(GuiLang.button_settings.chatComponent());
		addSub(page);
	}
}