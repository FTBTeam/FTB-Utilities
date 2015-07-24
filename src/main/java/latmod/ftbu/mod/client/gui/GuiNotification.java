package latmod.ftbu.mod.client.gui;

import latmod.ftbu.core.Notification;
import latmod.ftbu.core.util.LatCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;

import org.lwjgl.opengl.*;

import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class GuiNotification extends Gui
{
	public final Notification notification;
	
	private RenderItem renderItem = new RenderItem();
	private long time;

	public GuiNotification(Notification n)
	{
		notification = n;
		time = -1L;
	}
	
	public void render(Minecraft mc)
	{
		if(time == -1L) time = Minecraft.getSystemTime();
		
		if (time > 0L && mc.thePlayer != null)
		{
			double d0 = (double)(Minecraft.getSystemTime() - time) / (double)notification.timer;
			
			if (d0 < 0D || d0 > 1D) { time = 0L; return; }
			
			GL11.glViewport(0, 0, mc.displayWidth, mc.displayHeight);
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glLoadIdentity();
			ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
			int displayW = sr.getScaledWidth();
			int displayH = sr.getScaledHeight();
			GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glOrtho(0D, displayW, displayH, 0D, 1000D, 3000D);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glLoadIdentity();
			GL11.glTranslatef(0F, 0F, -2000F);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDepthMask(false);
			double d1 = d0 * 2D;

			if (d1 > 1D) d1 = 2D - d1;
			d1 *= 4D;
			d1 = 1D - d1;

			if (d1 < 0D) d1 = 0D;
			
			d1 *= d1;
			d1 *= d1;
			
			int width = 40 + Math.max(mc.fontRenderer.getStringWidth(notification.title), mc.fontRenderer.getStringWidth(notification.desc));
			if(notification.item == null) width -= 20;
			
			int i = displayW - width;
			int j = 0 - (int)(d1 * 36D);
			GL11.glColor4f(1F, 1F, 1F, 1F);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_LIGHTING);
			drawRect(i, j, displayW, j + 32, LatCore.Colors.getRGBA(notification.color, 140));
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			
			int w = notification.item == null ? 10 : 30;
			
			if(notification.desc.isEmpty())
			{
				mc.fontRenderer.drawString(notification.title, i + w, j + 12, -256);
			}
			else
			{
				mc.fontRenderer.drawString(notification.title, i + w, j + 7, -256);
				mc.fontRenderer.drawString(notification.desc, i + w, j + 18, -1);
			}
			
			if(notification.item == null) return;
			
			RenderHelper.enableGUIStandardItemLighting();
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glEnable(GL11.GL_COLOR_MATERIAL);
			GL11.glEnable(GL11.GL_LIGHTING);
			renderItem.renderItemIntoGUI(mc.fontRenderer, mc.getTextureManager(), notification.item, i + 8, j + 8, false);
			renderItem.renderItemOverlayIntoGUI(mc.fontRenderer, mc.getTextureManager(), notification.item, i + 8, j + 8);
			//renderItem.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), notification.item, i + 8, j + 8);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDepthMask(true);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
		}
	}

	public boolean isDead()
	{ return time == 0L; }
}