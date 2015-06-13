package latmod.ftbu.mod.client;
import java.util.UUID;

import latmod.ftbu.core.client.LatCoreMCClient;
import latmod.ftbu.core.client.badges.Badge;
import latmod.ftbu.core.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.client.event.*;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class FTBURenderHandler
{
	public static final FTBURenderHandler instance = new FTBURenderHandler();
	public static final FastList<GuiNotification> messages = new FastList<GuiNotification>();
	public static final FastMap<UUID, Badge> playerBadges = new FastMap<UUID, Badge>();
	
	@SubscribeEvent
	public void onPlayerRender(RenderPlayerEvent.Specials.Post e)
	{
		if(!Badge.reloading && FTBUClient.enablePlayerDecorators.getB() && !e.entityPlayer.isInvisible())
		{
			Badge b = playerBadges.get(e.entityPlayer.getUniqueID());
			if(b != null) b.onPlayerRender(e.entityPlayer);
		}
	}
	
	@SubscribeEvent
	public void renderTick(TickEvent.RenderTickEvent event)
	{
		Minecraft mc = Minecraft.getMinecraft();
		
		if(mc.theWorld != null && event.phase == TickEvent.Phase.END)
		{
			if(!messages.isEmpty())
			{
				GuiNotification m = messages.get(0);
				m.render(mc);
				if(m.isDead()) messages.remove(0);
			}
		}
	}
	
	@SubscribeEvent
	public void renderWorld(RenderWorldLastEvent e)
	{
		if(Waypoints.waypoints.isEmpty() || !Waypoints.enabled.getB()) return;
		
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		GL11.glDisable(GL11.GL_LIGHTING);
		
		for(Waypoints.Waypoint w : Waypoints.waypoints)
		{
			if(w.dim != RenderManager.instance.livingPlayer.dimension) continue;
			
			double rx = w.posX - RenderManager.renderPosX;
			double ry = w.posY - RenderManager.renderPosY;
			double rz = w.posZ - RenderManager.renderPosZ;
			
			double dist = rx * rx + ry * ry + rz * rz;
			
			if(dist > 2500D * 2500D) continue;
			
			double mdist = 16D;
			
			if(dist >= mdist * mdist)
			{
				double m = Math.max(Math.max(Math.abs(rx), Math.abs(rz)), Math.abs(ry));
				rx = rx * mdist / m;
				ry = ry * mdist / m;
				rz = rz * mdist / m;
			}
			
			GL11.glPushMatrix();
			GL11.glTranslated(rx, ry, rz);
			GL11.glRotatef(-RenderManager.instance.playerViewY, 0F, 1F, 0F);
			GL11.glRotatef(RenderManager.instance.playerViewX, 1F, 0F, 0F);
			GL11.glScalef(1F, -1F, -1F);
			
			GL11.glColor4f(w.colR / 255F, w.colG / 255F, w.colB / 255F, 0.2F);
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex3f(0F, -0.5F, 0F);
			GL11.glVertex3f(0.5F, 0F, 0F);
			GL11.glVertex3f(0F, 0.5F, 0F);
			GL11.glVertex3f(-0.5F, 0F, 0F);
			GL11.glEnd();
			
			GL11.glColor4f(w.colR / 255F, w.colG / 255F, w.colB / 255F, 0.5F);
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex3f(0F, -0.2F, 0F);
			GL11.glVertex3f(0.2F, 0F, 0F);
			GL11.glVertex3f(0F, 0.2F, 0F);
			GL11.glVertex3f(-0.2F, 0F, 0F);
			GL11.glEnd();
			
			if(Waypoints.displayTitle.getB() && !w.name.isEmpty())
			{
				Minecraft mc = LatCoreMCClient.getMinecraft();
				GL11.glPushMatrix();
				GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
				GL11.glNormal3f(0F, 1F, 0F);
				OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
				//GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glDisable(GL11.GL_CULL_FACE);
				GL11.glDisable(GL11.GL_LIGHTING);
				float f1 = 0.02F;
				GL11.glScalef(-f1, f1, f1);
				
				GL11.glColor4f(1F, 1F, 1F, 1F);
				mc.fontRenderer.drawString(w.name, -(mc.fontRenderer.getStringWidth(w.name) / 2), -4, 0xFFFFFFFF);
				GL11.glPopAttrib();
				GL11.glPopMatrix();
			}
			
			GL11.glPopMatrix();
		}
		
		GL11.glDepthMask(true);
		GL11.glPopAttrib();
	}
}