package latmod.ftbu.mod.client.gui.friends;

import latmod.ftbu.core.gui.*;
import latmod.ftbu.core.net.*;
import latmod.ftbu.core.util.*;
import latmod.ftbu.mod.player.ClientNotifications;

import org.lwjgl.opengl.GL11;

public class ButtonAction extends ButtonLM
{
	public final GuiFriends gui;
	public final PlayerAction action;
	
	public ButtonAction(GuiFriends g, PlayerAction a, String s)
	{
		super(g, 3 + GuiFriends.actionButtons.size() * 18, -17, 16, 16);
		gui = g;
		action = a;
		title = s;
	}
	
	public boolean isEnabled()
	{ return !GuiFriends.notificationsGuiOpen; }
	
	public void render()
	{
		render(action.getIcon(gui));
		
		if(action == PlayerAction.notifications && !ClientNotifications.perm.isEmpty())
		{
			String n = String.valueOf(ClientNotifications.perm.size());
			int nw = gui.mc.fontRenderer.getStringWidth(n);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			LMColorUtils.setGLColor(0xAAFF2222);
			GuiLM.drawTexturedRectD(gui.getPosX(posX + width - nw), gui.getPosY(posY - 2), gui.getZLevel(), nw + 1, 9, 0D, 0D, 0D, 0D);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glColor4f(1F, 1F, 1F, 1F);
			gui.setTexture(null);
			gui.mc.fontRenderer.drawString(n, gui.getPosX(posX + width - nw + 1), gui.getPosY(posY - 1), 0xFFFFFFFF);
		}
	}
	
	public void onButtonPressed(int b)
	{
		gui.playClickSound();
		action.onClicked((GuiFriends)gui);
		LMNetHelper.sendToServer(new MessageLMPlayerRequestInfo(GuiFriends.selectedPlayer.playerLM.playerID));
	}
	
	public void addMouseOverText(FastList<String> l)
	{
		super.addMouseOverText(l);
		action.addMouseOverText(l);
	}
}