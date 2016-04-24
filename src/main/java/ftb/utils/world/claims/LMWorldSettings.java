package ftb.utils.world.claims;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ftb.lib.api.config.ConfigEntryBool;
import ftb.utils.world.LMWorld;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

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
	
	public void readFromJson(JsonObject group)
	{
		border_enabled.func_152753_a(group.get(border_enabled.getID()));
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
		group.add(border_enabled.getID(), border_enabled.getSerializableElement());
		
		JsonArray a = new JsonArray();
		
		a.add(worldBorder0.toJson());
		
		for(WorldBorder wb : worldBorder.values())
		{
			if(wb.size != 0 || wb.pos.x != 0 || wb.pos.y != 0) a.add(wb.toJson());
		}
		
		group.add("world_border", a);
	}
	
	public void readFromNet(NBTTagCompound tag)
	{
		NBTTagCompound tag1 = tag.getCompoundTag("WB");
		border_enabled.set(tag.getBoolean("E"));
		if(!border_enabled.getAsBoolean()) return;
		
		worldBorder.clear();
		
		NBTTagList list = tag1.getTagList("L", Constants.NBT.TAG_INT_ARRAY);
		
		for(int i = 0; i < list.tagCount(); i++)
		{
			int[] ai = list.func_150306_c(i);
			
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
				worldBorder.put(wb.dim, wb);
			}
		}
	}
	
	public void writeToNet(NBTTagCompound tag)
	{
		NBTTagCompound tag1 = new NBTTagCompound();
		
		tag1.setBoolean("E", border_enabled.getAsBoolean());
		
		if(border_enabled.getAsBoolean())
		{
			NBTTagList list = new NBTTagList();
			
			list.appendTag(new NBTTagIntArray(new int[] {0, worldBorder0.pos.x, worldBorder0.pos.y, worldBorder0.size}));
			
			for(WorldBorder wb : worldBorder.values())
			{
				list.appendTag(new NBTTagIntArray(new int[] {wb.dim, wb.pos.x, wb.pos.y, wb.size}));
			}
			
			tag1.setTag("L", list);
		}
		
		tag.setTag("WB", tag1);
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