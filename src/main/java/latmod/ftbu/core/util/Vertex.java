package latmod.ftbu.core.util;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;

/** Made by LatvianModder */
public final class Vertex implements Cloneable
{
	public double x;
	public double y;
	public double z;
	
	public Vertex() { }
	
	public Vertex(double nx, double ny, double nz)
	{ x = nx; y = ny; z = nz; }
	
	public Vertex(Entity e, boolean y)
	{ this(e.posX, y ? e.posY : 0D, e.posZ); }
	
	public Vertex(Entity e)
	{ this(e, true); }
	
	public Vertex(ChunkCoordinates c, boolean y)
	{ this(c.posX  + 0.5D, (y ? c.posY : 0D) + 0.5D, c.posZ + 0.5D); }
	
	public Vertex(ChunkCoordinates c)
	{ this(c, true); }
	
	public Vertex(Random r, boolean sin)
	{
		this(r.nextDouble(), r.nextDouble(), r.nextDouble());
		if(sin) { scale(2D); add(-1D, -1D, -1D); }
	}
	
	public void set(double nx, double ny, double nz)
	{ x = nx; y = ny; z = nz; }
	
	public void set(Vertex v)
	{ set(v.x, v.y, v.z); }
	
	public void add(double ax, double ay, double az)
	{ set(x + ax, y + ay, z + az); }
	
	public void add(Vertex v, double s)
	{ add(v.x * s, v.y * s, v.z * s); }
	
	public void scale(double sx, double sy, double sz)
	{ x *= sx; y *= sy; z *= sz; }
	
	public void scale(Vertex v, double s)
	{ scale(v.x * s, v.y * s, v.z * s); }
	
	public void scale(double s)
	{ scale(s, s, s); }
	
	public boolean isNull()
	{ return x == 0D && y == 0D && z == 0D; }
	
	public boolean containsNaN()
	{ return Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(y); }
	
	public boolean equals(Object o)
	{ return (o instanceof Vertex && equalsPos((Vertex)o)); }
	
	public boolean equalsPos(Vertex v)
	{ return v.x == x && v.y == y && v.z == z; }
	
	public Vertex clone()
	{ return new Vertex(x, y, z); }
	
	public double distSq(double x1, double y1, double z1)
	{ return MathHelperLM.sq(x1 - x) + MathHelperLM.sq(y1 - y) + MathHelperLM.sq(z1 - z); }
	
	public double dist(double x1, double y1, double z1)
	{ return MathHelperLM.sqrt(distSq(x1, y1, z1)); }
	
	public double distSq(Vertex v)
	{ return distSq(v.x, v.y, v.z); }
	
	public double dist(Vertex v)
	{ return MathHelperLM.sqrt(distSq(v)); }
	
	public double atan2(Vertex v)
	{
		if(v == null) return -1D;
		return Math.atan2(x - v.x, z - v.z);
	}
	
	public double atan2Y(Vertex v)
	{
		if(v == null) return -1D;
		return Math.atan2(x - v.x, y - v.y);
	}
	
	public double atan(Vertex v)
	{
		if(v == null) return -1D;
		return Math.atan(y - v.y);
	}
	
	public int getX()
	{ return MathHelperLM.floor(x); }
	
	public int getY()
	{ return MathHelperLM.floor(y); }
	
	public int getZ()
	{ return MathHelperLM.floor(z); }
	
	public static class DimPos
	{
		public Vertex pos;
		public int dim;
		
		public DimPos(double x, double y, double z, int d)
		{ pos = new Vertex(x, y, z); dim = d; }
		
		public DimPos(Entity e)
		{ this(e.posX, e.posY, e.posZ, e.dimension); }
		
		public DimPos()
		{ this(0D, 0D, 0D, 0); }
		
		public void readFromNBT(NBTTagCompound tag)
		{
			pos.x = tag.getDouble("X");
			pos.y = tag.getDouble("Y");
			pos.z = tag.getDouble("Z");
			dim = tag.getInteger("D");
		}
		
		public void writeToNBT(NBTTagCompound tag)
		{
			tag.setDouble("X", pos.x);
			tag.setDouble("Y", pos.y);
			tag.setDouble("Z", pos.z);
			tag.setInteger("D", dim);
		}
		
		public int intX()
		{ return MathHelperLM.floor(pos.x); }
		
		public int intY()
		{ return MathHelperLM.floor(pos.y); }
		
		public int intZ()
		{ return MathHelperLM.floor(pos.z); }
		
		public boolean equals(Object o)
		{ return (o instanceof DimPos) && equalsDimPos((DimPos)o); }
		
		public boolean equalsDimPos(DimPos p)
		{ return pos.equalsPos(p.pos) && dim == p.dim; }
		
		public static class Rot extends DimPos
		{
			public float yaw, pitch;
			
			public Rot(double x, double y, double z, int d, float yr, float pr)
			{ super(x, y, z, d); yaw = yr; pitch = pr; }
			
			public Rot(Entity e)
			{ super(e); yaw = e.rotationYaw; pitch = e.rotationPitch; }
			
			public void readFromNBT(NBTTagCompound tag)
			{
				super.readFromNBT(tag);
				yaw = tag.getFloat("YR");
				pitch = tag.getFloat("PR");
			}
			
			public void writeToNBT(NBTTagCompound tag)
			{
				super.writeToNBT(tag);
				tag.setFloat("YR", yaw);
				tag.setFloat("PR", pitch);
			}
			
			public boolean equals(Object o)
			{ return (o instanceof Rot) && equalsDimPosRot((Rot)o); }
			
			public boolean equalsDimPosRot(Rot p)
			{ return equalsDimPos(p) && yaw == p.yaw && pitch == p.pitch; }
		}
	}
}