package latmod.ftbu.mod.client;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.*;
import ftb.lib.client.FTBLibClient;
import latmod.ftbu.api.client.callback.ClientTickCallback;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.util.client.*;
import latmod.ftbu.util.client.model.CubeRenderer;
import latmod.ftbu.world.LMWorldClient;
import latmod.lib.*;
import latmod.lib.util.Pos2I;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderWorldLastEvent;

@SideOnly(Side.CLIENT)
public class FTBURenderHandler
{
	public static final FTBURenderHandler instance = new FTBURenderHandler();
	public static final ResourceLocation world_border_tex = FTBU.mod.getLocation("textures/map/world_border.png");
	
	public static final FastList<ClientTickCallback> callbacks = new FastList<ClientTickCallback>();
	private static final CubeRenderer worldBorderRenderer = new CubeRenderer();
	
	@SubscribeEvent(priority = EventPriority.LOW)
	public void renderTick(TickEvent.RenderTickEvent e)
	{
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		
		if(e.phase == TickEvent.Phase.START)
		{
			ScaledResolution sr = new ScaledResolution(FTBLibClient.mc, FTBLibClient.mc.displayWidth, FTBLibClient.mc.displayHeight);
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
		
		if(LMWorldClient.inst == null || !LMWorldClient.inst.settings.isEnabled(LMFrustrumUtils.currentDim)) return;
		int wb = LMWorldClient.inst.settings.getSize(LMFrustrumUtils.currentDim);
		Pos2I borderPos = LMWorldClient.inst.settings.getPos(LMFrustrumUtils.currentDim);
		
		float minX = (MathHelperLM.chunk(-wb + borderPos.x) + 1) * 16 + 0.01F;
		float maxX = MathHelperLM.chunk(wb + borderPos.x) * 16 - 0.01F;
		float minZ = (MathHelperLM.chunk(-wb + borderPos.y) + 1) * 16 + 0.01F;
		float maxZ = MathHelperLM.chunk(wb + borderPos.y) * 16 - 0.01F;
		
		float rd = 32F;
		
		boolean renderWest = LMFrustrumUtils.playerX <= minX + rd;
		boolean renderEast = LMFrustrumUtils.playerX >= maxX - rd;
		boolean renderNorth = LMFrustrumUtils.playerZ <= minZ + rd;
		boolean renderSouth = LMFrustrumUtils.playerZ >= maxZ - rd;
		
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDepthMask(false);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
		//OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE, 1, 0);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		FTBLibClient.pushMaxBrightness();
		FTBLibClient.setTexture(world_border_tex);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		
		GL11.glPushMatrix();
		GL11.glTranslated(-LMFrustrumUtils.renderX, -LMFrustrumUtils.renderY, -LMFrustrumUtils.renderZ);
		
		float f = (Minecraft.getSystemTime() * 0.0005F) % 1F;
		
		worldBorderRenderer.setSize(minX, 0D, minZ, maxX, 256D, maxZ);
		worldBorderRenderer.setUV(minX + f, 0F, maxX + f, 256F);
		
		float maxA = 0.8F;
		
		GL11.glColor4f(1F, 1F, 1F, maxA);
		
		if(renderWest)
		{
			GL11.glColor4f(1F, 1F, 1F, maxA - (float)(LMFrustrumUtils.playerX - minX) * maxA / rd);
			worldBorderRenderer.renderWest();
		}
		
		if(renderEast)
		{
			GL11.glColor4f(1F, 1F, 1F, maxA - (float)(maxX - LMFrustrumUtils.playerX) * maxA / rd);
			worldBorderRenderer.renderEast();
		}
		
		if(renderNorth)
		{
			GL11.glColor4f(1F, 1F, 1F, maxA - (float)(LMFrustrumUtils.playerZ - minZ) * maxA / rd);
			worldBorderRenderer.renderNorth();
		}
		
		if(renderSouth)
		{
			GL11.glColor4f(1F, 1F, 1F, maxA - (float)(maxZ - LMFrustrumUtils.playerZ) * maxA / rd);
			worldBorderRenderer.renderSouth();
		}
		
		GL11.glPopMatrix();
		
		GL11.glShadeModel(GL11.GL_FLAT);
		FTBLibClient.popMaxBrightness();
		GL11.glDepthMask(true);
		GL11.glPopAttrib();
	}
}