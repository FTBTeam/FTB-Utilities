package latmod.ftbu.world;

import com.google.gson.*;

import ftb.lib.*;
import latmod.ftbu.world.claims.ClaimedChunks;
import latmod.lib.*;
import latmod.lib.config.ConfigEntryBool;
import latmod.lib.util.Pos2I;
import net.minecraft.nbt.*;

public class LMWorldSettings
{
	public final LMWorld world;
	public final ConfigEntryBool border_enabled;
	private final WorldBorder worldBorder0;
	private final FastMap<Integer, WorldBorder> worldBorder;
	
	public LMWorldSettings(LMWorld w)
	{
		world = w;
		
		border_enabled = new ConfigEntryBool("border_enabled", false)
		{
			public void set(boolean v)
			{
				if(get() != v && world.side.isServer())
				{
					super.set(v);
					world.update();
				}
			}
		};
		
		worldBorder0 = new WorldBorder(0);
		worldBorder0.size = 0;
		worldBorder = new FastMap<Integer, WorldBorder>();
	}
	
	public void readFromNBT(NBTTagCompound tag)
	{
		border_enabled.set(tag.getBoolean("WB_Enabled"));
		worldBorder.clear();
		worldBorder0.size = 0;
		
		NBTTagList wbList = tag.getTagList("WBorder", LMNBTUtils.INT_ARRAY);
		
		for(int i = 0; i < wbList.tagCount(); i++)
		{
			int[] ai = wbList.func_150306_c(i);
			
			if(ai.length >= 4)
			{
				if(ai[0] == 0)
				{
					worldBorder0.pos.x = ai[1];
					worldBorder0.pos.y = ai[2];
					worldBorder0.size = ai[3];
				}
				else
				{
					WorldBorder wb = new WorldBorder(ai[0]);
					wb.pos.x = ai[1];
					wb.pos.y = ai[2];
					wb.size = ai[3];
					worldBorder.put(Integer.valueOf(ai[0]), wb);
				}
			}
		}
	}
	
	public void readFromJson(JsonObject group)
	{
		border_enabled.setJson(group.get(border_enabled.ID));
		worldBorder.clear();
		worldBorder0.size = 0;
		
		JsonArray a = group.get("world_border").getAsJsonArray();
		
		for(int i = 0; i < a.size(); i++)
		{
			WorldBorder wb = WorldBorder.fromJson(a.get(i));
			
			if(wb.dim == 0)
			{
				worldBorder0.pos.x = wb.pos.x;
				worldBorder0.pos.y = wb.pos.y;
				worldBorder0.size = wb.size;
			}
			else worldBorder.put(Integer.valueOf(wb.dim), wb);
		}
	}
	
	public void writeToJson(JsonObject group)
	{
		group.add(border_enabled.ID, border_enabled.getJson());
		
		JsonArray a = new JsonArray();
		
		a.add(worldBorder0.toJson());
		
		for(WorldBorder wb : worldBorder)
		{
			if(wb.size != 0 || wb.pos.x != 0 || wb.pos.y != 0)
				a.add(wb.toJson());
		}
		
		group.add("world_border", a);
	}
	
	public void readFromNet(ByteIOStream io)
	{
		border_enabled.set(io.readBoolean());
		
		worldBorder0.pos.x = io.readInt();
		worldBorder0.pos.y = io.readInt();
		worldBorder0.size = io.readInt();
		
		int s = io.readInt();
		worldBorder.clear();
		for(int i = 0; i < s; i++)
		{
			WorldBorder wb = new WorldBorder(io.readInt());
			wb.pos.x = io.readInt();
			wb.pos.y = io.readInt();
			wb.size = io.readInt();
		}
	}
	
	public void writeToNet(ByteIOStream io)
	{
		io.writeBoolean(border_enabled.get());
		
		io.writeInt(worldBorder0.pos.x);
		io.writeInt(worldBorder0.pos.y);
		io.writeInt(worldBorder0.size);
		
		io.writeInt(worldBorder.size());
		
		for(WorldBorder wb : worldBorder.values())
		{
			io.writeInt(wb.dim);
			io.writeInt(wb.pos.x);
			io.writeInt(wb.pos.y);
			io.writeInt(wb.size);
		}
	}
	
	public WorldBorder get(int dim)
	{
		if(dim == 0) return worldBorder0;
		WorldBorder wb = worldBorder.get(Integer.valueOf(dim));
		if(wb == null) worldBorder.put(Integer.valueOf(dim), wb = new WorldBorder(dim));
		return wb;
	}
	
	public int getBorderSize(int dim)
	{
		if(!border_enabled.get()) return 0;
		else if(dim == 0) return worldBorder0.size;
		else
		{
			WorldBorder b = get(dim);
			if(b.size > -1) return b.size;
			return (int)(worldBorder0.size * LMDimUtils.getWorldScale(dim));
		}
	}
	
	public Pos2I getBorderPos(int dim)
	{
		WorldBorder b = get(dim);
		return (b == null) ? new Pos2I(0, 0) : b.pos;
	}
	
	public void setBorderSize(int dim, int s)
	{
		if(!world.side.isServer()) return;
		
		WorldBorder wb = get(dim);
		if(wb.size != s)
		{
			wb.size = s;
			world.update();
		}
	}
	
	public void setBorderPos(int dim, int x, int z)
	{
		if(!world.side.isServer()) return;
		
		WorldBorder wb = get(dim);
		if(wb.pos.x != x || wb.pos.y != z)
		{
			wb.pos.set(x, z);
			world.update();
		}
	}
	
	public boolean isOutsideBorder(int dim, int cx, int cz)
	{
		if(ClaimedChunks.isInSpawn(dim, cx, cz)) return false;
		WorldBorder wb = get(dim);
		int size = getBorderSize(dim);
		if(size == 0) return false;
		int minX = MathHelperLM.chunk(wb.pos.x - size);
		int maxX = MathHelperLM.chunk(wb.pos.x + size);
		int minZ = MathHelperLM.chunk(wb.pos.y - size);
		int maxZ = MathHelperLM.chunk(wb.pos.y + size);
		return cx >= maxX || cx <= minX || cz >= maxZ || cz <= minZ;
	}
	
	public boolean isOutsideBorderD(int dim, double x, double z)
	{ return isOutsideBorder(dim, MathHelperLM.chunk(x), MathHelperLM.chunk(z)); }

	public boolean isEnabled(int dim)
	{ return getBorderSize(dim) > 0; }
}