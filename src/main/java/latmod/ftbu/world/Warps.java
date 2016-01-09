package latmod.ftbu.world;

import com.google.gson.*;
import ftb.lib.*;
import latmod.lib.LMJsonUtils;
import net.minecraft.nbt.NBTTagCompound;

import java.util.*;

public class Warps
{
	private final HashMap<String, EntityPos> warps = new HashMap<>();
	
	public void readFromNBT(NBTTagCompound tag, String s)
	{
		warps.clear();
		
		NBTTagCompound tag1 = (NBTTagCompound) tag.getTag(s);
		
		if(tag1 != null && !tag1.hasNoTags())
		{
			List<String> l = LMNBTUtils.getMapKeys(tag1);
			
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
		for(Map.Entry<String, EntityPos> e : warps.entrySet())
			tag1.setIntArray(e.getKey(), e.getValue().toIntArray());
		tag.setTag(s, tag1);
	}
	
	public void readFromJson(JsonObject g, String s)
	{
		JsonObject g1 = g.get(s).getAsJsonObject();
		
		if(g1 != null) for(Map.Entry<String, JsonElement> e : g1.entrySet())
		{
			if(e.getValue().isJsonArray())
			{
				int[] a = LMJsonUtils.fromArray(e.getValue());
				set(e.getKey(), a[0], a[1], a[2], a[3]);
			}
			else
			{
				JsonObject o = e.getValue().getAsJsonObject();
				set(e.getKey(), o.get("dim").getAsInt(), o.get("x").getAsInt(), o.get("y").getAsInt(), o.get("z").getAsInt());
			}
		}
	}
	
	public void writeToJson(JsonObject g, String s)
	{
		JsonObject g1 = new JsonObject();
		for(Map.Entry<String, EntityPos> e : warps.entrySet())
		{
			EntityPos pos = e.getValue();
			JsonObject o = new JsonObject();
			o.add("dim", new JsonPrimitive(pos.dim));
			o.add("x", new JsonPrimitive(pos.intX()));
			o.add("y", new JsonPrimitive(pos.intY()));
			o.add("z", new JsonPrimitive(pos.intZ()));
			g1.add(e.getKey(), o);
		}
		g.add(s, g1);
	}
	
	public String[] list()
	{
		if(warps.isEmpty()) return new String[0];
		return warps.keySet().toArray(new String[0]);
	}
	
	public EntityPos get(String s)
	{ return warps.get(s); }
	
	public boolean set(String s, EntityPos pos)
	{ return warps.put(s, pos.clone()) == null; }
	
	public boolean set(String s, int x, int y, int z, int dim)
	{ return set(s, new EntityPos(x + 0.5D, y + 0.5D, z + 0.5D, dim)); }
	
	public boolean rem(String s)
	{ return warps.remove(s) != null; }

	public int size()
	{ return warps.size(); }
}