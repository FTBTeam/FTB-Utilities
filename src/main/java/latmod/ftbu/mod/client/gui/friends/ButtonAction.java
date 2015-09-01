package latmod.ftbu.mod.client.gui.friends;

import latmod.ftbu.core.gui.*;
import latmod.ftbu.core.net.*;
import latmod.ftbu.core.util.FastList;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class ButtonAction extends ButtonLM
{
	public final GuiFriends gui;
	public final PlayerAction action;
	
	public ButtonAction(GuiFriends g, int y, PlayerAction a, String s)
	{
		super(g, 0, y, g.getFontRenderer().getStringWidth(s) + 5 + ((a.getIcon(g) == null) ? 0 : 17), 18);
		gui = g;
		action = a;
		title = s;
	}
	
	public void renderWidget()
	{
		int ax = getAX();
		int ay = getAY();
		
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GuiLM.drawRect(ax - 1, ay, ax + width + 1, ay + height, 0xFF666666);
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		GuiLM.drawRect(ax - 1, ay, ax + width + 1, ay + height, 0x66000000);
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		
		GL11.glColor4f(0.2F, 0.2F, 0.2F, 1F);
		
		GL11.glVertex2f(ax - 1, ay + height);
		GL11.glVertex2f(ax + 1 + width, ay + height);
		
		GL11.glVertex2f(ax + 1 + width, ay);
		GL11.glVertex2f(ax + 1 + width, ay + height);
		
		GL11.glVertex2f(ax - 1, ay + height);
		GL11.glVertex2f(ax - 1, ay);
		
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		GL11.glEnable(GL11.GL_BLEND);
		
		TextureCoords icon = action.getIcon(gui);
		int x = 2;
		if(icon != null) x += 17;
		
		icon.render(gui, ax, ay + 1D, 16D, 16D);
		
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
		gui.getFontRenderer().drawString(title, ax + x, ay + 5, 0xFFFFFFFF);
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