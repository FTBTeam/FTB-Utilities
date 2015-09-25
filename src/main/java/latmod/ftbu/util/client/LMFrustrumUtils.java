package latmod.ftbu.util.client;

import cpw.mods.fml.relauncher.*;
import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraft.client.renderer.entity.RenderManager;

@SideOnly(Side.CLIENT)
public class LMFrustrumUtils
{
	public static boolean isFirstPerson;
	public static int currentDim;
	public static double playerX, playerY, playerZ, renderX, renderY, renderZ;
	public static final Frustrum frustrum = new Frustrum();
	
	/*
	public static final IntBuffer viewport = BufferUtils.createIntBuffer(4);
	public static final FloatBuffer modelView = BufferUtils.createFloatBuffer(16);
	public static final FloatBuffer projection = BufferUtils.createFloatBuffer(16);
	*/
	
	public static void update()
	{
		isFirstPerson = LatCoreMCClient.mc.gameSettings.thirdPersonView == 0;
		currentDim = LatCoreMCClient.getDim();
		playerX = RenderManager.instance.viewerPosX;
		playerY = RenderManager.instance.viewerPosY;
		playerZ = RenderManager.instance.viewerPosZ;
		renderX = RenderManager.renderPosX;
		renderY = RenderManager.renderPosY;
		renderZ = RenderManager.renderPosZ;
		
		frustrum.setPosition(playerX, playerY, playerZ);
	}
	
	/*
	public static void updateMatrix()
	{
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelView);
		GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection);
		GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);
	}
	
	public static Point2F getScreenCoords(float x, float y, float z)
	{
		FloatBuffer screenCoords = BufferUtils.createFloatBuffer(3);
		
		FloatBuffer screenCoords = BufferUtils.createFloatBuffer(4);
		
		boolean result = GLU.gluProject(x, y, z, modelView, projection, viewport, screenCoords);
		if(result)
		{
			float px = screenCoords.get(0);
			float py = screenCoords.get(1) - screenCoords.get(2);
			
			//if(Minecraft == 0) System.out.println(px + " : " + py);
			
			if(px >= 0 && py >= 0 && px < viewport.get(2) && py < viewport.get(3))
				return new Point2F(px, py);
		}
		return null;
	}
	
	public static Point2F getScreenCoords(VecLM v)
	{ return getScreenCoords((float)v.x, (float)v.y, (float)v.z); }
	*/
}