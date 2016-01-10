package latmod.ftbu.world;

import com.google.gson.*;
import ftb.lib.LMNBTUtils;
import latmod.lib.ByteIOStream;
import latmod.lib.config.ConfigEntryBool;
import net.minecraft.nbt.*;

import java.util.HashMap;

public class LMWorldSettings
{
	public final LMWorld world;
	public final ConfigEntryBool border_enabled;
	public final WorldBorder worldBorder0;
	public final HashMap<Integer, WorldBorder> worldBorder;
	
	public LMWorldSettings(LMWorld w)
	{
		world = w;
		border_enabled = new ConfigEntryBool("border_enabled", false);
		worldBorder0 = new WorldBorder(this, 0);
		worldBorder0.size = 0;
		worldBorder = new HashMap<>();
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
					WorldBorder wb = new WorldBorder(this, ai[0]);
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
			WorldBorder wb = WorldBorder.fromJson(this, a.get(i));
			
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
		
		for(WorldBorder wb : worldBorder.values())
		{
			if(wb.size != 0 || wb.pos.x != 0 || wb.pos.y != 0) a.add(wb.toJson());
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
			WorldBorder wb = new WorldBorder(this, io.readInt());
			wb.pos.x = io.readInt();
			wb.pos.y = io.readInt();
			wb.size = io.readInt();
			worldBorder.put(Integer.valueOf(wb.dim), wb);
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
	
	public WorldBorder getWB(int dim)
	{
		if(dim == 0) return worldBorder0;
		WorldBorder wb = worldBorder.get(Integer.valueOf(dim));
		
		if(wb == null)
		{
			wb = new WorldBorder(this, dim);
			wb.size = worldBorder0.size;
		}
		
		return wb;
	}
	
	public WorldBorder getAndSet(int dim)
	{
		if(dim == 0) return worldBorder0;
		WorldBorder wb = worldBorder.get(Integer.valueOf(dim));
		
		if(wb == null)
		{
			wb = new WorldBorder(this, dim);
			worldBorder.put(Integer.valueOf(dim), wb);
		}
		
		return wb;
	}
}