package ftb.utils.mod.config;

import ftb.lib.FTBLib;
import ftb.lib.api.config.ConfigEntryBool;
import ftb.lib.api.config.ConfigEntryDouble;
import ftb.lib.api.config.ConfigEntryString;
import ftb.lib.api.config.ConfigEntryStringList;
import latmod.lib.annotations.Info;
import latmod.lib.annotations.NumberBounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FTBUConfigGeneral
{
	@NumberBounds(min = 0D, max = 720D)
	@Info({"Server will automatically shut down after X hours", "0 - Disabled", "0.5 - 30 minutes", "1 - 1 Hour", "24 - 1 Day", "168 - 1 Week", "720 - 1 Month"})
	public static final ConfigEntryDouble restart_timer = new ConfigEntryDouble("restart_timer", 0D);
	
	@Info("If set to true, explosions and hostile mobs in spawn area will be disabled")
	public static final ConfigEntryBool safe_spawn = new ConfigEntryBool("safe_spawn", false);
	
	@Info("If set to false, players won't be able to attack each other in spawn area")
	public static final ConfigEntryBool spawn_pvp = new ConfigEntryBool("spawn_pvp", true);
	
	private static final List<Class<?>> blockedEntitiesL = new ArrayList<>();
	
	//TODO: Replace me with a custom entry
	@Info("Entity IDs that are banned from world. They will not spawn and existing ones will be destroyed")
	private static final ConfigEntryStringList blocked_entities = new ConfigEntryStringList("blocked_entities", null)
	{
		@Override
		public void set(List<String> l)
		{
			super.set(l);
			
			blockedEntitiesL.clear();
			
			for(String s : getAsStringList())
			{
				try
				{
					Class<?> c = (Class<?>) EntityList.stringToClassMapping.get(s);
					if(c != null && Entity.class.isAssignableFrom(c)) blockedEntitiesL.add(c);
				}
				catch(Exception e)
				{
				}
			}
		}
	};
	
	@Info("Enable spawn area in singleplayer")
	public static final ConfigEntryBool spawn_area_in_sp = new ConfigEntryBool("spawn_area_in_sp", false);
	
	public static final ConfigEntryBool server_info_difficulty = new ConfigEntryBool("server_info_difficulty", true);
	
	public static final ConfigEntryBool server_info_mode = new ConfigEntryBool("server_info_mode", true);
	
	public static File guidepacksFolderFile;
	
	private static final ConfigEntryString guidepacks_folder = new ConfigEntryString("guidepacks_folder", "")
	{
		@Override
		public void set(Object o)
		{
			super.set(o);
			guidepacksFolderFile = getAsString().isEmpty() ? new File(FTBLib.folderLocal, "guidepacks") : new File(getAsString());
		}
	};
	
	public static final ConfigEntryStringList blocked_tops = new ConfigEntryStringList("blocked_tops", null);
	
	public static boolean isEntityBanned(Class<?> c)
	{
		for(int i = 0; i < blockedEntitiesL.size(); i++)
		{
			Class<?> c1 = blockedEntitiesL.get(i);
			if(c1.isAssignableFrom(c)) return true;
		}
		
		return false;
	}
}