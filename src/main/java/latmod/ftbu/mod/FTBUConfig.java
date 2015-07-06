package latmod.ftbu.mod;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

import latmod.ftbu.core.*;
import latmod.ftbu.core.event.FTBUReadmeEvent;
import latmod.ftbu.core.util.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.google.gson.annotations.Expose;

public class FTBUConfig implements IServerConfig // FTBU
{
	public static final FTBUConfig instance = new FTBUConfig();
	
	public String getConfigName()
	{ return FTBU.mod.modID; }
	
	public void load()
	{
		General.load();
		Login.load();
		WorldBorder.load();
		Backups.load();
		
		int overrides = loadOverrides();
		LatCoreMC.logger.info("Config loaded with " + overrides + " overrides");
		
		saveAll();
	}
	
	public void readConfig(NBTTagCompound tag)
	{
		Login.inst.customBadges = tag.getString("CB");
	}
	
	public void writeConfig(NBTTagCompound tag)
	{
		if(!Login.inst.customBadges.isEmpty())
			tag.setString("CB", Login.inst.customBadges);
	}
	
	public static void saveAll()
	{
		General.save();
		Login.save();
		WorldBorder.save();
		Backups.save();
	}
	
	// Categories //
	
	public static class General
	{
		public static General inst;
		private static File saveFile;
		
		@Expose private Boolean allowCreativeInteractSecure;
		@Expose public String commandFTBU;
		@Expose public String commandAdmin;
		@Expose public Float restartTimer;
		@Expose public Boolean safeSpawn;
		@Expose public Boolean spawnPVP;
		@Expose public Boolean enableDedicatedOnSP;
		
		public static void load()
		{
			saveFile = new File(LatCoreMC.latmodFolder, "ftbu/general.txt");
			inst = LatCore.fromJsonFromFile(saveFile, General.class);
			if(inst == null) inst = new General();
			inst.loadDefaults();
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
		}
		
		public static void save()
		{
			if(inst == null) load();
			if(!LatCore.toJsonFile(saveFile, inst))
				LatCoreMC.logger.warn(saveFile.getName() + " failed to save!");
		}
	}
	
	public static boolean allowInteractSecure(EntityPlayer ep)
	{ return General.inst.allowCreativeInteractSecure && ep.capabilities.isCreativeMode; }
	
	public static boolean isDedi()
	{ return General.inst.enableDedicatedOnSP || LatCoreMC.isDedicatedServer(); }
	
	public static class Login
	{
		public static Login inst;
		private static File saveFile;
		
		@Expose public String[] motd;
		@Expose public String rules;
		@Expose public String customBadges;
		@Expose private String[] startingItems;
		private final FastList<ItemStack> startingItemsList = new FastList<ItemStack>();
		
		public static void load()
		{
			saveFile = new File(LatCoreMC.latmodFolder, "ftbu/login.txt");
			inst = LatCore.fromJsonFromFile(saveFile, Login.class);
			if(inst == null) inst = new Login();
			inst.loadDefaults();
			save();
		}
		
		public void loadDefaults()
		{
			if(motd == null) motd = new String[] { "Welcome to the server!" };
			if(rules == null) rules = "";
			if(customBadges == null) customBadges = "";
			if(startingItems == null) startingItems = new String[] { "minecraft:apple 16 0" };
			
			startingItemsList.clear();
			for(String s : startingItems)
			{
				ItemStack is = InvUtils.parseItem(s);
				if(is != null && is.stackSize > 0)
					startingItemsList.add(is);
			}
		}
		
		public static void save()
		{
			if(inst == null) load();
			if(!LatCore.toJsonFile(saveFile, inst))
				LatCoreMC.logger.warn(saveFile.getName() + " failed to save!");
		}

		public static FastList<ItemStack> getStartingItems(UUID id)
		{
			return inst.startingItemsList;
		}
	}
	
	public static class WorldBorder
	{
		public static WorldBorder inst;
		private static File saveFile;
		
		@Expose public Boolean enabled;
		@Expose private Integer radius;
		@Expose private Map<Integer, Integer> custom;
		
