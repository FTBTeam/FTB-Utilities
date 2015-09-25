package latmod.ftbu.mod.client.gui.friends;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.core.client.*;
import latmod.ftbu.core.gui.*;
import latmod.ftbu.core.net.*;
import latmod.ftbu.core.util.FastList;
import latmod.ftbu.core.world.LMWorldClient;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.*;

@SideOnly(Side.CLIENT)
public class GuiFriends extends GuiLM implements IClientActionGui
{
	public final GuiScreen parentScreen;
	public int notificationsWidth = 0;
	
	public final PanelPlayerList panelPlayerList;
	public final PanelPlayerView panelPlayerView;
	public final PanelNotifications panelNotifications;
	public final PanelText panelPlayerInfo;
	public PanelPopupMenu panelPopupMenu = null;
	
	public GuiFriends(GuiScreen gui)
	{
		super(null, null);
		parentScreen = gui;
		hideNEI = true;
		xSize = ySize = 0;
		
		//PanelPlayerView.selectedPlayer = new 
		
		panelPlayerList = new PanelPlayerList(this);
		panelPlayerView = new PanelPlayerView(this);
		panelNotifications = new PanelNotifications(this);
		panelPlayerInfo = new PanelText(this);
	}
	
	public void initLMGui()
	{
		ClientAction.ACTION_REQUEST_PLAYER_INFO.send(0);
		xSize = width;
		ySize = height;
	}
	
	public void addWidgets()
	{
		if(panelPlayerView.selectedPlayer != null)
		{
			panelPlayerInfo.text.clear();
			panelPlayerInfo.text.add(EnumChatFormatting.BOLD + panelPlayerView.selectedPlayer.getCommandSenderName());
			if(panelPlayerView.selectedPlayer.playerLM.isOnline)
				panelPlayerInfo.text.add(EnumChatFormatting.GREEN + "[" + FTBULang.Friends.label_online() + "]");
			
			if(!panelPlayerView.selectedPlayer.isOwner)
			{
				boolean raw1 = panelPlayerView.selectedPlayer.playerLM.isFriendRaw(LMWorldClient.inst.clientPlayer);
				boolean raw2 = LMWorldClient.inst.clientPlayer.isFriendRaw(panelPlayerView.selectedPlayer.playerLM);
				
				if(raw1 && raw2)
					panelPlayerInfo.text.add(EnumChatFormatting.GREEN + "[" + FTBULang.Friends.label_friend() + "]");
				else if(raw1 || raw2)
					panelPlayerInfo.text.add((raw1 ? EnumChatFormatting.GOLD : EnumChatFormatting.BLUE) + "[" + FTBULang.Friends.label_pfriend() + "]");
			}
			
			for(IChatComponent c : panelPlayerView.selectedPlayer.playerLM.clientInfo)
				panelPlayerInfo.text.add(c.getFormattedText());
		}
		
		mainPanel.add(panelPlayerList);
		mainPanel.add(panelPlayerView);
		mainPanel.add(panelNotifications);
		mainPanel.add(panelPlayerInfo);
		mainPanel.add(panelPopupMenu);
		
		panelPlayerList.height = panelPlayerView.height = ySize;
		panelPlayerList.posX = 0;
		panelNotifications.height = height - panelPlayerInfo.height;
		panelNotifications.posY = height - panelNotifications.height;
		
		panelNotifications.width = panelPlayerInfo.width = Math.max(100, Math.max(panelNotifications.width, panelPlayerInfo.width));
		panelNotifications.posX = panelPlayerInfo.posX = xSize - panelNotifications.width;
		
		for(ButtonNotification b : panelNotifications.notificationButtons)
			b.width = panelNotifications.width;
		
		panelPlayerView.width = xSize - (panelPlayerList.width + panelNotifications.width) - 2;
		panelPlayerView.posX = panelPlayerList.width;
	}
	
	public void drawBackground()
	{
		panelPlayerList.renderWidget();
		panelPlayerView.renderWidget();
		panelNotifications.renderWidget();
		panelPlayerInfo.renderWidget();
		
		if(panelPopupMenu != null)
		{
			zLevel = 1000;
			panelPopupMenu.renderWidget();
			zLevel = 0;
		}
		
		drawBlankRect(panelPlayerView.posX - 1, 0, zLevel, 1, height, 0xFF000000);
		drawBlankRect(panelNotifications.posX - 1, 0, zLevel, 1, height, 0xFF000000);
		drawBlankRect(panelNotifications.posX, panelNotifications.posY, zLevel, panelNotifications.width, 1, 0xFF000000);
		
		LatCoreMCClient.notifyClient("notify", panelNotifications.width, 1);
	}
	
	public void drawText(FastList<String> l)
	{
		super.drawText(l);
	}
	
	public void onClientDataChanged()
	{
		refreshWidgets();
	}
}