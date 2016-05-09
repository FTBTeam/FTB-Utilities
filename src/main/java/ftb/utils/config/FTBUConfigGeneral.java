package ftb.utils.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import ftb.lib.api.config.ConfigEntryBool;
import ftb.lib.api.config.ConfigEntryCustom;
import ftb.lib.api.config.ConfigEntryDouble;
import latmod.lib.annotations.Info;
import latmod.lib.annotations.NumberBounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;

import java.util.ArrayList;
import java.util.List;

public class FTBUConfigGeneral
{
	@NumberBounds(min = 0, max = 720)
	@Info({"Server will automatically shut down after X hours", "0 - Disabled", "0.5 - 30 minutes", "1 - 1 Hour", "24 - 1 Day", "168 - 1 Week", "720 - 1 Month"})
	public static final ConfigEntryDouble restart_timer = new ConfigEntryDouble("restart_timer", 0D);
	
	@Info("If set to true, explosions and hostile mobs in spawn area will be disabled")
	public static final ConfigEntryBool safe_spawn = new ConfigEntryBool("safe_spawn", false);
	
	@Info("If set to false, playerMap won't be able to attack each other in spawn area")
	public static final ConfigEntryBool spawn_pvp = new ConfigEntryBool("spawn_pvp", true);
	
	@Info("Entity IDs that are banned from world. They will not spawn and existing ones will be destroyed")
	public static final ConfigEntryBannedEntityList blocked_entities = new ConfigEntryBannedEntityList("blocked_entities");
	
	public static final ConfigEntryBool ranks_enabled = new ConfigEntryBool("ranks_enabled", false);
	//public static final ConfigEntryBool ranks_override_chat = new ConfigEntryBool("ranks_override_chat", true);
	
	//public static final ConfigEntryBool ranks_override_commands = new ConfigEntryBool("ranks_override_commands", true);
	
	@Info("Enable spawn area in singleplayer")
	public static final ConfigEntryBool spawn_area_in_sp = new ConfigEntryBool("spawn_area_in_sp", false);
	
	public static final ConfigEntryBool server_info_difficulty = new ConfigEntryBool("server_info_difficulty", true);
	public static final ConfigEntryBool server_info_mode = new ConfigEntryBool("server_info_mode", true);
	
	public static class ConfigEntryBannedEntityList extends ConfigEntryCustom
	{
		public final List<Class<?>> list;
		
		public ConfigEntryBannedEntityList(String id)
		{
			super(id);
			list = new ArrayList<>();
		}
		
		@Override
		public void fromJson(JsonElement o)
		{
			list.clear();
			
			if(o.isJsonArray())
			{
				for(JsonElement e : o.getAsJsonArray())
				{
					Class<?> c = EntityList.NAME_TO_CLASS.get(e.getAsString());
					if(c != null && Entity.class.isAssignableFrom(c))
					{
						list.add(c);
					}
				}
			}
		}
		
		@Override
		public JsonElement getSerializableElement()
		{
			JsonArray a = new JsonArray();
			
			for(Class<?> c1 : list)
			{
				String s = EntityList.CLASS_TO_NAME.get(c1);
				
				if(s != null)
				{
					a.add(new JsonPrimitive(s));
				}
			}
			
			return a;
		}
		
		public boolean isEntityBanned(Class<?> c)
		{
			for(Class<?> c1 : list)
			{
				if(c1.isAssignableFrom(c)) { return true; }
			}
			
			return false;
		}
	}
}