		public static void load()
		{
			saveFile = new File(LatCoreMC.latmodFolder, "ftbu/world_border.txt");
			inst = LatCore.fromJsonFromFile(saveFile, WorldBorder.class);
			if(inst == null) inst = new WorldBorder();
			inst.loadDefaults();
			save();
		}
		
		public void loadDefaults()
		{
			if(enabled == null) enabled = false;
			if(radius == null) radius = 10000;
			if(custom == null) custom = new HashMap<Integer, Integer>();
		}
		
		public static void save()
		{
			if(inst == null) load();
			if(!LatCore.toJsonFile(saveFile, inst))
				LatCoreMC.logger.warn(saveFile.getName() + " failed to save!");
		}
		
		public void setWorldBorder(int dim, int rad)
		{
			if(dim == 0) radius = rad;
			else custom.put(dim, rad);
		}
		
		public int getWorldBorder(int dim)
		{
			if(!enabled) return 0;
			if(dim == 0) return radius;
			
			if(custom != null && !custom.isEmpty())
			{
				Integer i = custom.get(dim);
				if(i != null) return i.intValue();
			}
			
			return (int)(radius * LatCoreMC.getWorldScale(dim));
		}
	}
	
	public static class Backups
	{
		public static Backups inst;
		private static File saveFile;
		
		@Expose public Boolean enabled;
		@Expose public Float backupTimer;
		@Expose public Integer backupsToKeep;
		@Expose public Boolean onStartup;
		@Expose public Boolean onShutdown;
		@Expose public Boolean compress;
		
		public static void load()
		{
			saveFile = new File(LatCoreMC.latmodFolder, "ftbu/backups.txt");
			inst = LatCore.fromJsonFromFile(saveFile, Backups.class);
			if(inst == null) inst = new Backups();
			inst.loadDefaults();
			save();
		}
		
		public void loadDefaults()
		{
			if(enabled == null) enabled = false;
			if(backupTimer == null) backupTimer = 2F;
			if(backupsToKeep == null) backupsToKeep = 20;
			if(onStartup == null) onStartup = false;
			if(onShutdown == null) onShutdown = false;
			if(compress == null) compress = false;
		}
		
		public static void save()
		{
			if(inst == null) load();
			if(!LatCore.toJsonFile(saveFile, inst))
				LatCoreMC.logger.warn(saveFile.getName() + " failed to save!");
		}
	}
	
	// Other //
	
