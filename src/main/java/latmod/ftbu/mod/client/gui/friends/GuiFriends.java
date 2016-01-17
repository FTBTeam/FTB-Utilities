package latmod.ftbu.mod.client.gui.friends;

import ftb.lib.api.gui.*;
import ftb.lib.api.gui.widgets.PanelPopupMenu;
import latmod.ftbu.net.ClientAction;
import latmod.ftbu.world.LMWorldClient;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.*;
import net.minecraftforge.fml.relauncher.*;

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
			panelPlayerInfo.text.add(EnumChatFormatting.BOLD + panelPlayerView.selectedPlayer.getName());
			if(panelPlayerView.selectedPlayer.playerLM.isOnline)
				panelPlayerInfo.text.add(EnumChatFormatting.GREEN.toString() + '[' + FTBLibLang.label_online() + ']');
			
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