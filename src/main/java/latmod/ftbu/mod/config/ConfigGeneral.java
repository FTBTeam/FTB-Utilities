package latmod.ftbu.mod.config;

import java.io.File;

import com.google.gson.annotations.Expose;

import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.api.readme.*;
import latmod.ftbu.core.util.LMJsonUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.FakePlayer;

public class ConfigGeneral
{
	private static File saveFile;
	
	@Expose public Boolean allowCreativeInteractSecure;
	@Expose public String commandFTBU;
	@Expose public String commandAdmin;
	@Expose public Float restartTimer;
	@Expose public Boolean safeSpawn;
	@Expose public Boolean spawnPVP;
	@Expose public Boolean enableDedicatedOnSP;
	@Expose public Integer maxClaims;
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

	public static void saveReadme(ReadmeFile file)
	{
		ReadmeCategory general = file.get("latmod/ftbu/general.txt");
		general.add("allowCreativeInteractSecure", "If set to true, creative players will be able to access protected chests / chunks.", true);
		general.add("commandFTBU", "Command name for ftbu command.", "ftbu");
		general.add("commandAdmin", "Command name for ftbu command.", "admin");
		general.add("restartTimer", "Server will automatically shut down after X hours. 0 - Disabled, 0.5 - 30 minutes, 1 - 1 Hour, 24 - 1 Day, 168 - 1 Week, 720 - 1 Month, etc.", 0);
		general.add("safeSpawn", "If set to true, explosions and hostile mobs in spawn area will be disabled.", false);
		general.add("spawnPVP", "If set to false, players won't be able to attack each other in spawn area.", true);
		general.add("enableDedicatedOnSP", "Enables server-only features on singleplayer / LAN worlds.", false);
		general.add("maxClaims", "Max amount of chunks that player can claim. EnkiTools mod overrides this. 0 - Disabled, recommended: 16. ", 0);
	}
}