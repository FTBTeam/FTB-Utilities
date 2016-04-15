package ftb.utils.world;

import com.google.gson.*;
import ftb.lib.*;
import latmod.lib.LMJsonUtils;
import net.minecraft.nbt.NBTTagCompound;

import java.util.*;

public class Warps
{
	private final HashMap<String, BlockDimPos> warps = new HashMap<>();
	
	public void readFromNBT(NBTTagCompound tag, String s)
	{
		warps.clear();
		
		NBTTagCompound tag1 = (NBTTagCompound) tag.getTag(s);
		
		if(tag1 != null && !tag1.hasNoTags())
		{
			for(String s1 : LMNBTUtils.getMapKeys(tag1))
			{
				warps.put(s1, new BlockDimPos(tag1.getIntArray(s1)));
			}
		}
	}
	
	public void writeToNBT(NBTTagCompound tag, String s)
	{
		NBTTagCompound tag1 = new NBTTagCompound();
		for(Map.Entry<String, BlockDimPos> e : warps.entrySet())
			tag1.setIntArray(e.getKey(), e.getValue().toIntArray());
		tag.setTag(s, tag1);
	}
	
	public void readFromJson(JsonObject g, String s)
	{
		warps.clear();
		if(!g.has(s)) return;
		
		JsonObject g1 = g.get(s).getAsJsonObject();
		
		if(g1 != null) for(Map.Entry<String, JsonElement> e : g1.entrySet())
		{
			if(e.getValue().isJsonArray())
			{
				set(e.getKey(), new BlockDimPos(LMJsonUtils.fromIntArray(e.getValue())));
			}
			else
			{
				JsonObject o = e.getValue().getAsJsonObject();
				set(e.getKey(), new BlockDimPos(o.get("x").getAsInt(), o.get("y").getAsInt(), o.get("z").getAsInt(), o.get("dim").getAsInt()));
			}
		}
	}
	
	public void writeToJson(JsonObject g, String s)
	{
		JsonObject g1 = new JsonObject();
		
		for(Map.Entry<String, BlockDimPos> e : warps.entrySet())
		{
			BlockDimPos pos = e.getValue();
			JsonObject o = new JsonObject();
			o.add("dim", new JsonPrimitive(pos.dim));
			o.add("x", new JsonPrimitive(pos.x));
			o.add("y", new JsonPrimitive(pos.y));
			o.add("z", new JsonPrimitive(pos.z));
			g1.add(e.getKey(), o);
		}
		
		g.add(s, g1);
	}
	
	public Set<String> list()
	{ return warps.keySet(); }
	
	public BlockDimPos get(String s)
	{ return warps.get(s); }
	
	public boolean set(String s, BlockDimPos pos)
	{
		if(pos == null) return warps.remove(s) != null;
		return warps.put(s, pos.copy()) == null;
	}
	
	public int size()
	{ return warps.size(); }
}