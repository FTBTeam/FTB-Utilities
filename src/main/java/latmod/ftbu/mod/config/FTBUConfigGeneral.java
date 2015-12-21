package latmod.ftbu.mod.config;

import cpw.mods.fml.relauncher.Side;
import latmod.ftbu.world.claims.ChunkloaderType;
import latmod.lib.FastList;
import latmod.lib.config.*;
import latmod.lib.util.DoubleBounds;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;

public class FTBUConfigGeneral
{
	public static final ConfigEntryBool allow_creative_interact_secure = new ConfigEntryBool("allow_creative_interact_secure", true).sync().setInfo("If set to true, creative players will be able to access protected chests / chunks");
	public static final ConfigEntryDouble restart_timer = new ConfigEntryDouble("restart_timer", new DoubleBounds(0F, 0F, 720F)).setInfo("Server will automatically shut down after X hours\n0 - Disabled\n0.5 - 30 minutes\n1 - 1 Hour\n24 - 1 Day\n168 - 1 Week\n720 - 1 Month");
	public static final ConfigEntryBool safe_spawn = new ConfigEntryBool("safe_spawn", false).setInfo("If set to true, explosions and hostile mobs in spawn area will be disabled");
	public static final ConfigEntryBool spawn_pvp = new ConfigEntryBool("spawn_pvp", true).setInfo("If set to false, players won't be able to attack each other in spawn area");
	private static final ConfigEntryStringArray blocked_entities = new ConfigEntryStringArray("blocked_entities", new FastList<String>()).setInfo("Entity IDs that are banned from world. They will not spawn and existing ones will be destroyed");
	public static final ConfigEntryEnum<ChunkloaderType> chunkloader_type = new ConfigEntryEnum<ChunkloaderType>("chunkloader_type", ChunkloaderType.class, ChunkloaderType.values(), ChunkloaderType.DISABLED, false).sync().setInfo("disabled - Players won't be able to chunkload\nnormal - Chunks stay loaded when player loggs off\nplayers - Chunks only stay loaded while owner is online");
	//public static final ConfigEntryBool ranks_enabled = new ConfigEntryBool("ranks_enabled", false);
	//public static final ConfigEntryBool ranks_override_chat = new ConfigEntryBool("ranks_override_chat", true);
	public static final ConfigEntryBool sign_warp = new ConfigEntryBool("sign_warp", true).setInfo("Enable right-clicking on '[warp]' signs");
	public static final ConfigEntryBool sign_home = new ConfigEntryBool("sign_home", true).setInfo("Enable right-clicking on '[home]' signs");
	
	public static boolean allowCreativeInteractSecure(EntityPlayer ep)
	{ return ep != null && allow_creative_interact_secure.get() && ep.capabilities.isCreativeMode/* && !(ep instanceof FakePlayer)*/; }
	
	private static final FastList<Class<?>> blockedEntitiesL = new FastList<Class<?>>();
	
	public static void onReloaded(Side side)
	{
		if(side.isServer())
		{
			blockedEntitiesL.clear();
			
			for(String s : blocked_entities.get())
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