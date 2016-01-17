package latmod.ftbu.mod.client.gui.friends;

import ftb.lib.api.PlayerAction;
import ftb.lib.api.gui.PlayerActionRegistry;
import ftb.lib.gui.GuiLM;
import ftb.lib.gui.widgets.*;
import latmod.ftbu.world.*;
import net.minecraft.client.renderer.GlStateManager;

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
		
		List<PlayerAction> list = PlayerActionRegistry.getPlayerActions(PlayerAction.Type.OTHER, LMWorldClient.inst.clientPlayer, playerLM, true);
		for(int i = 0; i < list.size(); i++)
		{
			ButtonPlayerActionSmall b = new ButtonPlayerActionSmall(this, list.get(i));
			mainPanel.add(b);
			mainPanel.width = Math.max(mainPanel.width, b.width);
			if(i != list.size() - 1) mainPanel.height += b.height + 4;
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
			title = a.getDisplayName();
			width = 22 + g.getFontRenderer().getStringWidth(title);
		}
		
		public void onButtonPressed(int b)
		{
			gui.container.player.closeScreen();
			action.onClicked(LMWorldClient.inst.clientPlayer, gui.playerLM);
		}
		
		public void renderWidget()
		{
			int ax = getAX();
			int ay = getAY();
			
			GlStateManager.color(0.46F, 0.46F, 0.46F, 0.53F);
			GuiLM.drawBlankRect(ax, ay, gui.getZLevel(), width, height);
			gui.render(action.icon, ax + 1, ay + 1);
			
			gui.getFontRenderer().drawString(title, ax + 20, ay + 6, 0xFFFFFFFF);
			GlStateManager.color(1F, 1F, 1F, 0.2F);
			if(mouseOver(ax, ay)) GuiLM.drawBlankRect(ax, ay, gui.getZLevel(), width, height);
			GlStateManager.color(1F, 1F, 1F, 1F);
		}
		
		public void addMouseOverText(List<String> l)
		{
		}
	}
}