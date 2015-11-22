package latmod.ftbu.mod.client.gui.friends;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;

import ftb.lib.gui.GuiLM;
import ftb.lib.gui.widgets.ButtonLM;
import latmod.ftbu.net.ClientAction;
import latmod.ftbu.world.*;
import latmod.lib.FastList;
import net.minecraft.client.entity.AbstractClientPlayer;

public class ButtonPlayer extends ButtonLM
{
	public final Player player;
	public final PanelPlayerList panel;
	
	public ButtonPlayer(PanelPlayerList pl, LMPlayerClient p)
	{
		super(pl.gui, 0, pl.playerButtons.size() * 21, 34 + pl.gui.getFontRenderer().getStringWidth(p.getName()), 20);
		player = new Player(p);
		panel = pl;
	}
	
	public void onButtonPressed(int b)
	{
		if(b == 2) return;
		
		if(player != null && player.playerLM != null)
		{
			panel.gui.panelPlayerView.selectedPlayer = new Player(player.playerLM);
			panel.gui.panelPlayerView.selectedPlayer.func_152121_a(MinecraftProfileTexture.Type.SKIN, AbstractClientPlayer.getLocationSkin(player.playerLM.getName()));
			panel.gui.panelPlayerView.selectedPlayer.inventory.currentItem = 0;
			ClientAction.ACTION_REQUEST_PLAYER_INFO.send(player.playerLM.playerID);
			if(b != 0) panel.gui.panelPopupMenu = new PanelPopupPlayerActions((GuiFriends)gui, gui.mouseX - gui.mainPanel.getAX() + 3, gui.mouseY - gui.mainPanel.getAY() - 3, player.playerLM);
		}
		
		gui.refreshWidgets();
	}
	
	public void addMouseOverText(FastList<String> al)
	{
	}
	
	public void renderWidget()
	{
		int ay = getAY();
		if(ay + height < 0 || ay > gui.height) return;
		int ax = getAX();
		double z = gui.getZLevel();
		
		GuiLM.drawBlankRect(ax, ay, z, width, height, (parentPanel.isEnabled() && mouseOver()) ? 0x66FFFFFF : 0x66666666);
		
		boolean raw1 = player.playerLM.isFriendRaw(LMWorldClient.inst.getClientPlayer());
		boolean raw2 = LMWorldClient.inst.getClientPlayer().isFriendRaw(player.playerLM);
		
		int statusCol = 0xFF000000;
		if(raw1 && raw2) statusCol = 0xFF2EBD2E;
		else if(raw1 || raw2) statusCol = raw1 ? 0xFFE0BE00 : 0xFF00B6ED;
		GuiLM.drawBlankRect(ax + 1, ay + 1, z, 18, 18, statusCol);
		
		GuiLM.drawPlayerHead(player.playerLM.getName(), ax + 2, ay + 2, 16, 16, z);
		if(player.playerLM.isOnline())
		{
			GuiLM.drawBlankRect(ax + width - 9, ay, z, 8, 8, 0xFF00BB00);
			GuiLM.drawBlankRect(ax + width - 8, ay + 1, z, 6, 6, 0xFF00FF00);
			//GuiIcons.online.render(gui, ax + width - 17, ay, 16, 16);
		}
		
		gui.getFontRenderer().drawString(player.playerLM.getName(), ax + 22, ay + 6, 0xFFFFFFFF);
	}
}