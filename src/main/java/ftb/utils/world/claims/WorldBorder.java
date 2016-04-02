package ftb.utils.world.claims;

import com.google.gson.*;
import ftb.lib.LMDimUtils;
import latmod.lib.MathHelperLM;
import latmod.lib.util.Pos2I;

public class WorldBorder
{
	public final LMWorldSettings settings;
	public final int dim;
	public final Pos2I pos;
	public int size;
	
	public WorldBorder(LMWorldSettings s, int d)
	{
		settings = s;
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
	
	public static WorldBorder fromJson(LMWorldSettings s, JsonElement e)
	{
		JsonObject a = e.getAsJsonObject();
		WorldBorder wb = new WorldBorder(s, a.get("dim").getAsInt());
		wb.pos.x = a.get("x").getAsInt();
		wb.pos.y = a.get("z").getAsInt();
		wb.size = a.get("size").getAsInt();
		return wb;
	}
	
	public int getSize()
	{
		if(!settings.border_enabled.getAsBoolean()) return 0;
		if(size > 0) size = Math.max(size, 20);
		if(dim == 0) return Math.max(0, size);
		else return (size == -1) ? (int) (settings.worldBorder0.size * LMDimUtils.getWorldScale(dim)) : size;
	}
	
	public void setSize(int s)
	{
		if(settings.world.side.isServer())
		{
			if(size != s)
			{
				size = s;
				settings.world.getServerWorld().update(null);
			}
		}
	}
	
	public void setPos(int x, int z)
	{
		if(settings.world.side.isServer())
		{
			if(pos.x != x || pos.y != z)
			{
				pos.set(x, z);
				settings.world.getServerWorld().update(null);
			}
		}
	}
	
	public boolean isOutside(int cx, int cz)
	{
		if(ClaimedChunks.isInSpawn(dim, cx, cz)) return false;
		int size1 = getSize();
		if(size1 == 0) return false;
		int minX = MathHelperLM.chunk(pos.x - size1);
		int maxX = MathHelperLM.chunk(pos.x + size1);
		int minZ = MathHelperLM.chunk(pos.y - size1);
		int maxZ = MathHelperLM.chunk(pos.y + size1);
		return cx >= maxX || cx <= minX || cz >= maxZ || cz <= minZ;
	}
	
	public boolean isOutsideD(double x, double z)
	{ return isOutside(MathHelperLM.chunk(x), MathHelperLM.chunk(z)); }
}