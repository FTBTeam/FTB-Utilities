package latmod.ftbu.world;

import com.google.gson.*;
import latmod.lib.util.Pos2I;

public class WorldBorder
{
	public final int dim;
	public Pos2I pos;
	public int size;
	
	public WorldBorder(int d)
	{
		dim = d;
		pos = new Pos2I(0, 0);
		size = 0;
	}

	public JsonElement toJson()
	{
		JsonObject o = new JsonObject();
		o.add("dim", new JsonPrimitive(dim));
		o.add("x", new JsonPrimitive(pos.x));
		o.add("z", new JsonPrimitive(pos.y));
		o.add("size", new JsonPrimitive(size));
		return o;
	}
	
	public static WorldBorder fromJson(JsonElement e)
	{
		JsonObject a = e.getAsJsonObject();
		WorldBorder wb = new WorldBorder(a.get("dim").getAsInt());
		wb.pos.x = a.get("x").getAsInt();
		wb.pos.y = a.get("z").getAsInt();
		wb.size = a.get("size").getAsInt();
		return wb;
	}
}