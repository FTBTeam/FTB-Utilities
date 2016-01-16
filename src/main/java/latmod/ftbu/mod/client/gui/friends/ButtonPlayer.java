package latmod.ftbu.mod.client.gui.friends;

import ftb.lib.gui.GuiLM;
import ftb.lib.gui.widgets.ButtonLM;
import latmod.ftbu.net.ClientAction;
import latmod.ftbu.world.*;
import net.minecraft.client.renderer.GlStateManager;

import java.util.List;

public class ButtonPlayer extends ButtonLM
{
	public final Player player;
	public final PanelPlayerList panel;
	
	public ButtonPlayer(PanelPlayerList pl, LMPlayerClient p)
	{
		super(pl.gui, 0, pl.playerButtons.size() * 21, 34 + pl.gui.getFontRenderer().getStringWidth(p.getProfile().getName()), 20);
		player = new Player(p);
		panel = pl;
	}
	
	public void onButtonPressed(int b)
	{
		if(b == 2) return;
		
		if(player != null && player.playerLM != null)
		{
			panel.gui.panelPlayerView.selectedPlayer = new Player(player.playerLM);
			//FIXME: panel.gui.panelPlayerView.selectedPlayer.func_152121_a(MinecraftProfileTexture.Type.SKIN, AbstractClientPlayer.getLocationSkin(player.playerLM.getName()));
			panel.gui.panelPlayerView.selectedPlayer.inventory.currentItem = 0;
			ClientAction.REQUEST_PLAYER_INFO.send(player.playerLM.playerID);
			if(b != 0)
				panel.gui.panelPopupMenu = new PanelPopupPlayerActions((GuiFriends) gui, gui.mouseX - gui.mainPanel.getAX() + 3, gui.mouseY - gui.mainPanel.getAY() - 3, player.playerLM);
		}
		
		gui.refreshWidgets();
	}
	
	public void addMouseOverText(List<String> al)
	{
	}
	
	public void renderWidget()
	{
		int ay = getAY();
		if(ay + height < 0 || ay > gui.height) return;
		int ax = getAX();
		double z = gui.getZLevel();
		
		if(parentPanel.isEnabled() && mouseOver()) GlStateManager.color(1F, 1F, 1F, 0.4F);
		else GlStateManager.color(0.4F, 0.4F, 0.4F, 0.4F);
		GuiLM.drawBlankRect(ax, ay, z, width, height);
		
		boolean raw1 = player.playerLM.isFriendRaw(LMWorldClient.inst.clientPlayer);
		boolean raw2 = LMWorldClient.inst.clientPlayer.isFriendRaw(player.playerLM);
		
		GlStateManager.color(0F, 0F, 0F, 1F);
		if(raw1 && raw2) GlStateManager.color(0.18F, 0.74F, 0.18F, 1F);
			//else if(raw1 || raw2) GlStateManager.color(raw1 ? 0xFFE0BE00 : 0xFF00B6ED);
		else if(raw1) GlStateManager.color(0.87F, 0.74F, 0F, 1F);
		else if(raw2) GlStateManager.color(0F, 0.71F, 0.92F, 1F);
		
		GuiLM.drawBlankRect(ax + 1, ay + 1, z, 18, 18);
		
		GlStateManager.color(1F, 1F, 1F, 1F);
		GuiLM.drawPlayerHead(player.playerLM.getProfile().getName(), ax + 2, ay + 2, 16, 16, z);
		if(player.playerLM.isOnline())
		{
			GlStateManager.color(0F, 0.73F, 0F, 1F);
			GuiLM.drawBlankRect(ax + width - 9, ay, z, 8, 8);
			GlStateManager.color(0F, 1F, 0F, 1F);
			GuiLM.drawBlankRect(ax + width - 8, ay + 1, z, 6, 6);
			//GuiIcons.online.render(gui, ax + width - 17, ay, 16, 16);
		}
		
		gui.getFontRenderer().drawString(player.playerLM.getProfile().getName(), ax + 22, ay + 6, 0xFFFFFFFF);
	}
}