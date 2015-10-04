package latmod.ftbu.mod.config;

import latmod.ftbu.api.config.*;
import latmod.ftbu.api.readme.ReadmeInfo;
import latmod.ftbu.util.LatCoreMC;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.FakePlayer;

public class FTBUConfigGeneral
{
	public static final ConfigGroup group = new ConfigGroup("general");
	
	@ReadmeInfo(info = "If set to true, creative players will be able to access protected chests / chunks.", def = "true")
	public static final ConfigEntryBool allowCreativeInteractSecure = new ConfigEntryBool("allowCreativeInteractSecure", true).setSyncWithClient();
	
	@ReadmeInfo(info = "Command name for ftbu command.", def = "ftbu")
	public static final ConfigEntryString commandFTBU = new ConfigEntryString("commandFTBU", "ftbu");
	
	@ReadmeInfo(info = "Command name for admin command.", def = "admin")
	public static final ConfigEntryString commandAdmin = new ConfigEntryString("commandAdmin", "admin");
	
	@ReadmeInfo(info = "Server will automatically shut down after X hours. 0 - Disabled, 0.5 - 30 minutes, 1 - 1 Hour, 24 - 1 Day, 168 - 1 Week, 720 - 1 Month.", def = "0")
	public static final ConfigEntryFloat restartTimer = new ConfigEntryFloat("restartTimer", new FloatBounds(0F, 0F, 720F));
	
	@ReadmeInfo(info = "If set to true, explosions and hostile mobs in spawn area will be disabled.", def = "false")
	public static final ConfigEntryBool safeSpawn = new ConfigEntryBool("safeSpawn", false);
	
	@ReadmeInfo(info = "If set to false, players won't be able to attack each other in spawn area.", def = "true")
	public static final ConfigEntryBool spawnPVP = new ConfigEntryBool("spawnPVP", true);
	
	@ReadmeInfo(info = "Enables server-only features on singleplayer / LAN worlds.", def = "false")
	public static final ConfigEntryBool enableDedicatedOnSP = new ConfigEntryBool("enableDedicatedOnSP", false);
	
	@ReadmeInfo(info = "Max amount of chunks that player can claim. EnkiTools mod overrides this. 0 - Disabled, recommended: 25. ", def = "0")
	public static final ConfigEntryInt maxClaims = new ConfigEntryInt("maxClaims", new IntBounds(0, -1, 16000));
	
	//public String[] spawnBreakWhitelist;
	//public String[] spawnInteractWhitelist;
	//public String[] placementBlacklist;
	
	public static void load(ConfigFile f)
	{
		group.add(allowCreativeInteractSecure);
		group.add(commandFTBU);
		group.add(commandAdmin);
		group.add(restartTimer);
		group.add(safeSpawn);
		group.add(spawnPVP);
		group.add(enableDedicatedOnSP);
		group.add(maxClaims);
		f.add(group);
		
		/*
		if(spawnBreakWhitelist == null) spawnBreakWhitelist = new String[]
		{
				"OpenBlocks:grave"
		};
		
		if(spawnInteractWhitelist == null) spawnInteractWhitelist = new String[]
		{
				"minecraft:furnace",
				"minecraft:crafting_table",
				"minecraft:sign",
				"minecraft:door",
				"Natura:BerryBush",
				"IC2:blockPersonal",
				"Mystcraft:BlockBookstand",
		};
		
		if(placementBlacklist == null) placementBlacklist = new String[]
		{
		};
		*/
	}
	
	public static boolean allowInteractSecure(EntityPlayer ep)
	{ return allowCreativeInteractSecure.get() || (ep != null && !(ep instanceof FakePlayer) && ep.capabilities.isCreativeMode); }
	
	public static boolean isDedi()
	{ return enableDedicatedOnSP.get() || LatCoreMC.isDedicatedServer(); }
}