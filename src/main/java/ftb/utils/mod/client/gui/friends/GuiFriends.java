package ftb.utils.mod.client.gui.friends;

import cpw.mods.fml.relauncher.*;
import ftb.lib.api.client.GlStateManager;
import ftb.lib.api.gui.*;
import ftb.lib.api.gui.widgets.PanelPopupMenu;
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
	
	public GuiFriends(GuiScreen gui)
	{
		super(gui, null);
		
		//PanelPlayerView.selectedPlayer = new 
		
		panelPlayerList = new PanelPlayerList(this);
		panelPlayerView = new PanelPlayerView(this);
		panelPlayerInfo = new PanelText(this);
	}
	
	public void initLMGui()
	{
		mainPanel.width = width;
		mainPanel.height = height;
		ClientAction.REQUEST_PLAYER_INFO.send(LMWorldClient.inst.clientPlayerID);
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
		GlStateManager.color(1F, 1F, 1F, 1F);
	}
	
	public void drawText(List<String> l)
	{
		super.drawText(l);
	}
	
	public void onClientDataChanged()
	{
		refreshWidgets();
	}
}