package latmod.ftbu.mod.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.*;
import ftb.lib.client.*;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.util.client.*;
import latmod.ftbu.util.client.model.CubeRenderer;
import latmod.ftbu.world.*;
import latmod.lib.MathHelperLM;
import net.minecraft.client.Minecraft;
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
		
		if(!LMWorldClient.inst.settings.border_enabled.get()) return;
		
		WorldBorder wb = LMWorldClient.inst.settings.getWB(LMFrustrumUtils.currentDim);
		int s = wb.getSize();
		if(s <= 0) return;
		
		double minX = (MathHelperLM.chunk(-s + wb.pos.x) + 1D) * 16D + 0.01D;
		double maxX = MathHelperLM.chunk(s + wb.pos.x) * 16D - 0.01D;
		double minZ = (MathHelperLM.chunk(-s + wb.pos.y) + 1D) * 16D + 0.01D;
		double maxZ = MathHelperLM.chunk(s + wb.pos.y) * 16D - 0.01D;
		
		double rd = 32D;
		
		boolean renderWest = LMFrustrumUtils.playerX <= minX + rd;
		boolean renderEast = LMFrustrumUtils.playerX >= maxX - rd;
		boolean renderNorth = LMFrustrumUtils.playerZ <= minZ + rd;
		boolean renderSouth = LMFrustrumUtils.playerZ >= maxZ - rd;
		
		GlStateManager.pushAttrib();
		GlStateManager.enableBlend();
		GlStateManager.disableLighting();
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		GlStateManager.disableCull();
		GlStateManager.depthMask(false);
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableTexture();
		FTBLibClient.pushMaxBrightness();
		FTBLibClient.setTexture(world_border_tex);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(-LMFrustrumUtils.renderX, -LMFrustrumUtils.renderY, -LMFrustrumUtils.renderZ);
		
		double f = (Minecraft.getSystemTime() * 0.0005D) % 1D;
		
		worldBorderRenderer.setSize(minX, 0D, minZ, maxX, 256D, maxZ);
		worldBorderRenderer.setUVD(minX + f, 0D, maxX + f, 256D);
		
		float maxA = 0.8F;
		
		GlStateManager.color(1F, 1F, 1F, maxA);
		
		if(renderWest)
		{
			GlStateManager.color(1F, 1F, 1F, maxA - (float) ((LMFrustrumUtils.playerX - minX) * maxA / rd));
			worldBorderRenderer.renderWest();
		}
		
		if(renderEast)
		{
			GlStateManager.color(1F, 1F, 1F, maxA - (float) ((maxX - LMFrustrumUtils.playerX) * maxA / rd));
			worldBorderRenderer.renderEast();
		}
		
		if(renderNorth)
		{
			GlStateManager.color(1F, 1F, 1F, maxA - (float) ((LMFrustrumUtils.playerZ - minZ) * maxA / rd));
			worldBorderRenderer.renderNorth();
		}
		
		if(renderSouth)
		{
			GlStateManager.color(1F, 1F, 1F, maxA - (float) ((maxZ - LMFrustrumUtils.playerZ) * maxA / rd));
			worldBorderRenderer.renderSouth();
		}
		
		GlStateManager.popMatrix();
		GlStateManager.shadeModel(GL11.GL_FLAT);
		FTBLibClient.popMaxBrightness();
		GlStateManager.depthMask(true);
		GlStateManager.popAttrib();
	}
}