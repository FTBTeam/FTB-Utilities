package latmod.ftbu.world;

import latmod.lib.*;
import latmod.lib.util.Pos2I;
import net.minecraft.nbt.NBTTagCompound;

public class LMWorldSettings
{
	private final FastMap<Integer, WorldBorder> worldBorder;
	
	public LMWorldSettings()
	{
		worldBorder = new FastMap<Integer, WorldBorder>();
		worldBorder.put(0, new WorldBorder(0));
	}
	
	public void readFromNBT(NBTTagCompound tag, boolean server)
	{
		if(server)
		{
		}
	}
	
	public void writeToNBT(NBTTagCompound tag, boolean server)
	{
		if(server)
		{
		}
	}
	
	public WorldBorder get(int dim)
	{ return worldBorder.get(Integer.valueOf(dim)); }
	
	public WorldBorder getNew(int dim)
	{
		WorldBorder b = get(dim);
		if(b == null) worldBorder.put(dim, b = new WorldBorder(dim));
		return b;
	}
	
	public int getSize(int dim)
	{
		/*
		if(!enabled) return 0;
		else if(dim == 0) return dim0.size;
		else
		{
			WorldBorder b = get(dim);
			if(b != null) return b.size;
			return (int)(dim0.size * LMDimUtils.getMovementFactor(dim));
		}*/
		return 0;
	}
	
	public Pos2I getPos(int dim)
	{
		WorldBorder b = get(dim);
		return (b == null) ? new Pos2I(0, 0) : new Pos2I(b.posX, b.posZ);
	}
	
	public void setSize(int dim, int s)
	{ getNew(dim).size = s; }
	
	public void setPos(int dim, int x, int z)
	{ getNew(dim).setPos(x, z); }
	
	public boolean isOutside(int dim, int cx, int cz)
	{
		if(WorldBorder.isInSpawn(dim, cx, cz)) return false;
		int radius = getSize(dim);
		if(radius == 0) return false;
		int min = MathHelperLM.chunk(-radius);
		int max = MathHelperLM.chunk(radius);
		return cx >= max || cx <= min || cz >= max || cz <= min;
	}
	
	public boolean isOutsideF(int dim, double x, double z)
	{ return isOutside(dim, MathHelperLM.chunk(x), MathHelperLM.chunk(z)); }

	public boolean isEnabled(int dim)
	{
		WorldBorder wb = get(dim);
		return getNew(0).enabled && (wb == null || wb.enabled);
	}
}