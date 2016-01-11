package latmod.ftbu.mod.client.gui.friends;

import cpw.mods.fml.relauncher.*;
import ftb.lib.api.gui.*;
import ftb.lib.gui.GuiLM;
import ftb.lib.gui.widgets.PanelPopupMenu;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.net.ClientAction;
import latmod.ftbu.world.LMWorldClient;
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
		super(gui, null, null);
		hideNEI = true;
		xSize = ySize = 0;
		
		//PanelPlayerView.selectedPlayer = new 
		
		panelPlayerList = new PanelPlayerList(this);
		panelPlayerView = new PanelPlayerView(this);
		panelPlayerInfo = new PanelText(this);
	}
	
	public void initLMGui()
	{
		ClientAction.REQUEST_PLAYER_INFO.send(LMWorldClient.inst.clientPlayerID);
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
				panelPlayerInfo.text.add(EnumChatFormatting.GREEN.toString() + '[' + FTBLibLang.label_online() + ']');
			
			if(!panelPlayerView.selectedPlayer.isOwner)
			{
				boolean raw1 = panelPlayerView.selectedPlayer.playerLM.isFriendRaw(LMWorldClient.inst.getClientPlayer());
				boolean raw2 = LMWorldClient.inst.getClientPlayer().isFriendRaw(panelPlayerView.selectedPlayer.playerLM);
				
				if(raw1 && raw2)
					panelPlayerInfo.text.add(EnumChatFormatting.GREEN.toString() + '[' + FTBU.mod.translateClient("label.friend") + ']');
				else if(raw1 || raw2)
					panelPlayerInfo.text.add("" + (raw1 ? EnumChatFormatting.GOLD : EnumChatFormatting.BLUE) + '[' + FTBU.mod.translateClient("label.pfriend") + ']');
			}
			
			for(IChatComponent c : panelPlayerView.selectedPlayer.playerLM.clientInfo)
				panelPlayerInfo.text.add(c.getFormattedText());
		}
		
		mainPanel.add(panelPlayerList);
		mainPanel.add(panelPlayerView);
		mainPanel.add(panelPlayerInfo);
		mainPanel.add(panelPopupMenu);
		
		panelPlayerList.height = panelPlayerView.height = ySize;
		panelPlayerList.posX = 0;
		
		panelPlayerInfo.width = Math.max(100, panelPlayerInfo.width);
		panelPlayerInfo.posX = xSize - panelPlayerInfo.width;
		
		panelPlayerView.width = xSize - (panelPlayerList.width + panelPlayerInfo.width) - 2;
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
		
		drawBlankRect(panelPlayerView.posX - 1, 0, zLevel, 1, height, 0xFF000000);
		drawBlankRect(panelPlayerInfo.posX - 1, 0, zLevel, 1, height, 0xFF000000);
		drawBlankRect(width - panelPlayerInfo.width, panelPlayerInfo.height, zLevel, panelPlayerInfo.width, 1, 0xFF000000);
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