package latmod.ftbu.world;

import latmod.ftbu.util.*;
import latmod.lib.*;
import latmod.lib.util.Pos2I;
import net.minecraft.nbt.*;

public class LMWorldSettings
{
	public final LMWorld world;
	private boolean borderEnabled;
	private final WorldBorder worldBorder0;
	private final FastMap<Integer, WorldBorder> worldBorder;
	
	public LMWorldSettings(LMWorld w)
	{
		world = w;
		borderEnabled = false;
		worldBorder0 = new WorldBorder(0);
		worldBorder0.size = 0;
		worldBorder = new FastMap<Integer, WorldBorder>();
	}
	
	public void readFromNBT(NBTTagCompound tag, boolean server)
	{
		borderEnabled = tag.getBoolean("WB_Enabled");
		worldBorder.clear();
		worldBorder0.size = 0;
		
		NBTTagList wbList = tag.getTagList("WBorder", LMNBTUtils.INT_ARRAY);
		
		for(int i = 0; i < wbList.tagCount(); i++)
		{
			int[] ai = wbList.func_150306_c(i);
			
			if(ai.length >= 4)
			{
				if(ai[0] == 0)
				{
					worldBorder0.pos.x = ai[1];
					worldBorder0.pos.y = ai[2];
					worldBorder0.size = ai[3];
				}
				else
				{
					WorldBorder wb = new WorldBorder(ai[0]);
					wb.pos.x = ai[1];
					wb.pos.y = ai[2];
					wb.size = ai[3];
					worldBorder.put(ai[0], wb);
				}
			}
		}
	}
	
	public void writeToNBT(NBTTagCompound tag, boolean server)
	{
		tag.setBoolean("WB_Enabled", borderEnabled);
		
		NBTTagList wbList = new NBTTagList();
		
		wbList.appendTag(new NBTTagIntArray(new int[] { 0, worldBorder0.pos.x, worldBorder0.pos.y, worldBorder0.size }));
		
		for(int i = 0; i < worldBorder.size(); i++)
		{
			WorldBorder wb = worldBorder.values.get(i);
			wbList.appendTag(new NBTTagIntArray(new int[] { wb.dim, wb.pos.x, wb.pos.y, wb.size }));
		}
		
		tag.setTag("WBorder", wbList);
	}
	
	public void setWorldBorderEnabled(boolean b)
	{
		if(borderEnabled != b && world.side.isServer())
		{
			borderEnabled = b;
			world.update();
		}
	}
	
	public WorldBorder get(int dim)
	{
		if(dim == 0) return worldBorder0;
		return worldBorder.get(Integer.valueOf(dim));
	}
	
	public WorldBorder getNew(int dim)
	{
		WorldBorder b = get(dim);
		if(b == null) worldBorder.put(dim, b = new WorldBorder(dim));
		return b;
	}
	
	public int getSize(int dim)
	{
		if(!borderEnabled) return 0;
		else if(dim == 0) return worldBorder0.size;
		else
		{
			WorldBorder b = get(dim);
			if(b != null && b.size > -1) return b.size;
			return (int)(worldBorder0.size * LMDimUtils.getWorldScale(dim));
		}
	}
	
	public Pos2I getPos(int dim)
	{
		WorldBorder b = get(dim);
		return (b == null) ? new Pos2I(0, 0) : b.pos;
	}
	
	public void setSize(int dim, int s)
	{
		if(!world.side.isServer()) return;
		
		WorldBorder wb = getNew(dim);
		if(wb.size != s)
		{
			wb.size = s;
			world.update();
		}
	}
	
	public void setPos(int dim, int x, int z)
	{
		if(!world.side.isServer()) return;
		
		WorldBorder wb = getNew(dim);
		if(wb.pos.x != x || wb.pos.y != z)
		{
			wb.setPos(x, z);
			world.update();
		}
	}
	
	public boolean isOutside(int dim, int cx, int cz)
	{
		if(Claims.isInSpawn(dim, cx, cz)) return false;
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
		if(!borderEnabled) return false;
		else if(dim == 0) return worldBorder0.size > 0;
		WorldBorder b = get(dim);
		if(b == null) return isEnabled(0);
		return getSize(dim) > 0;
	}
}