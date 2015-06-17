package latmod.ftbu.mod.client;
import java.util.*;

import latmod.ftbu.core.client.LatCoreMCClient;
import latmod.ftbu.core.client.badges.Badge;
import latmod.ftbu.core.client.model.CubeRenderer;
import latmod.ftbu.core.util.*;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.client.Waypoints.Waypoint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
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
	private static final FastList<WaypointClient> visibleWaypoints = new FastList<WaypointClient>();
	public static final ResourceLocation texMarker = FTBU.mod.getLocation("textures/map/marker.png");
	public static final ResourceLocation texBeacon = FTBU.mod.getLocation("textures/map/beacon.png");
	private static double playerX, playerY, playerZ, renderX, renderY, renderZ, far = 4D;
	private static final FastList<String> stringList = new FastList<String>();
	
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
		if(Waypoints.waypoints.isEmpty() || !Waypoints.enabled.getB() || Minecraft.getMinecraft().theWorld == null) return;
		visibleWaypoints.clear();
		
		Minecraft mc = LatCoreMCClient.getMinecraft();
		
		boolean isMarker = Waypoints.waypointType.getI() == 0;
		if(mc.thePlayer == null || mc.theWorld == null || (isMarker && mc.gameSettings.thirdPersonView != 0)) return;
		
		Tessellator t = Tessellator.instance;
		
		int currentDim = mc.theWorld.provider.dimensionId;
		//double renderDistSq = Math.min(Waypoints.renderDistanceSq[Waypoints.renderDistance.getI()], MathHelperLM.sq(mc.gameSettings.renderDistanceChunks * 20D));
		double renderDistSq = Waypoints.renderDistanceSq[Waypoints.renderDistance.getI()];
		
		playerX = RenderManager.instance.viewerPosX;
		playerY = RenderManager.instance.viewerPosY;
		playerZ = RenderManager.instance.viewerPosZ;
		
		renderX = RenderManager.renderPosX;
		renderY = RenderManager.renderPosY;
		renderZ = RenderManager.renderPosZ;
		
		for(int i = 0; i < Waypoints.waypoints.size(); i++)
		{
			Waypoint w = Waypoints.waypoints.get(i);
			
			if(w.enabled && w.dim == currentDim)
			{
				double x = w.posX + 0.5D;
				double y = w.posY + 0.5D;
				double z = w.posZ + 0.5D;
				double distSq = MathHelperLM.distSq(x, y, z, playerX, playerY, playerZ);
				
				if(distSq <= renderDistSq)
				{
					visibleWaypoints.add(new WaypointClient(w, x, y, z, distSq));
				}
			}
		}
		
		if(visibleWaypoints.isEmpty()) return;
		
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		visibleWaypoints.sort(WaypointComparator.instance);
		
		if(isMarker)
		{
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDepthMask(false);
			OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
			mc.getTextureManager().bindTexture(texMarker);
			
			for(int i = 0; i < visibleWaypoints.size(); i++)
			{
				WaypointClient w = visibleWaypoints.get(i);
				
				GL11.glPushMatrix();
				GL11.glTranslated(w.closeRenderX, w.closeRenderY, w.closeRenderZ);
				GL11.glRotatef(-RenderManager.instance.playerViewY, 0F, 1F, 0F);
				GL11.glRotatef(RenderManager.instance.playerViewX, 1F, 0F, 0F);
				GL11.glScaled(w.scale, -w.scale, -w.scale);
				
				t.startDrawingQuads();
				t.setColorRGBA(w.colR, w.colG, w.colB, 255);
				t.addVertexWithUV(-0.5D, -0.5D, 0D, 0D, 0D);
				t.addVertexWithUV(0.5D, -0.5D, 0D, 1D, 0D);
				t.addVertexWithUV(0.5D, 0.5D, 0D, 1D, 1D);
				t.addVertexWithUV(-0.5D, 0.5D, 0D, 0D, 1D);
				t.draw();
				
				GL11.glPopMatrix();
			}
			
			GL11.glDepthMask(true);
		}
		else
		{
			mc.getTextureManager().bindTexture(texBeacon);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glDepthMask(false);
			OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
			//OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE, 1, 0);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			
			for(int i = 0; i < visibleWaypoints.size(); i++)
			{
				WaypointClient w = visibleWaypoints.get(i);
				
				GL11.glPushMatrix();
				GL11.glTranslated(w.posX - renderX, w.posY - renderY, w.posZ - renderZ);
				
				float d = 0.4F;
				GL11.glColor4f(w.colR / 255F, w.colG / 255F, w.colB / 255F, 0.15F);
				CubeRenderer.instance.setSize(-d, -w.posY, -d, d, 256D - w.posY, d);
				CubeRenderer.instance.renderAll();
				d = 0.3F;
				CubeRenderer.instance.setSize(-d, -w.posY, -d, d, 256D - w.posY, d);
				CubeRenderer.instance.renderAll();
				GL11.glPopMatrix();
			}
			
			GL11.glDepthMask(true);
		}
		
		boolean displayTitle = Waypoints.displayTitle.getB();
		boolean displayDist = Waypoints.displayDist.getB();
		
		if(mc.gameSettings.thirdPersonView == 0 && (displayTitle || displayDist))
		{
			OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE, 1, 0);
			GL11.glColor4f(1F, 1F, 1F, 1F);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDepthMask(false);
			
			OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			
			for(int i = 0; i < visibleWaypoints.size(); i++)
			{
				WaypointClient w = visibleWaypoints.get(i);
				
				if(displayDist || !w.name.isEmpty())
				{
					stringList.clear();
					if(displayTitle && !w.name.isEmpty()) stringList.add(w.name);
					if(displayDist) stringList.add((int)(w.distance + 0.5D) + "m");
					
					if(stringList.isEmpty()) continue;
					
					GL11.glPushMatrix();
					GL11.glTranslated(w.closeRenderX, w.closeRenderY + 0.5D, w.closeRenderZ);
					GL11.glRotatef(-RenderManager.instance.playerViewY, 0F, 1F, 0F);
					GL11.glRotatef(RenderManager.instance.playerViewX, 1F, 0F, 0F);
					GL11.glNormal3f(0F, 1F, 0F);
					float f1 = 0.02F;
					GL11.glScalef(-f1, -f1, f1);
					
					for(int j = 0; j < stringList.size(); j++)
					{
						int y = -5 + 11 * j;
						String s = stringList.get(j);
						int l = mc.fontRenderer.getStringWidth(s) / 2;
						GL11.glDisable(GL11.GL_TEXTURE_2D);
						t.startDrawingQuads();
						t.setColorRGBA_F(0F, 0F, 0F, 0.4F);
						t.addVertex(-l -1, y, 0D);
						t.addVertex(l + 1, y, 0D);
						t.addVertex(l + 1, y + 10, 0D);
						t.addVertex(-l -1, y + 10, 0D);
						t.draw();
						GL11.glEnable(GL11.GL_TEXTURE_2D);
						mc.fontRenderer.drawString(s, -l, y + 1, 0xFFFFFFFF);
					}
					
					GL11.glPopMatrix();
				}
			}
			
			GL11.glDepthMask(true);
		}
		
		GL11.glPopAttrib();
	}
	
	public static class WaypointClient
	{
		public final String name;
		public final double posX, posY, posZ;
		public final double closeRenderX, closeRenderY, closeRenderZ;
		public final int colR, colG, colB;
		public final double distance, scale;
		
		public WaypointClient(Waypoint w, double x, double y, double z, double dsq)
		{
			name = w.name;
			colR = w.colR;
			colG = w.colG;
			colB = w.colB;
			posX = x;
			posY = y;
			posZ = z;
			
			distance = MathHelperLM.sqrt(dsq);
			
			double crX = posX - renderX;
			double crY = posY - renderY;
			double crZ = posZ - renderZ;
			
			double d1 = MathHelperLM.sqrt3sq(crX, crY, crZ);
			
			if(d1 > far)
			{
				double d = far / d1;
				crX *= d;
				crY *= d;
				crZ *= d;
				d1 = far;
			}
			
			closeRenderX = crX;
			closeRenderY = crY;
			closeRenderZ = crZ;
			
			scale = (d1 * 0.1D + 1D) * 0.4D;
		}
	}
	
	private static class WaypointComparator implements Comparator<WaypointClient>
	{
		public static final WaypointComparator instance = new WaypointComparator();
		
		public int compare(WaypointClient o1, WaypointClient o2)
		{ return (o1.distance < o2.distance) ? 1 : -1; }
	}
}