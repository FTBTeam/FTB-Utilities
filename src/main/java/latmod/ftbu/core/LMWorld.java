package latmod.ftbu.core;

import java.util.UUID;

import latmod.ftbu.core.util.FastList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

public class LMWorld
{
	private static UUID worldID;
	private static String worldIDS;
	public static final FastList<Warp> warps = new FastList<Warp>();
	
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
		for(Warp w : warps)
			tagWarps.setIntArray(w.name, new int[] { w.x, w.y, w.z, w.dim });
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
	{
		String[] s = new String[warps.size()];
		for(int i = 0; i < s.length; i++)
			s[i] = warps.get(i).name;
		return s;
	}
	
	public static LMWorld.Warp getWarp(String s)
	{ return warps.getObj(s); }
	
	public static boolean setWarp(String s, int x, int y, int z, int dim)
	{
		int i = warps.indexOf(s);
		if(i == -1) { warps.add(new LMWorld.Warp(s, x, y, z, dim)); return true; }
		else { warps.set(i, new LMWorld.Warp(s, x, y, z, dim)); return false; }
	}
	
	public static boolean remWarp(String s)
	{ return warps.remove(s); }
	
	public static class Warp
	{
		public String name;
		public int x, y, z;
		public int dim;
		
		public Warp(String s, int px, int py, int pz, int d)
		{ name = s; x = px; y = py; z = pz; dim = d; }
		
		public String toString()
		{ return name; }
		
		public boolean equals(Object o)
		{ return o != null && (o == this || o.toString().equals(toString())); }
		
		public void teleportPlayer(EntityPlayerMP ep)
		{ Teleporter.travelEntity(ep, x + 0.5D, y + 0.5D, z + 0.5D, dim); }
	}
}