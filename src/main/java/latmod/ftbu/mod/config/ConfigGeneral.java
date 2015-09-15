package latmod.ftbu.mod.config;

import java.io.File;

import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.api.readme.ReadmeInfo;
import latmod.ftbu.core.util.LMJsonUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.FakePlayer;

public class ConfigGeneral
{
	private static transient File saveFile;
	
	@ReadmeInfo(info = "If set to true, creative players will be able to access protected chests / chunks.", def = "true")
	public Boolean allowCreativeInteractSecure;
	
	@ReadmeInfo(info = "Command name for ftbu command.", def = "ftbu")
	public String commandFTBU;
	
	@ReadmeInfo(info = "Command name for admin command.", def = "admin")
	public String commandAdmin;
	
	@ReadmeInfo(info = "Server will automatically shut down after X hours. 0 - Disabled, 0.5 - 30 minutes, 1 - 1 Hour, 24 - 1 Day, 168 - 1 Week, 720 - 1 Month, etc.", def = "0")
	public Float restartTimer;
	
	@ReadmeInfo(info = "If set to true, explosions and hostile mobs in spawn area will be disabled.", def = "false")
	public Boolean safeSpawn;
	
	@ReadmeInfo(info = "If set to false, players won't be able to attack each other in spawn area.", def = "true")
	public Boolean spawnPVP;
	
	@ReadmeInfo(info = "Enables server-only features on singleplayer / LAN worlds.", def = "false")
	public Boolean enableDedicatedOnSP;
	
	@ReadmeInfo(info = "Max amount of chunks that player can claim. EnkiTools mod overrides this. 0 - Disabled, recommended: 16. ", def = "0")
	public Integer maxClaims;
	
	//@Expose public String[] spawnBreakWhitelist;
	//@Expose public String[] spawnInteractWhitelist;
	//@Expose public String[] placementBlacklist;
	
	public static void load()
	{
		saveFile = new File(LatCoreMC.latmodFolder, "ftbu/general.txt");
		FTBUConfig.general = LMJsonUtils.fromJsonFile(saveFile, ConfigGeneral.class);
		if(FTBUConfig.general == null) FTBUConfig.general = new ConfigGeneral();
		FTBUConfig.general.loadDefaults();
		save();
	}
	
	public void loadDefaults()
	{
		if(allowCreativeInteractSecure == null) allowCreativeInteractSecure = true;
		if(commandFTBU == null) commandFTBU = "ftbu";
		if(commandAdmin == null) commandAdmin = "admin";
		if(restartTimer == null) restartTimer = 0F;
		if(safeSpawn == null) safeSpawn = false;
		if(spawnPVP == null) spawnPVP = true;
		if(enableDedicatedOnSP == null) enableDedicatedOnSP = false;
		if(maxClaims == null) maxClaims = 0;
		
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
	
	public boolean allowInteractSecure(EntityPlayer ep)
	{ return allowCreativeInteractSecure || (ep != null && !(ep instanceof FakePlayer) && ep.capabilities.isCreativeMode); }
	
	public boolean isDedi()
	{ return enableDedicatedOnSP || LatCoreMC.isDedicatedServer(); }
	
	public static void save()
	{
		if(FTBUConfig.general == null) load();
		if(!LMJsonUtils.toJsonFile(saveFile, FTBUConfig.general))
			LatCoreMC.logger.warn(saveFile.getName() + " failed to save!");
	}
}