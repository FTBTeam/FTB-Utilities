package latmod.ftbu.mod.client.gui.friends;

import ftb.lib.api.PlayerAction;
import ftb.lib.gui.GuiLM;
import ftb.lib.gui.widgets.*;
import latmod.ftbu.mod.client.FTBUActions;
import latmod.ftbu.world.LMPlayerClient;
import latmod.lib.FastList;

import java.util.List;

public class GuiFriendsGuiSmall extends GuiLM
{
	public final LMPlayerClient playerLM;
	
	public GuiFriendsGuiSmall(LMPlayerClient p)
	{
		super(null, null);
		playerLM = p;
		hideNEI = true;
	}
	
	public void addWidgets()
	{
		mainPanel.width = 0;
		mainPanel.height = 0;
		
		FastList<PlayerAction> list = FTBUActions.getActionsFor(playerLM);
		for(int i = 0; i < list.size(); i++)
		{
			ButtonPlayerActionSmall b = new ButtonPlayerActionSmall(this, list.get(i));
			mainPanel.add(b);
			mainPanel.width = Math.max(mainPanel.width, b.width);
			if(i != list.size() - 1)
				mainPanel.height += b.height + 4;
		}
		
		for(WidgetLM w : mainPanel.widgets)
			w.width = mainPanel.width;
		
		xSize = mainPanel.width;
		ySize = mainPanel.height;
		mainPanel.posX = guiLeft = (width - xSize) / 2;
		mainPanel.posY = guiTop = (height - ySize) / 2 - 20;
	}
	
	public void drawBackground()
	{
		for(WidgetLM w : mainPanel.widgets)
			w.renderWidget();
	}
	
	public static class ButtonPlayerActionSmall extends ButtonLM
	{
		public final GuiFriendsGuiSmall gui;
		public final PlayerAction action;
		
		public ButtonPlayerActionSmall(GuiFriendsGuiSmall g, PlayerAction a)
		{
			super(g, 0, g.mainPanel.height, 0, 18);
			gui = g;
			action = a;
			title = a.getTitle();
			width = 22 + g.getFontRenderer().getStringWidth(title);
		}
		
		public void onButtonPressed(int b)
		{
			gui.container.player.closeScreen();
			action.onClicked(gui.playerLM.playerID);
		}
		
		public void renderWidget()
		{
			int ax = getAX();
			int ay = getAY();
			
			GuiLM.drawBlankRect(ax, ay, gui.getZLevel(), width, height, 0x88777777);
			gui.render(action.icon, ax + 1, ay + 1);
			gui.getFontRenderer().drawString(title, ax + 20, ay + 6, 0xFFFFFFFF);
			
			if(mouseOver(ax, ay)) GuiLM.drawBlankRect(ax, ay, gui.getZLevel(), width, height, 0x33FFFFFF);
		}
		
		public void addMouseOverText(List<String> l)
		{
		}
	}
}