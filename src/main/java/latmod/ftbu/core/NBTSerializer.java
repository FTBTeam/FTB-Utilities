package latmod.ftbu.core;

import java.lang.reflect.Type;
import java.util.Map;

import latmod.ftbu.core.util.FastMap;
import net.minecraft.nbt.*;

import com.google.gson.*;

public abstract class NBTSerializer<E> implements JsonDeserializer<E>, JsonSerializer<E>
{
	public static void init(GsonBuilder gb) // LatCore
	{
		gb.registerTypeHierarchyAdapter(NBTTagCompound.class, new MapS());
		gb.registerTypeHierarchyAdapter(NBTTagList.class, new ListS());
		gb.registerTypeHierarchyAdapter(NBTTagString.class, new StringS());
		gb.registerTypeHierarchyAdapter(NBTBase.NBTPrimitive.class, new PrimitiveS());
	}
	
	private static Type getElementType(JsonElement e)
	{
		if(e.isJsonNull()) return null;
		else if(e.isJsonArray()) return NBTTagList.class;
		else if(e.isJsonObject()) return NBTTagCompound.class;
		try { if(e.getAsString() != null) return NBTTagString.class; }
		catch(Exception ex) { } return NBTBase.NBTPrimitive.class;
	}
	
	// Map //
	
	private static class MapS extends NBTSerializer<NBTTagCompound>
	{
		public NBTTagCompound deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException 
		{
			NBTTagCompound tag = new NBTTagCompound();
			JsonObject o = json.getAsJsonObject();
			
			for(Map.Entry<String, JsonElement> e : o.entrySet())
			{
				JsonElement je = e.getValue();
				tag.setTag(e.getKey(), context.deserialize(je, getElementType(je)));
			}
			
			return tag;
		}
		
		public JsonElement serialize(NBTTagCompound src, Type typeOfSrc, JsonSerializationContext context)
		{
			JsonObject o = new JsonObject();
			FastMap<String, NBTBase> m = LMNBTUtils.toFastMap(src);
			for(int i = 0; i < m.size(); i++)
				o.add(m.keys.get(i), context.serialize(m.values.get(i)));
			return o;
		}
	}
	
	// List //
	
	private static class ListS extends NBTSerializer<NBTTagList>
	{
		public NBTTagList deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException 
		{
			NBTTagList list = new NBTTagList();
			JsonArray a = json.getAsJsonArray();
			for(int i = 0; i < a.size(); i++)
				list.appendTag(context.deserialize(a.get(i), NBTBase.class));
			return list;
		}
		
		public JsonElement serialize(NBTTagList src, Type typeOfSrc, JsonSerializationContext context)
		{
			JsonArray o = new JsonArray();
			
			NBTTagList l = (NBTTagList)src;
			int id1 = l.func_150303_d();
			
			for(int i = 0; i < l.tagCount(); i++)
			{
				Object o1 = null;
				
				if(id1 == LMNBTUtils.MAP) o1 = l.getCompoundTagAt(i);
				//else if(id1 == LMNBTUtils.INT_ARRAY) o1 = l.func_150306_c(i);
				else if(id1 == LMNBTUtils.FLOAT) o1 = l.func_150308_e(i);
				else if(id1 == LMNBTUtils.DOUBLE) o1 = l.func_150309_d(i);
				else if(id1 == LMNBTUtils.STRING) o1 = l.getStringTagAt(i);
				
				if(o1 != null)
					o.add(context.serialize(o1));
			}
			
			return o;
		}
	}
	
	// String //
	
	private static class StringS extends NBTSerializer<NBTTagString>
	{
		public NBTTagString deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException 
		{
			return new NBTTagString(json.getAsString());
		}
		
		public JsonElement serialize(NBTTagString src, Type typeOfSrc, JsonSerializationContext context)
		{
			return new JsonPrimitive(src.func_150285_a_());
		}
	}
	
	// Primitives //
	
	private static class PrimitiveS extends NBTSerializer<NBTBase.NBTPrimitive>
	{
		public NBTBase.NBTPrimitive deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException 
		{
			try { return new NBTTagDouble(json.getAsDouble()); }
			catch(Exception e) { } return new NBTTagLong(json.getAsLong());
		}
		
		public JsonElement serialize(NBTBase.NBTPrimitive src, Type typeOfSrc, JsonSerializationContext context)
		{
			if(src.getId() == LMNBTUtils.FLOAT || src.getId() == LMNBTUtils.DOUBLE)
				return new JsonPrimitive(src.func_150286_g());
			return new JsonPrimitive(src.func_150291_c());
		}
	}
}