package latmod.ftbu.util.client.model;

import cpw.mods.fml.relauncher.*;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.*;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class CubeRenderer
{
	public static final CubeRenderer instance = new CubeRenderer();
	
	protected static final float[] normalsX = new float[] { 0F, 0F, 0F, 0F, -1F, 1F };
	protected static final float[] normalsY = new float[] { -1F, 1F, 0F, 0F, 0F, 0F };
	protected static final float[] normalsZ = new float[] { 0F, 0F, -1F, 1F, 0F, 0F };
	
	public Tessellator tessellator = null;
	
	public boolean hasTexture = true;
	public boolean hasNormals = true;
	
	/** Unimplemented */
	public boolean isInterpolated = false;
	
	protected int currentSide = -1;
	protected double minX, minY, minZ, maxX, maxY, maxZ;
	protected float minU, minV, maxU, maxV;
	
	public void setSize(double x0, double y0, double z0, double x1, double y1, double z1)
	{ minX = x0; minY = y0; minZ = z0; maxX = x1; maxY = y1; maxZ = z1; }
	
	public void setSize(AxisAlignedBB aabb)
	{ setSize(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ); }
	
	public void setSize(Block b)
	{ setSize(b.getBlockBoundsMinX(), b.getBlockBoundsMinY(), b.getBlockBoundsMinZ(), b.getBlockBoundsMaxX(), b.getBlockBoundsMaxY(), b.getBlockBoundsMaxZ()); }
	
	public void setUV(float u0, float v0, float u1, float v1)
	{ minU = u0; minV = v0; maxU = u1; maxV = v1; }
	
	public void setUVD(double minU, double minV, double maxU, double maxV)
	{ setUV((float)minU, (float)minV, (float)maxU, (float)maxV); }
	
	public void setUVFromIcon(IIcon icon)
	{ setUV(icon.getMinU(), icon.getMinV(), icon.getMaxU(), icon.getMaxV()); }
	
	public void setUVFromBlock(Block b, int m, int s)
	{ setUVFromIcon(b.getIcon(s, m)); }
	
	public void renderAll()
	{
		renderDown();
		renderUp();
		renderSouth();
		renderNorth();
		renderWest();
		renderEast();
	}
	
	public void renderSide(int s)
	{
		if(s < 0 || s > 5) return;
		else if(s == 0) renderDown();
		else if(s == 1) renderUp();
		else if(s == 2) renderSouth();
		else if(s == 3) renderNorth();
		else if(s == 4) renderWest();
		else if(s == 5) renderEast();
	}
	
	protected void begin(int i)
	{
		if(i < 0 || i > 5)
		{ currentSide = -1; return; }
		
		currentSide = i;
		if(hasNormals)
		{
			if(tessellator == null) GL11.glNormal3f(normalsX[i], normalsY[i], normalsZ[i]);
			else tessellator.setNormal(normalsX[i], normalsY[i], normalsZ[i]);
		}
		
		if(tessellator == null) GL11.glBegin(GL11.GL_QUADS);
		else tessellator.startDrawingQuads();
	}
	
	protected void end()
	{
		if(currentSide == -1) return;
		if(tessellator == null) GL11.glEnd();
		else tessellator.draw();
		currentSide = -1;
	}
	
	protected void vertex(double x, double y, double z, float u, float v)
	{
		if(currentSide == -1) return;
		
		if(tessellator == null)
		{
			if(hasTexture) GL11.glTexCoord2d(u, v);
			GL11.glVertex3d(x, y, z);
		}
		else
		{
			if(hasTexture) tessellator.setTextureUV(u, v);
			tessellator.addVertex(x, y, z);
		}
	}
	
	public void renderDown()
	{
		begin(0);
		vertex(minX, minY, minZ, minU, minV);
		vertex(maxX, minY, minZ, maxU, minV);
		vertex(maxX, minY, maxZ, maxU, maxV);
		vertex(minX, minY, maxZ, minU, maxV);
		end();
	}
	
	public void renderUp()
	{
		begin(1);
		vertex(minX, maxY, minZ, minU, minV);
		vertex(minX, maxY, maxZ, minU, maxV);
		vertex(maxX, maxY, maxZ, maxU, maxV);
		vertex(maxX, maxY, minZ, maxU, minV);
		end();
	}
	
	public void renderSouth()
	{
		begin(2);
		vertex(minX, minY, maxZ, minU, maxV);
		vertex(maxX, minY, maxZ, maxU, maxV);
		vertex(maxX, maxY, maxZ, maxU, minV);
		vertex(minX, maxY, maxZ, minU, minV);
		end();
	}
	
	public void renderNorth()
	{
		begin(3);
		vertex(minX, minY, minZ, maxU, maxV);
		vertex(minX, maxY, minZ, maxU, minV);
		vertex(maxX, maxY, minZ, minU, minV);
		vertex(maxX, minY, minZ, minU, maxV);
		end();
	}
	
	public void renderWest()
	{
		begin(4);
		vertex(minX, minY, minZ, minU, maxV);
		vertex(minX, minY, maxZ, maxU, maxV);
		vertex(minX, maxY, maxZ, maxU, minV);
		vertex(minX, maxY, minZ, minU, minV);
		end();
	}
	
	public void renderEast()
	{
		begin(5);
		vertex(maxX, minY, minZ, maxU, maxV);
		vertex(maxX, maxY, minZ, maxU, minV);
		vertex(maxX, maxY, maxZ, minU, minV);
		vertex(maxX, minY, maxZ, minU, maxV);
		end();
	}
}