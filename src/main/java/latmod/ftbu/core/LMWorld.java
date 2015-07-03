package latmod.ftbu.core;

import java.util.UUID;

import latmod.ftbu.core.util.*;
import net.minecraft.nbt.NBTTagCompound;

public class LMWorld
{
	private static UUID worldID;
	private static String worldIDS;
	public static final FastMap<String, EntityPos> warps = new FastMap<String, EntityPos>();
	
	public static void load(NBTTagCompound tag)
	{
		if(tag.hasKey("UUID"))
			setID(LatCoreMC.getUUIDFromString(tag.getString("UUID")));
		else
			setID(UUID.randomUUID());
		
		LatCoreMC.logger.info("WorldID: " + getIDS());
		
		warps.clear();
		
		NBTTagCompound tagWarps = (NBTTagCompound)tag.getTag("Warps");
		
		if(tagWarps != null && !tagWarps.hasNoTags())
		{
			FastList<String> l = NBTHelper.getMapKeys(tagWarps);
			
			for(int i = 0; i < l.size(); i++)
			{
				int[] a = tagWarps.getIntArray(l.get(i));
				setWarp(l.get(i), a[0], a[1], a[2], a[3]);
			}
		}
	}
	
	public static void save(NBTTagCompound tag)
	{
		tag.setString("UUID", getIDS());
		
		NBTTagCompound tagWarps = new NBTTagCompound();
		for(int i = 0; i < warps.size(); i++)
			tagWarps.setIntArray(warps.keys.get(i), warps.values.get(i).toIntArray());
		tag.setTag("Warps", tagWarps);
	}
	
	// WorldID //
	
	public static final UUID getID()
	{ return worldID; }
	
	public static final String getIDS()
	{ return worldIDS; }
	
	public static final void setID(UUID id)
	{
		worldID = id;
		worldIDS = LatCoreMC.toShortUUID(worldID);
	}
	
	// Warps //
	
	public static String[] listWarps()
	{ return warps.keys.toArray(new String[0]); }
	
	public static EntityPos getWarp(String s)
	{ return warps.get(s); }
	
	public static boolean setWarp(String s, int x, int y, int z, int dim)
	{ return warps.put(s, new EntityPos(x + 0.5D, y + 0.5D, z + 0.5D, dim)); }
	
	public static boolean remWarp(String s)
	{ return warps.remove(s); }
}