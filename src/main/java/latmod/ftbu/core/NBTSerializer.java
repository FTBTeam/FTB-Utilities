package latmod.ftbu.core;

import java.lang.reflect.Type;

import com.google.gson.*;

import latmod.ftbu.core.util.FastMap;
import net.minecraft.nbt.*;

public abstract class NBTSerializer
{
	public static void init(GsonBuilder gb) // LatCore
	{
		gb.registerTypeHierarchyAdapter(NBTBase.class, new NBTBaseDeserializer());
		gb.registerTypeHierarchyAdapter(NBTTagCompound.class, new MapS());
		gb.registerTypeHierarchyAdapter(NBTTagList.class, new ListS());
		gb.registerTypeHierarchyAdapter(NBTTagString.class, new StringS());
		gb.registerTypeHierarchyAdapter(NBTBase.NBTPrimitive.class, new PrimitiveS());
	}
	
	public static class NBTBaseDeserializer implements JsonDeserializer<NBTBase>
	{
		public NBTBase deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
		{
			try { return JsonToNBT.func_150315_a(json.toString()); }
			catch (NBTException e)
			{ e.printStackTrace(); }
			return null;
		}
	}
	
	private static class MapS implements JsonSerializer<NBTTagCompound>
	{
		public JsonElement serialize(NBTTagCompound src, Type typeOfSrc, JsonSerializationContext context)
		{
			JsonObject o = new JsonObject();
			FastMap<String, NBTBase> m = LMNBTUtils.toFastMap(src);
			for(int i = 0; i < m.size(); i++)
				o.add(m.keys.get(i), context.serialize(m.values.get(i)));
			return o;
		}
	}
	
	private static class ListS implements JsonSerializer<NBTTagList>
	{
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
	
	private static class StringS implements JsonSerializer<NBTTagString>
	{
		public JsonElement serialize(NBTTagString src, Type typeOfSrc, JsonSerializationContext context)
		{
			return new JsonPrimitive(src.func_150285_a_());
		}
	}
	
	private static class PrimitiveS implements JsonSerializer<NBTBase.NBTPrimitive>
	{
		public JsonElement serialize(NBTBase.NBTPrimitive src, Type typeOfSrc, JsonSerializationContext context)
		{
			if(src.getId() == LMNBTUtils.FLOAT || src.getId() == LMNBTUtils.DOUBLE)
				return new JsonPrimitive(src.func_150286_g());
			return new JsonPrimitive(src.func_150291_c());
		}
	}
}