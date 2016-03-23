package ftb.utils.mod.client.gui.friends;

import cpw.mods.fml.relauncher.*;
import ftb.lib.api.client.GlStateManager;
import ftb.lib.api.gui.*;
import ftb.lib.api.gui.widgets.*;
import ftb.utils.net.ClientAction;
import ftb.utils.world.LMWorldClient;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.*;

import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiFriends extends GuiLM implements IClientActionGui
{
	public int notificationsWidth = 0;
	
	public final PanelPlayerList panelPlayerList;
	public final PanelPlayerView panelPlayerView;
	public final PanelText panelPlayerInfo;
	public PanelPopupMenu panelPopupMenu = null;
	public final TextBoxLM searchBox;
	
	public GuiFriends(GuiScreen gui)
	{
		super(gui, null);
		
		//PanelPlayerView.selectedPlayer = new 
		
		panelPlayerList = new PanelPlayerList(this);
		panelPlayerView = new PanelPlayerView(this);
		panelPlayerInfo = new PanelText(this);
		
		searchBox = new TextBoxLM(this, 0, 0, 130, 20)
		{
			public void returnPressed()
			{
				gui.refreshWidgets();
			}
		};
		
		searchBox.textRenderX = -1;
		searchBox.textRenderY = 6;
	}
	
	public void initLMGui()
	{
		mainPanel.width = width;
		mainPanel.height = height;
		ClientAction.REQUEST_PLAYER_INFO.send(LMWorldClient.inst.clientPlayerID);
		
		searchBox.posX = (mainPanel.width - searchBox.width) / 2;
		searchBox.posY = mainPanel.height - searchBox.height - 4;
	}
	
	public void addWidgets()
	{
		if(panelPlayerView.selectedPlayer != null)
		{
			panelPlayerInfo.text.clear();
			panelPlayerInfo.text.add(EnumChatFormatting.BOLD + panelPlayerView.selectedPlayer.getCommandSenderName());
			if(panelPlayerView.selectedPlayer.playerLM.isOnline)
				panelPlayerInfo.text.add(EnumChatFormatting.GREEN.toString() + '[' + FTBLibLang.label_online() + ']');
			
			for(IChatComponent c : panelPlayerView.selectedPlayer.playerLM.clientInfo)
				panelPlayerInfo.text.add(c.getFormattedText());
		}
		
		mainPanel.add(panelPlayerList);
		mainPanel.add(panelPlayerView);
		mainPanel.add(panelPlayerInfo);
		mainPanel.add(panelPopupMenu);
		mainPanel.add(searchBox);
		
		panelPlayerList.height = panelPlayerView.height = mainPanel.height;
		panelPlayerList.posX = 0;
		
		panelPlayerInfo.width = Math.max(100, panelPlayerInfo.width);
		panelPlayerInfo.posX = mainPanel.width - panelPlayerInfo.width;
		
		panelPlayerView.width = mainPanel.width - (panelPlayerList.width + panelPlayerInfo.width) - 2;
		panelPlayerView.posX = panelPlayerList.width;
	}
	
	public void drawBackground()
	{
		panelPlayerList.renderWidget();
		panelPlayerView.renderWidget();
		panelPlayerInfo.renderWidget();
		
		if(panelPopupMenu != null)
		{
			zLevel = 1000;
			panelPopupMenu.renderWidget();
			zLevel = 0;
		}
		
		GlStateManager.color(0F, 0F, 0F, 1F);
		drawBlankRect(panelPlayerView.posX - 1, 0, zLevel, 1, height);
		drawBlankRect(panelPlayerInfo.posX - 1, 0, zLevel, 1, height);
		drawBlankRect(width - panelPlayerInfo.width, panelPlayerInfo.height, zLevel, panelPlayerInfo.width, 1);
		
		GlStateManager.color(0.9F, 0.9F, 0.9F, 1F);
		drawBlankRect(searchBox.getAX() - 1, searchBox.getAY() - 1, zLevel, searchBox.width + 2, searchBox.height + 2);
		GlStateManager.color(0F, 0F, 0F, 1F);
		drawBlankRect(searchBox.getAX(), searchBox.getAY(), zLevel, searchBox.width, searchBox.height);
		GlStateManager.color(1F, 1F, 1F, 1F);
	}
	
	public void drawText(List<String> l)
	{
		searchBox.renderWidget();
		
		super.drawText(l);
	}
	
	public void onClientDataChanged()
	{
		refreshWidgets();
	}
}