package latmod.ftbu.mod.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.*;
import ftb.lib.client.FTBLibClient;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.util.client.*;
import latmod.ftbu.util.client.model.CubeRenderer;
import latmod.ftbu.world.LMWorldClient;
import latmod.lib.MathHelperLM;
import latmod.lib.util.Pos2I;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class FTBURenderHandler
{
	public static final FTBURenderHandler instance = new FTBURenderHandler();
	public static final ResourceLocation world_border_tex = FTBU.mod.getLocation("textures/map/world_border.png");
	
	private static final CubeRenderer worldBorderRenderer = new CubeRenderer();
	
	@SubscribeEvent
	public void renderWorld(RenderWorldLastEvent e)
	{
		if(!LatCoreMCClient.isPlaying()) return;
		LMFrustrumUtils.update();
		//LMFrustrumUtils.updateMatrix();
		
		if(LMWorldClient.inst == null || !LMWorldClient.inst.settings.isEnabled(LMFrustrumUtils.currentDim)) return;
		int wb = LMWorldClient.inst.settings.getBorderSize(LMFrustrumUtils.currentDim);
		Pos2I borderPos = LMWorldClient.inst.settings.getBorderPos(LMFrustrumUtils.currentDim);
		
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