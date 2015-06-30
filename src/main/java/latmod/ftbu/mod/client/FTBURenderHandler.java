package latmod.ftbu.mod.client;
import java.util.*;

import latmod.ftbu.core.client.LatCoreMCClient;
import latmod.ftbu.core.client.badges.Badge;
import latmod.ftbu.core.client.model.*;
import latmod.ftbu.core.util.*;
import latmod.ftbu.mod.*;
import latmod.ftbu.mod.client.gui.GuiNotification;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityBeaconRenderer;
import net.minecraft.util.*;
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
	private static final FastList<WaypointClient> visibleBeacons = new FastList<WaypointClient>();
	private static final FastList<WaypointClient> visibleMarkers = new FastList<WaypointClient>();
	public static final ResourceLocation texMarker = FTBU.mod.getLocation("textures/map/marker.png");
	private static double playerX, playerY, playerZ, renderX, renderY, renderZ, far = 4D;
	private static final FastList<String> stringList = new FastList<String>();
	public static final TexturedCubeRenderer worldBorderRenderer = new TexturedCubeRenderer(true);
	private static final CubeRenderer beaconRenderer = new CubeRenderer();
	public static final ResourceLocation world_border_tex = FTBU.mod.getLocation("textures/map/world_border.png");
	private static Minecraft mc;
	private static boolean isFPS;
	private static int currentDim;
	
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
		mc = LatCoreMCClient.getMinecraft();
		if(mc.thePlayer == null || mc.theWorld == null) return;
		
		isFPS = mc.gameSettings.thirdPersonView == 0;
		
		currentDim = mc.theWorld.provider.dimensionId;
		
		playerX = RenderManager.instance.viewerPosX;
		playerY = RenderManager.instance.viewerPosY;
		playerZ = RenderManager.instance.viewerPosZ;
		
		renderX = RenderManager.renderPosX;
		renderY = RenderManager.renderPosY;
		renderZ = RenderManager.renderPosZ;
		
		renderWaypoints();
		renderWorldBorder();
	}
	
	private void renderWaypoints()
	{
		if(!Waypoints.enabled.getB() || Minecraft.getMinecraft().theWorld == null) return;
		FastList<Waypoint> list = Waypoints.getAll();
		if(list.isEmpty()) return;
		
		visibleBeacons.clear();
		visibleMarkers.clear();
		
		double renderDistSq = Waypoints.renderDistanceSq[Waypoints.renderDistance.getI()];
		
		for(int i = 0; i < list.size(); i++)
		{
			Waypoint w = list.get(i);
			
			if(w.enabled && w.dim == currentDim)
			{
				double x = w.posX + 0.5D;
				double y = w.posY + 0.5D;
				double z = w.posZ + 0.5D;
				double distSq = MathHelperLM.distSq(x, y, z, playerX, playerY, playerZ);
				
				if(distSq <= renderDistSq)
				{
					if(w.isMarker) visibleMarkers.add(new WaypointClient(w, x, y, z, distSq));
					else visibleBeacons.add(new WaypointClient(w, x, y, z, distSq));
				}
			}
		}
		
		boolean hasMarkers = !visibleMarkers.isEmpty();
		boolean hasBeacons = !visibleBeacons.isEmpty();
		
		if(!hasMarkers && !hasBeacons) return;
		
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		visibleMarkers.sort(WaypointComparator.instance);
		visibleBeacons.sort(WaypointComparator.instance);
		
		Tessellator t = Tessellator.instance;
		
		if(hasMarkers && isFPS)
		{
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDepthMask(false);
			OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
			mc.getTextureManager().bindTexture(texMarker);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			
			for(int i = 0; i < visibleMarkers.size(); i++)
			{
				WaypointClient w = visibleMarkers.get(i);
				
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
			GL11.glEnable(GL11.GL_DEPTH_TEST);
		}
		
		if(hasBeacons)
		{
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glDepthMask(false);
			OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
			//OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE, 1, 0);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			
			for(int i = 0; i < visibleBeacons.size(); i++)
			{
				WaypointClient w = visibleBeacons.get(i);
				
				GL11.glPushMatrix();
				GL11.glTranslated(w.posX - renderX, w.posY - renderY, w.posZ - renderZ);
				
				float d = 0.4F;
				GL11.glColor4f(w.colR / 255F, w.colG / 255F, w.colB / 255F, 0.15F);
				beaconRenderer.setSize(-d, -w.posY, -d, d, 256D - w.posY, d);
				for(int k = 2; k < 6; k++)
					beaconRenderer.renderSide(k);
				d = 0.3F;
				beaconRenderer.setSize(-d, -w.posY, -d, d, 256D - w.posY, d);
				for(int k = 2; k < 6; k++)
					beaconRenderer.renderSide(k);
				GL11.glPopMatrix();
			}
			
			GL11.glDepthMask(true);
		}
		
		boolean displayTitle = Waypoints.displayTitle.getB();
		boolean displayDist = Waypoints.displayDist.getB();
		
		if(isFPS && (displayTitle || displayDist))
		{
			GL11.glColor4f(1F, 1F, 1F, 1F);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDepthMask(false);
			OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
			
			for(int i = 0; i < visibleBeacons.size() + visibleMarkers.size(); i++)
			{
				WaypointClient w = (i >= visibleBeacons.size()) ? visibleMarkers.get(i - visibleBeacons.size()) : visibleBeacons.get(i);
				
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
	
	private void renderWorldBorder()
	{
		mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
		
		int currentDim = mc.theWorld.provider.dimensionId;
		
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDepthMask(false);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
		//OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE, 1, 0);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		LatCoreMCClient.pushMaxBrightness();
		mc.getTextureManager().bindTexture(world_border_tex);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		
		GL11.glPushMatrix();
		GL11.glTranslated(-renderX, -renderY, -renderZ);
		
		int wb = FTBUConfig.WorldBorder.inst.getWorldBorder(currentDim);
		float min = (MathHelperLM.chunk(-wb) + 1) * 16 + 0.01F;
		float max = MathHelperLM.chunk(wb) * 16 - 0.01F;
		float f = Minecraft.getSystemTime() * 0.0004F;
		
		worldBorderRenderer.setSize(min, 0D, min, max, 256D, max);
		worldBorderRenderer.setUV(-1, min + f, 0F, max + f, 256F);
		
		float maxA = 0.8F;
		
		GL11.glColor4f(1F, 1F, 1F, maxA);
		
		if(playerX <= min + 16)
		{
			GL11.glColor4f(1F, 1F, 1F, maxA - (float)(playerX - min) * maxA / 16F);
			worldBorderRenderer.renderWest();
		}
		
		if(playerX >= max - 16)
		{
			GL11.glColor4f(1F, 1F, 1F, maxA - (float)(max - playerX) * maxA / 16F);
			worldBorderRenderer.renderEast();
		}
		
		if(playerZ <= min + 16)
		{
			GL11.glColor4f(1F, 1F, 1F, maxA - (float)(playerZ - min) * maxA / 16F);
			worldBorderRenderer.renderNorth();
		}
		
		if(playerZ >= max - 16)
		{
			GL11.glColor4f(1F, 1F, 1F, maxA - (float)(max - playerZ) * maxA / 16F);
			worldBorderRenderer.renderSouth();
		}
		
		GL11.glPopMatrix();
		
		LatCoreMCClient.popMaxBrightness();
		GL11.glDepthMask(true);
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