package latmod.ftbu.mod.config;

import cpw.mods.fml.relauncher.Side;
import ftb.lib.api.config.ConfigSyncRegistry;
import latmod.ftbu.api.guide.GuideInfo;
import latmod.lib.FastList;
import latmod.lib.config.*;
import latmod.lib.util.FloatBounds;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;

public class FTBUConfigGeneral
{
	public static final ConfigGroup group = new ConfigGroup("general");
	
	@GuideInfo(info = "If set to true, creative players will be able to access protected chests / chunks", def = "true")
	public static final ConfigEntryBool allowCreativeInteractSecure = new ConfigEntryBool("allowCreativeInteractSecure", true);
	
	@GuideInfo(info = "Server will automatically shut down after X hours. 0 - Disabled, 0.5 - 30 minutes, 1 - 1 Hour, 24 - 1 Day, 168 - 1 Week, 720 - 1 Month", def = "0")
	public static final ConfigEntryFloat restartTimer = new ConfigEntryFloat("restartTimer", new FloatBounds(0F, 0F, 720F));
	
	@GuideInfo(info = "If set to true, explosions and hostile mobs in spawn area will be disabled", def = "false")
	public static final ConfigEntryBool safeSpawn = new ConfigEntryBool("safeSpawn", false);
	
	@GuideInfo(info = "If set to false, players won't be able to attack each other in spawn area", def = "true")
	public static final ConfigEntryBool spawnPVP = new ConfigEntryBool("spawnPVP", true);
	
	@GuideInfo(info = "Entity classes that are banned from world. They will not spawn and existing ones will be destroyed", def = "Blank")
	public static final ConfigEntryStringArray blockedEntities = new ConfigEntryStringArray("blockedEntities", new String[0]);
	
	public static void load(ConfigFile f)
	{
		group.addAll(FTBUConfigGeneral.class);
		f.add(group);
		
		ConfigSyncRegistry.add(allowCreativeInteractSecure);
	}
	
	public static boolean allowCreativeInteractSecure(EntityPlayer ep)
	{ return ep != null && allowCreativeInteractSecure.get() && ep.capabilities.isCreativeMode/* && !(ep instanceof FakePlayer)*/; }
	
	private static final FastList<Class<?>> blockedEntitiesL = new FastList<Class<?>>();
	
	public static void onReloaded(Side side)
	{
		String[] list;
		
		if(side.isServer())
		{
			blockedEntitiesL.clear();
			
			list = blockedEntities.get();
			
			if(list != null && list.length > 0)
			{
				for(String s : list)
				{
					try
					{
						Class<?> c = (Class<?>)EntityList.stringToClassMapping.get(s);
						if(c != null && Entity.class.isAssignableFrom(c))
							blockedEntitiesL.add(c);
					}
					catch(Exception e)
					{ e.printStackTrace(); }
				}
			}
		}
		
		/*
		blockedItemsL.clear();
		
		list = blockedItems.get();
		
		if(list != null && list.length > 0)
		{
			for(String s : list)
			{
				ItemStack is = ItemStackTypeAdapter.parseItem(s);
				if(is != null && !LMInvUtils.isAir(is.getItem())) blockedItemsL.add(is);
			}
		}
		
		FTBUBannedItemsHandler.removeItems(blockedItemsL);
		*/
	}
	
	public static boolean isEntityBanned(Class<?> c)
	{
		for(int i = 0; i < blockedEntitiesL.size(); i++)
		{
			Class<?> c1 = blockedEntitiesL.get(i);
			if(c1.isAssignableFrom(c))
				return true;
		}
		
		return false;
	}
}