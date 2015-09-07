package latmod.ftbu.mod.client.gui.friends;

import latmod.ftbu.core.gui.*;
import latmod.ftbu.core.net.*;
import latmod.ftbu.core.util.FastList;
import net.minecraft.client.gui.GuiScreen;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class GuiFriends extends GuiLM implements IClientActionGui
{
	public final GuiScreen parentScreen;
	public int notificationsWidth = 0;
	
	public final PanelPlayerList panelPlayerList;
	public final PanelPlayerView panelPlayerView;
	public final PanelNotifications panelNotifications;
	public PanelPopupPlayerActions panelPopupMenu = null;
	
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
	}
	
	public void initLMGui()
	{
		LMNetHelper.sendToServer(new MessageLMPlayerRequestInfo(0));
		xSize = width;
		ySize = height;
	}
	
	public void addWidgets()
	{
		mainPanel.add(panelPlayerList);
		mainPanel.add(panelPlayerView);
		mainPanel.add(panelNotifications);
		mainPanel.add(panelPopupMenu);
		
		//TODO: Remove this
		panelNotifications.width = 200;
		
		panelPlayerList.height = panelPlayerView.height = panelNotifications.height = ySize;
		panelPlayerList.posX = 0;
		panelNotifications.posX = xSize - panelNotifications.width;
		panelPlayerView.width = xSize - (panelPlayerList.width + panelNotifications.width) - 2;
		panelPlayerView.posX = panelPlayerList.width;
		
		/*if(notificationsGuiOpen)
		{
			notificationsWidth = MathHelperLM.max(100, guiLeft - 10, getMaxNTextLength(), fontRendererObj.getStringWidth(FTBULang.Friends.notifications) + 30).intValue();
			notificationButtons.clear();
			ClientNotifications.perm.sort(null);
			
			for(int i = 0; i < ClientNotifications.perm.size(); i++)
			{
				ButtonNotification b = new ButtonNotification(this, ClientNotifications.perm.get(i));
				if(b.index * 26 + 16 <= height) notificationButtons.add(b);
			}
			
			mainPanel.addAll(notificationButtons);
			
			ButtonLM b = new ButtonLM(this, -getPosX(0) + 2, -getPosY(0) + 2, notificationsWidth, 14)
			{
				public void onButtonPressed(int b)
				{
					notificationsGuiOpen = false;
					refreshWidgets();
				}
			};
			
			b.title = FTBULang.button_close;
			mainPanel.add(b);
		}*/
	}
	
	/*
	private int getMaxNTextLength()
	{
		int s = 0;
		
		for(ClientNotifications.PermNotification n : ClientNotifications.perm)
		{
			int l = fontRendererObj.getStringWidth(n.notification.title.getFormattedText());
			if(n.notification.getDesc() != null)
				l = Math.max(l, fontRendererObj.getStringWidth(n.notification.getDesc().getFormattedText()));
			
			if(n.notification.getItem() != null) l += 20;
			l += 6;
			
			if(l > s) s = l;
		}
		
		return s;
	}*/
	
	public void drawBackground()
	{
		panelPlayerList.renderWidget();
		panelPlayerView.renderWidget();
		panelNotifications.renderWidget();
		
		if(panelPopupMenu != null)
		{
			zLevel = 1000;
			panelPopupMenu.renderWidget();
			zLevel = 0;
		}
		
		drawBlankRect(panelPlayerView.posX - 1, 0, 1, height, 0xFF000000);
		drawBlankRect(panelNotifications.posX - 1, 0, 1, height, 0xFF000000);
		
		/*
		if(players.size() < pbPlayers.length)
			scrollbar.value = 0F;
		else if(scrollbar.update())
			refreshPlayers();
		
		super.drawBackground();
		
		pbOwner.render();
		for(int i = 0; i < pbPlayers.length; i++)
			pbPlayers[i].render();
		
		buttonSave.render(GuiIcons.accept);
		buttonSort.render(GuiIcons.sort);
		
		buttonArmor.render(GuiIcons.jacket);
		if(FTBUClient.hideArmorFG.getB()) buttonArmor.render(GuiIcons.close);
		
		scrollbar.renderSlider(tex_slider);
		
		if(panelPopupMenu != null)
			panelPopupMenu.render();
		
		if(notificationsGuiOpen)
		{
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			
			drawRect(0, 0, notificationsWidth + 4, height, 0x33666666);
			drawRect(2, 2, notificationsWidth + 2, 16, 0xFF666666);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			fontRendererObj.drawString(FTBULang.Friends.notifications + " [" + ClientNotifications.perm.size() + "]", 6, 5, 0xFFFFFFFF);
			for(ButtonNotification b : notificationButtons) b.render();
		}*/
	}
	
	public void drawText(FastList<String> l)
	{
		//searchBox.render(107, 10, 0xFFA7A7A7);
		//drawString(getFontRenderer(), selectedPlayer.playerLM.getName(), guiLeft + 5, guiTop + ySize + 1, 0xFFFFFFFF);
		super.drawText(l);
	}
	
	public void onClientDataChanged()
	{
		refreshWidgets();
	}
}