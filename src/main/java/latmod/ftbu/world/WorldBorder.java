package latmod.ftbu.world;

import latmod.core.util.*;
import latmod.ftbu.util.*;
import net.minecraft.nbt.*;
import net.minecraft.util.ChunkCoordinates;

public class WorldBorder
{
	public boolean enabled;
	private final Box dim0;
	private final FastMap<Integer, Box> custom;
	
	public WorldBorder()
	{
		enabled = false;
		dim0 = new Box(0);
		dim0.size = 30000;
		custom = new FastMap<Integer, Box>();
	}
	
	public void readFromNBT(NBTTagCompound tag, String s)
	{
		NBTTagCompound tag1 = tag.getCompoundTag(s);
		if(tag1.hasNoTags()) return;
		
		enabled = tag1.getBoolean("ON");
		int[] a = tag1.getIntArray("DIM0");
		dim0.posX = a[0];
		dim0.posZ = a[1];
		dim0.size = a[2];
		custom.clear();
		
		if(tag1.hasKey("Custom"))
		{
			NBTTagList list = tag1.getTagList("DIM", LMNBTUtils.INT_ARRAY);
			
			for(int i = 0; i < list.tagCount(); i++)
			{
				a = list.func_150306_c(i);
				Box b = new Box(a[0]);
				b.posX = a[1];
				b.posZ = a[2];
				b.size = a[3];
				custom.put(b.dim, b);
			}
		}
	}
	
	public void writeToNBT(NBTTagCompound tag, String s)
	{
		NBTTagCompound tag1 = new NBTTagCompound();
		
		tag1.setBoolean("ON", enabled);
		tag1.setIntArray("DIM0", new int[] { dim0.posX, dim0.posZ, dim0.size });
		
		if(!custom.isEmpty())
		{
			NBTTagList list = new NBTTagList();
			
			for(int i = 0; i < custom.size(); i++)
			{
				Box b = custom.values.get(i);
				list.appendTag(new NBTTagIntArray(new int[] { b.dim, b.posX, b.posZ, b.size }));
			}
			
			tag1.setTag("DIM", list);
		}
		
		tag.setTag(s, tag1);
	}
	
	private Box get(int dim)
	{
		if(dim == 0) return dim0;
		return custom.get(Integer.valueOf(dim));
	}
	
	private Box getNew(int dim)
	{
		Box b = get(dim);
		if(b == null) custom.put(dim, b = new Box(dim));
		return b;
	}
	
	public int getSize(int dim)
	{
		if(!enabled) return 0;
		else if(dim == 0) return dim0.size;
		else
		{
			Box b = get(dim);
			if(b != null) return b.size;
			return (int)(dim0.size * LMDimUtils.getMovementFactor(dim));
		}
	}
	
	public void setSize(int dim, int s)
	{ getNew(dim).size = s; }
	
	public void setPos(int dim, int x, int z)
	{ getNew(dim).setPos(x, z); }
	
	public boolean isOutside(int dim, int cx, int cz)
	{
		if(!enabled || isInSpawn(dim, cx, cz)) return false;
		Box b = get(dim);
		if(b != null) return b.isOutside(cx, cz);
		int radius = getSize(dim);
		int min = MathHelperLM.chunk(-radius);
		int max = MathHelperLM.chunk(radius);
		return cx >= max || cx <= min || cz >= max || cz <= min;
	}
	
	public boolean isOutsideF(int dim, double x, double z)
	{ return isOutside(dim, MathHelperLM.chunk(x), MathHelperLM.chunk(z)); }
	
	private static class Box
	{
		public final int dim;
		public int posX, posZ, size;
		
		public Box(int d)
		{ dim = d; }
		
		public void setPos(int x, int z)
		{ posX = x; posZ = z; }
		
		public boolean isOutside(int cx, int cz)
		{
			int minX = MathHelperLM.chunk(posX - size);
			int maxX = MathHelperLM.chunk(posX + size);
			int minZ = MathHelperLM.chunk(posZ - size);
			int maxZ = MathHelperLM.chunk(posZ + size);
			return cx >= maxX || cx <= minX || cz >= maxZ || cz <= minZ;
		}
	}
	
	public static boolean isInSpawn(int dim, int cx, int cz)
	{
		if(dim != 0) return false;
		int radius = LatCoreMC.getServer().getSpawnProtectionSize();
		if(radius <= 0) return false;
		ChunkCoordinates c = LMDimUtils.getSpawnPoint(0);
		int minX = MathHelperLM.chunk(c.posX + 0.5D - radius);
		int minZ = MathHelperLM.chunk(c.posZ + 0.5D - radius);
		int maxX = MathHelperLM.chunk(c.posX + 0.5D + radius);
		int maxZ = MathHelperLM.chunk(c.posZ + 0.5D + radius);
		return cx >= minX && cx <= maxX && cz >= minZ && cz <= maxZ;
	}
	
	public static boolean isInSpawnF(int dim, double x, double z)
	{ return dim == 0 && isInSpawn(dim, MathHelperLM.chunk(x), MathHelperLM.chunk(z)); }
}