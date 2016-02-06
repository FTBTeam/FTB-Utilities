package ftb.utils.mod.config;

import latmod.lib.config.*;
import latmod.lib.util.DoubleBounds;
import net.minecraft.entity.*;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;

public class FTBUConfigGeneral
{
	public static final ConfigEntryDouble restart_timer = new ConfigEntryDouble("restart_timer", new DoubleBounds(0D, 0D, 720D)).setInfo("Server will automatically shut down after X hours\n0 - Disabled\n0.5 - 30 minutes\n1 - 1 Hour\n24 - 1 Day\n168 - 1 Week\n720 - 1 Month");
	public static final ConfigEntryBool safe_spawn = new ConfigEntryBool("safe_spawn", false).setInfo("If set to true, explosions and hostile mobs in spawn area will be disabled");
	public static final ConfigEntryBool spawn_pvp = new ConfigEntryBool("spawn_pvp", true).setInfo("If set to false, playerMap won't be able to attack each other in spawn area");
	private static final ConfigEntryStringArray blocked_entities = new ConfigEntryStringArray("blocked_entities").setInfo("Entity IDs that are banned from world. They will not spawn and existing ones will be destroyed");
	//public static final ConfigEntryBool ranks_enabled = new ConfigEntryBool("ranks_enabled", false).sync().setInfo("Set info ");
	//public static final ConfigEntryBool ranks_override_chat = new ConfigEntryBool("ranks_override_chat", true);
	public static final ConfigEntryBool spawn_area_in_sp = new ConfigEntryBool("spawn_area_in_sp", false).setInfo("Enable spawn area in singleplayer");
	public static final ConfigEntryBool disable_chunkloading = new ConfigEntryBool("disable_chunkloading", false).setInfo("Disabled chunkloading completely");
	
	private static final ArrayList<Class<?>> blockedEntitiesL = new ArrayList<>();
	
	public static void onReloaded(Side side)
	{
		if(side.isServer())
		{
			blockedEntitiesL.clear();
			
			for(String s : blocked_entities.get())
			{
				try
				{
					Class<?> c = (Class<?>) EntityList.stringToClassMapping.get(s);
					if(c != null && Entity.class.isAssignableFrom(c)) blockedEntitiesL.add(c);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		
		/*
		blockedItemsL.removeAll();
		
		list = blockedItems.get();
		
		if(list != null && list.length > 0)
		{
			for(String s : list)
			{
				ItemStack is = ItemStackTypeAdapter.parseItem(s);
				if(is != null && !LMInvUtils.isAir(is.getItem())) blockedItemsL.add(is);
			}
		}
		*/
	}
	
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