package latmod.core.client.model;

import static org.lwjgl.opengl.GL11.*;
import net.minecraft.block.Block;
import net.minecraft.util.*;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class CubeRenderer
{
	public static final int TEX_DISABLED = 0;
	public static final int TEX_SCALED = 1;
	public static final int TEX_NOT_SCALED = 2;
	
	public final int textureType;
	public double minX = 0D;
	public double minY = 0D;
	public double minZ = 0D;
	public double maxX = 1D;
	public double maxY = 1D;
	public double maxZ = 1D;
	public float minU = 0F;
	public float minV = 0F;
	public float maxU = 1F;
	public float maxV = 1F;
	
	public CubeRenderer(int i)
	{ textureType = i; }
	
	public void setSize(double x0, double y0, double z0, double x1, double y1, double z1)
	{ minX = x0; minY = y0; minZ = z0; maxX = x1; maxY = y1; maxZ = z1; }
	
	public void setSize(AxisAlignedBB aabb)
	{ setSize(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ); }
	
	public void setSize(Block b)
	{ setSize(b.getBlockBoundsMinX(), b.getBlockBoundsMinY(), b.getBlockBoundsMinZ(), b.getBlockBoundsMaxX(), b.getBlockBoundsMaxY(), b.getBlockBoundsMaxZ()); }
	
	public void setUV(float u0, float v0, float u1, float v1)
	{ minU = u0; minV = v0; maxU = u1; maxV = v1; }
	
	public void setUV(IIcon icon)
	{ setUV(icon.getMinU(), icon.getMinV(), icon.getMaxU(), icon.getMaxV()); }
	
	public void renderAll()
	{
		renderUp();
		renderDown();
		renderNorth();
		renderSouth();
		renderWest();
		renderEast();
	}
	
	public void renderFace(int face)
	{
		if(face == 0) renderUp();
		else if(face == 1) renderDown();
		else if(face == 2) renderNorth();
		else if(face == 3) renderSouth();
		else if(face == 4) renderWest();
		else if(face == 5) renderEast();
	}
	
	public void vertex(double x, double y, double z, float u, float v)
	{
		// float f = this.maxV - this.minV;
		//return this.minV + f * ((float)p_94207_1_ / 16.0F);
		if(textureType == TEX_SCALED)
			glTexCoord2f(u, v);
		else if(textureType == TEX_NOT_SCALED)
			glTexCoord2f(u, v);
		glVertex3d(x, y, z);
	}
	
	public void renderUp()
	{
		glBegin(GL_QUADS);
		vertex(minX, maxY, minZ, minU, minV);
		vertex(minX, maxY, maxZ, minU, maxV);
		vertex(maxX, maxY, maxZ, maxU, maxV);
		vertex(maxX, maxY, minZ, maxU, minV);
		glEnd();
	}
	
	public void renderDown()
	{
		glBegin(GL_QUADS);
		vertex(minX, minY, minZ, minU, minV);
		vertex(maxX, minY, minZ, maxU, minV);
		vertex(maxX, minY, maxZ, maxU, maxV);
		vertex(minX, minY, maxZ, minU, maxV);
		glEnd();
	}
	
	public void renderNorth()
	{
		glBegin(GL_QUADS);
		vertex(minX, minY, minZ, maxU, maxV);
		vertex(minX, maxY, minZ, maxU, minV);
		vertex(maxX, maxY, minZ, minU, minV);
		vertex(maxX, minY, minZ, minU, maxV);
		glEnd();
	}
	
	public void renderSouth()
	{
		glBegin(GL_QUADS);
		vertex(minX, minY, maxZ, minU, maxV);
		vertex(maxX, minY, maxZ, maxU, maxV);
		vertex(maxX, maxY, maxZ, maxU, minV);
		vertex(minX, maxY, maxZ, minU, minV);
		glEnd();
	}
	
	public void renderWest()
	{
		glBegin(GL_QUADS);
		vertex(minX, minY, minZ, minU, maxV);
		vertex(minX, minY, maxZ, maxU, maxV);
		vertex(minX, maxY, maxZ, maxU, minV);
		vertex(minX, maxY, minZ, minU, minV);
		glEnd();
	}
	
	public void renderEast()
	{
		glBegin(GL_QUADS);
		vertex(maxX, minY, minZ, maxU, maxV);
		vertex(maxX, maxY, minZ, maxU, minV);
		vertex(maxX, maxY, maxZ, minU, minV);
		vertex(maxX, minY, maxZ, minU, maxV);
		glEnd();
	}
}