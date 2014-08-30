package latmod.core.util;
import java.util.Random;

/** Made by LatvianModder */
public final class Vertex implements Cloneable
{
	public double x;
	public double y;
	public double z;
	
	public Vertex(double nx, double ny, double nz)
	{ x = nx; y = ny; z = nz; }
	
	public Vertex() { }
	
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
	
	public static final Vertex random(Random r)
	{ return new Vertex(r.nextDouble(), r.nextDouble(), r.nextDouble()); }
	
	public static final Vertex randomSin(Random r)
	{ Vertex v = random(r); v.scale(2D); v.add(-1D, -1D, -1D); return v; }
}