package latmod.ftbu.core.client.model;

import net.minecraft.block.Block;
import net.minecraft.util.*;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class CubeRenderer
{
	public static final CubeRenderer instance = new CubeRenderer();
	
	public boolean enableNormals = true;
	
	protected int currentSide;
	protected final double[] vertexMapX = new double[24];
	protected final double[] vertexMapY = new double[24];
	protected final double[] vertexMapZ = new double[24];
	
	public void setVertex(int idx, double x, double y, double z)
	{ vertexMapX[idx] = x; vertexMapY[idx] = y; vertexMapZ[idx] = z; }
	
	public void setSize(double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
	{
		setVertex(0, minX, minY, minZ);
		setVertex(1, maxX, minY, minZ);
		setVertex(2, maxX, minY, maxZ);
		setVertex(3, minX, minY, maxZ);
		
		setVertex(4, minX, maxY, minZ);
		setVertex(5, minX, maxY, maxZ);
		setVertex(6, maxX, maxY, maxZ);
		setVertex(7, maxX, maxY, minZ);
		
		setVertex(8, minX, minY, maxZ);
		setVertex(9, maxX, minY, maxZ);
		setVertex(10, maxX, maxY, maxZ);
		setVertex(11, minX, maxY, maxZ);
		
		setVertex(12, minX, minY, minZ);
		setVertex(13, minX, maxY, minZ);
		setVertex(14, maxX, maxY, minZ);
		setVertex(15, maxX, minY, minZ);
		
		setVertex(16, minX, minY, minZ);
		setVertex(17, minX, minY, maxZ);
		setVertex(18, minX, maxY, maxZ);
		setVertex(19, minX, maxY, minZ);
		
		setVertex(20, maxX, minY, minZ);
		setVertex(21, maxX, maxY, minZ);
		setVertex(22, maxX, maxY, maxZ);
		setVertex(23, maxX, minY, maxZ);
	}
	
	public void setSize(AxisAlignedBB aabb)
	{ setSize(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ); }
	
	public void setSize(Block b)
	{ setSize(b.getBlockBoundsMinX(), b.getBlockBoundsMinY(), b.getBlockBoundsMinZ(), b.getBlockBoundsMaxX(), b.getBlockBoundsMaxY(), b.getBlockBoundsMaxZ()); }
	
	public void renderAll()
	{
		for(int i = 0; i < 6; i++)
			renderSide0(i);
	}
	
	public void renderSide(int s)
	{
		if(s < 0 || s >= 6);
		renderSide0(s);
	}
	
	protected void vertex(int i)
	{
		int j = currentSide * 4 + i;
		GL11.glVertex3d(vertexMapX[j], vertexMapY[j], vertexMapZ[j]);
	}
	
	protected void begin(int i)
	{
		currentSide = i;
		if(enableNormals)
			GL11.glNormal3f(Facing.offsetsXForSide[i], Facing.offsetsYForSide[i], Facing.offsetsZForSide[i]);
		GL11.glBegin(GL11.GL_QUADS);
	}
	
	protected void end()
	{ GL11.glEnd(); }
	
	protected void renderSide0(int s)
	{ begin(s); vertex(0); vertex(1); vertex(2); vertex(3); end(); }
	
	public void renderDown()
	{ renderSide0(0); }
	
	public void renderUp()
	{ renderSide0(1); }
	
	public void renderSouth()
	{ renderSide0(2); }
	
	public void renderNorth()
	{ renderSide0(3); }
	
	public void renderWest()
	{ renderSide0(4); }
	
	public void renderEast()
	{ renderSide0(5); }
}