	public static void saveReadme() throws Exception
	{
		FTBUReadmeEvent e = new FTBUReadmeEvent();
		
		FTBUReadmeEvent.ReadmeFile.Category general = e.file.get("latmod/ftbu/general.txt");
		general.add("allowCreativeInteractSecure", "If set to true, creative players will be able to access protected chests / chunks.", true);
		general.add("commandFTBU", "Command name for ftbu command.", "ftbu");
		general.add("commandAdmin", "Command name for ftbu command.", "admin");
		general.add("restartTimer", "Server will automatically shut down after X hours. 0 - Disabled, 0.5 - 30 minutes, 1 - 1 Hour, 24 - 1 Day, 168 - 1 Week, 720 - 1 Month, etc.", 0);
		general.add("safeSpawn", "If set to true, explosions and hostile mobs in spawn area will be disabled.", false);
		general.add("spawnPVP", "If set to false, players won't be able to attack each other in spawn area.", true);
		general.add("enableDedicatedOnSP", "Enables server-only features on singleplayer / LAN worlds.", false);
		
		FTBUReadmeEvent.ReadmeFile.Category login = e.file.get("latmod/ftbu/login.txt");
		login.add("motd", "Message of the day. This will be displayed when player joins the server.", "Blank");
		login.add("rules", "Rules link you can click on. This will be displayed when player joins the server.", "Blank");
		login.add("customBadges", "URL for per-server custom badges file (Json). Example can be seen here: http://pastebin.com/LvBB9HmV ", "Blank");
		login.add("startingItems", "Items to give player when it first joins the server. Format: StringID Size Metadata, does not support NBT yet.", "minecraft:apple 16 0");
		
		FTBUReadmeEvent.ReadmeFile.Category world_border = e.file.get("latmod/ftbu/world_border.txt");
		world_border.add("enabled", "true enables world border, false disables.", false);
		world_border.add("radius", "Radius of the world border, starting at 0, 0. Its a square, so radius 5000 = 10000x10000 blocks of available world.", 5000);
		world_border.add("custom", "'DimensionID:Size' map. Example: \"custom\": { \"7\":1000, \"27\":3000 }", "Blank");
		
		FTBUReadmeEvent.ReadmeFile.Category backups = e.file.get("latmod/ftbu/backups.txt");
		backups.add("enabled", "true enables backups, false disabled.", false);
		backups.add("backupTimer", "Timer in hours. Can be .x, 1.0 - backups every hour, 6.0 - backups every 6 hours, 0.5 - backups every 30 minutes.", 2F);
		backups.add("backupsToKeep", "The number of backup files to keep. More backups = more space used.", 20);
		backups.add("onStartup", "Launches backup when server starts.", false);
		backups.add("onShutdown", "Launches backup when server stops.", false);
		backups.add("compress", "true to compress into .zip, false to backup as folders. true = less space used, false = faster to backup.", false);
		
		e.post();
		
		StringBuilder sb = new StringBuilder();
		
		for(int j = 0; j < e.file.map.size(); j++)
		{
			FTBUReadmeEvent.ReadmeFile.Category c = e.file.map.values.get(j);
			
			sb.append('[');
			sb.append(c.name);
			sb.append(']');
			sb.append('\n');
			
			for(int i = 0; i < c.lines.size(); i++)
			{
				sb.append(c.lines.keys.get(i));
				sb.append(" - ");
				sb.append(c.lines.values.get(i));
				sb.append('\n');
			}
			
			sb.append('\n');
		}
		
		LatCore.saveFile(new File(LatCoreMC.latmodFolder, "readme.txt"), sb.toString().trim());
	}
	
	private static class Overrides
	{
		@Expose public Map<String, Map<String, Object>> overrides;
		
		private static Object getFromFile(String s)
		{
			if(s.equals("general")) return General.inst;
			else if(s.equals("login")) return Login.inst;
			else if(s.equals("world_border")) return WorldBorder.inst;
			else if(s.equals("backups")) return Backups.inst;
			else return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static int loadOverrides()
	{
		int loaded = 0;
		
		try
		{
			File f = new File(LatCoreMC.configFolder, "LatMod/FTBU_Overrides.txt");
			Overrides overrides = LatCore.fromJsonFromFile(f, Overrides.class);
			if(overrides == null || overrides.overrides == null)
			{
				overrides = new Overrides();
				overrides.overrides = new HashMap<String, Map<String, Object>>();
				LatCore.toJsonFile(f, overrides);
				return 0;
			}
			
			if(overrides.overrides.isEmpty()) return 0;
			
			for(String cat : overrides.overrides.keySet())
			{
				Object c = Overrides.getFromFile(cat);
				
				if(c != null)
				{
					Map<String, Object> m = overrides.overrides.get(cat);
					
					if(m != null && !m.isEmpty())
					{
						for(String k : m.keySet())
						{
							Object v = m.get(k);
							
							if(v != null)
							{
								try
								{
									Field kf = c.getClass().getField(k);
									kf.setAccessible(true);
									
									Object v0 = kf.get(c);
									
									if(v0.getClass() == Float.class && v.getClass() == Double.class)
										v = ((Double)v).floatValue();
									
									if(List.class.isAssignableFrom(v.getClass()))
									{
										if(v0.getClass() == (new String[0]).getClass())
											v = ((List<String>)v).toArray(new String[0]);
									}
									
									kf.set(c, v);
									LatCoreMC.logger.info("Replaced " + v0 + " in '" + cat + "." + k + "' with " + v);
									loaded++;
								}
								catch(Exception e)
								{ e.printStackTrace(); }
							}
						}
					}
				}
			}
		}
		catch(Exception e)
		{ e.printStackTrace(); }
		
		return loaded;
	}
}