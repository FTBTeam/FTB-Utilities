package latmod.ftbu.world;

import ftb.lib.*;
import latmod.lib.*;
import net.minecraft.nbt.NBTTagCompound;

public class Warps
{
	private final FastMap<String, EntityPos> warps = new FastMap<String, EntityPos>();
	
	public void readFromNBT(NBTTagCompound tag, String s)
	{
		warps.clear();
		
		NBTTagCompound tag1 = (NBTTagCompound)tag.getTag(s);
		
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
		tag.setTag(s, tag1);
	}
	
	public String[] list()
	{
		if(warps.isEmpty()) return new String[0];
		return warps.keys.toArray(new String[0]);
	}
	
	public EntityPos get(String s)
	{ return warps.get(s); }
	
	public boolean set(String s, EntityPos pos)
	{ return warps.put(s, pos.clone()); }
	
	public boolean set(String s, int x, int y, int z, int dim)
	{ return warps.put(s, new EntityPos(x + 0.5D, y + 0.5D, z + 0.5D, dim)); }
	
	public boolean rem(String s)
	{ return warps.remove(s); }

	public int size()
	{ return warps.size(); }
}