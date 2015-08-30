package latmod.ftbu.mod.client.gui.friends;

import latmod.ftbu.core.gui.*;
import latmod.ftbu.core.net.*;
import latmod.ftbu.core.util.FastList;

import org.lwjgl.opengl.GL11;

public class ButtonAction extends ButtonLM
{
	public final GuiFriends gui;
	public final PlayerAction action;
	
	public ButtonAction(GuiFriends g, int y, PlayerAction a, String s)
	{
		super(g, 0, y, g.getFontRenderer().getStringWidth(s) + 5 + ((a.getIcon(g) == null) ? 0 : 9), 10);
		gui = g;
		action = a;
		title = s;
	}
	
	public void render()
	{
		int ax = getAX();
		int ay = getAY();
		
		TextureCoords icon = action.getIcon(gui);
		int x = 2;
		if(icon != null) x += 9;
		
		icon.render(gui, ax + 1D, ay + 2D, 8D, 8D);
		
		if(mouseOver())
		{
			GL11.glColor4f(1F, 1F, 1F, 1F);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GuiLM.drawRect(ax, ay, ax + width, ay + height, 0x66FFFFFF);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glColor4f(1F, 1F, 1F, 1F);
			GL11.glEnable(GL11.GL_BLEND);
		}
		
		gui.setTexture(null);
		gui.getFontRenderer().drawString(title, ax + x, ay + 2, 0xFFFFFFFF);
	}
	
	public void onButtonPressed(int b)
	{
		if(b == 0)
		{
			gui.playClickSound();
			action.onClicked((GuiFriends)gui);
			LMNetHelper.sendToServer(new MessageLMPlayerRequestInfo(GuiFriends.selectedPlayer.playerLM.playerID));
		}
	}
	
	public void addMouseOverText(FastList<String> l)
	{
		//super.addMouseOverText(l);
		action.addMouseOverText(l);
	}
}