package latmod.core;
import java.util.Random;

import net.minecraft.entity.Entity;
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
	{ return MathHelper.sq(x1 - x) + MathHelper.sq(y1 - y) + MathHelper.sq(z1 - z); }
	
	public double dist(double x1, double y1, double z1)
	{ return MathHelper.sqrt(distSq(x1, y1, z1)); }
	
	public double distSq(Vertex v)
	{ return distSq(v.x, v.y, v.z); }
	
	public double dist(Vertex v)
	{ return MathHelper.sqrt(dist(v)); }
}