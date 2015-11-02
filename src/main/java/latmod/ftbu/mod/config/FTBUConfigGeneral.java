package latmod.ftbu.mod.config;

import cpw.mods.fml.relauncher.Side;
import ftb.lib.api.config.ConfigSyncRegistry;
import latmod.ftbu.api.guide.GuideInfo;
import latmod.lib.FastList;
import latmod.lib.config.*;
import latmod.lib.util.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;

public class FTBUConfigGeneral
{
	public static final ConfigGroup group = new ConfigGroup("general");
	
	@GuideInfo(info = "If set to true, creative players will be able to access protected chests / chunks.", def = "true")
	public static final ConfigEntryBool allowCreativeInteractSecure = new ConfigEntryBool("allowCreativeInteractSecure", true);
	
	@GuideInfo(info = "Server will automatically shut down after X hours. 0 - Disabled, 0.5 - 30 minutes, 1 - 1 Hour, 24 - 1 Day, 168 - 1 Week, 720 - 1 Month.", def = "0")
	public static final ConfigEntryFloat restartTimer = new ConfigEntryFloat("restartTimer", new FloatBounds(0F, 0F, 720F));
	
	@GuideInfo(info = "If set to true, explosions and hostile mobs in spawn area will be disabled.", def = "false")
	public static final ConfigEntryBool safeSpawn = new ConfigEntryBool("safeSpawn", false);
	
	@GuideInfo(info = "If set to false, players won't be able to attack each other in spawn area.", def = "true")
	public static final ConfigEntryBool spawnPVP = new ConfigEntryBool("spawnPVP", true);
	
	//@GuideInfo(info = "Right clicking during daytime sets bed spawn.", def = "true")
	//public static final ConfigEntryBool daytimeBedSpawn = new ConfigEntryBool("daytimeBedSpawn", true);
	
	@GuideInfo(info = "Entity classes that are banned from world. They will not spawn and existing ones will be destroyed.", def = "Blank")
	public static final ConfigEntryStringArray blockedEntities = new ConfigEntryStringArray("blockedEntities", new String[0]);
	
	@GuideInfo(info = "Add config info to Guide.", def = "false")
	public static final ConfigEntryBool configInfoGuide = new ConfigEntryBool("configInfoGuide", false);
	
	@GuideInfo(info = "0 - Disabled, 1 - All enabled, 2 - Only text enabled (no items)", def = "1")
	public static final ConfigEntryInt mail = new ConfigEntryInt("configInfoGuide", new IntBounds(1, 0, 2));
	
	//@GuideInfo(info = "Items that will be banned (recipe removed).", def = "Blank")
	//public static final ConfigEntryStringArray blockedItems = new ConfigEntryStringArray("blockedItems", new String[0]);
	
	public static void load(ConfigFile f)
	{
		group.addAll(FTBUConfigGeneral.class);
		f.add(group);
		
		ConfigSyncRegistry.add(allowCreativeInteractSecure);
		ConfigSyncRegistry.add(mail);
		//ConfigSyncRegistry.add(blockedItems);
	}
	
	public static boolean allowCreativeInteractSecure(EntityPlayer ep)
	{ return ep != null && allowCreativeInteractSecure.get() && ep.capabilities.isCreativeMode/* && !(ep instanceof FakePlayer)*/; }
	
	private static final FastList<Class<?>> blockedEntitiesL = new FastList<Class<?>>();
	//private static final FastList<ItemStack> blockedItemsL = new FastList<ItemStack>();
	
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
	
	public static boolean isItemBanned(ItemStack is)
	{ return is != null && isItemBanned(is.getItem(), is.getItemDamage()); }
	
	public static boolean isItemBanned(Item item, int dmg)
	{
		return false;
		/*
		if(item == null) return false;
		
		for(int i = 0; i < blockedItemsL.size(); i++)
		{
			ItemStack is = blockedItemsL.get(i);
			if((is.getItemDamage() == dmg || is.getItemDamage() == -1) && is.getItem() == item)
				return true;
		}
		
		return false;
		*/
	}
}