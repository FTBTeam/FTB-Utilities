package latmod.ftbu.mod.client;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.*;
import latmod.core.util.*;
import latmod.ftbu.api.callback.ClientTickCallback;
import latmod.ftbu.badges.Badge;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.util.client.*;
import latmod.ftbu.util.client.model.TexturedCubeRenderer;
import latmod.ftbu.world.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.*;

@SideOnly(Side.CLIENT)
public class FTBURenderHandler
{
	public static final FTBURenderHandler instance = new FTBURenderHandler();
	public static final ResourceLocation world_border_tex = FTBU.mod.getLocation("textures/map/world_border.png");
	
	public static final FastList<ClientTickCallback> callbacks = new FastList<ClientTickCallback>();
	private static final TexturedCubeRenderer worldBorderRenderer = new TexturedCubeRenderer(true);
	
	@SubscribeEvent
	public void onPlayerRender(RenderPlayerEvent.Specials.Post e)
	{
		if(!Badge.badges.isEmpty() && FTBUClient.renderBadges.getB() && !e.entityPlayer.isInvisible())
		{
			LMPlayerClient pc = LMWorldClient.inst.getPlayer(e.entityPlayer);
			
			if(pc != null && pc.settings.renderBadge)
			{
				if(pc.cachedBadge == null)
					pc.cachedBadge = Badge.badges.get(pc.getUUID());
				if(pc.cachedBadge != null)
					pc.cachedBadge.onPlayerRender(e.entityPlayer);
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOW)
	public void renderTick(TickEvent.RenderTickEvent e)
	{
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		
		if(e.phase == TickEvent.Phase.START)
		{
			ScaledResolution sr = new ScaledResolution(LatCoreMCClient.mc, LatCoreMCClient.mc.displayWidth, LatCoreMCClient.mc.displayHeight);
			LatCoreMCClient.displayW = sr.getScaledWidth();
			LatCoreMCClient.displayH = sr.getScaledHeight();
		}
		
		if(e.phase == TickEvent.Phase.END && LatCoreMCClient.isPlaying())
			ClientNotifications.renderTemp();
		
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}
	
	@SubscribeEvent
	public void clientTick(TickEvent.ClientTickEvent e)
	{
		if(e.phase == TickEvent.Phase.END && !callbacks.isEmpty())
		{
			for(int i = 0; i < callbacks.size(); i++)
				callbacks.get(i).onCallback();
			callbacks.clear();
		}
	}
	
	@SubscribeEvent
	public void renderWorld(RenderWorldLastEvent e)
	{
		if(!LatCoreMCClient.isPlaying()) return;
		LMFrustrumUtils.update();
		//LMFrustrumUtils.updateMatrix();
		
		if(LMWorldClient.inst == null || !LMWorldClient.inst.worldBorder.enabled) return;
		
		int wb = LMWorldClient.inst.worldBorder.getSize(LMFrustrumUtils.currentDim);
		float min = (MathHelperLM.chunk(-wb) + 1) * 16 + 0.01F;
		float max = MathHelperLM.chunk(wb) * 16 - 0.01F;
		
		boolean renderWest = LMFrustrumUtils.playerX <= min + 16;
		boolean renderEast = LMFrustrumUtils.playerX >= max - 16;
		boolean renderNorth = LMFrustrumUtils.playerZ <= min + 16;
		boolean renderSouth = LMFrustrumUtils.playerZ >= max - 16;
		
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDepthMask(false);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
		//OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE, 1, 0);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		LatCoreMCClient.pushMaxBrightness();
		LatCoreMCClient.setTexture(world_border_tex);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		
		GL11.glPushMatrix();
		GL11.glTranslated(-LMFrustrumUtils.renderX, -LMFrustrumUtils.renderY, -LMFrustrumUtils.renderZ);
		
		float f = Minecraft.getSystemTime() * 0.0004F;
		
		worldBorderRenderer.setSize(min, 0D, min, max, 256D, max);
		worldBorderRenderer.setUV(-1, min + f, 0F, max + f, 256F);
		
		float maxA = 0.8F;
		
		GL11.glColor4f(1F, 1F, 1F, maxA);
		
		if(renderWest)
		{
			GL11.glColor4f(1F, 1F, 1F, maxA - (float)(LMFrustrumUtils.playerX - min) * maxA / 16F);
			worldBorderRenderer.renderWest();
		}
		
		if(renderEast)
		{
			GL11.glColor4f(1F, 1F, 1F, maxA - (float)(max - LMFrustrumUtils.playerX) * maxA / 16F);
			worldBorderRenderer.renderEast();
		}
		
		if(renderNorth)
		{
			GL11.glColor4f(1F, 1F, 1F, maxA - (float)(LMFrustrumUtils.playerZ - min) * maxA / 16F);
			worldBorderRenderer.renderNorth();
		}
		
		if(renderSouth)
		{
			GL11.glColor4f(1F, 1F, 1F, maxA - (float)(max - LMFrustrumUtils.playerZ) * maxA / 16F);
			worldBorderRenderer.renderSouth();
		}
		
		GL11.glPopMatrix();
		
		GL11.glShadeModel(GL11.GL_FLAT);
		LatCoreMCClient.popMaxBrightness();
		GL11.glDepthMask(true);
		GL11.glPopAttrib();
	}
}