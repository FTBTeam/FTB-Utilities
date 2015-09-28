package latmod.ftbu.world;

import latmod.core.util.*;
import latmod.ftbu.util.*;
import net.minecraft.nbt.NBTTagCompound;

public class Warps
{
	private final FastMap<String, EntityPos> warps = new FastMap<String, EntityPos>();
	
	public void readFromNBT(NBTTagCompound tag, String s)
	{
		warps.clear();
		
		NBTTagCompound tag1 = (NBTTagCompound)tag.getTag("Warps");
		
		if(tag1 != null && !tag1.hasNoTags())
		{
			FastList<String> l = LMNBTUtils.getMapKeys(tag1);
			
			for(int i = 0; i < l.size(); i++)
			{
				int[] a = tag1.getIntArray(l.get(i));
				set(l.get(i), a[0], a[1], a[2], a[3]);
			}
		}
	}
	
	public void writeToNBT(NBTTagCompound tag, String s)
	{
		NBTTagCompound tag1 = new NBTTagCompound();
		for(int i = 0; i < warps.size(); i++)
			tag1.setIntArray(warps.keys.get(i), warps.values.get(i).toIntArray());
		tag.setTag("Warps", tag1);
	}
	
	public String[] list()
	{ return warps.keys.toArray(new String[0]); }
	
	public EntityPos get(String s)
	{ return warps.get(s); }
	
	public boolean set(String s, int x, int y, int z, int dim)
	{ return warps.put(s, new EntityPos(x + 0.5D, y + 0.5D, z + 0.5D, dim)); }
	
	public boolean rem(String s)
	{ return warps.remove(s); }
}