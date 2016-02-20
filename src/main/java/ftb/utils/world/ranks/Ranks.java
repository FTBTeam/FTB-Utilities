package ftb.utils.world.ranks;

import com.google.gson.*;
import com.mojang.authlib.GameProfile;
import ftb.lib.FTBLib;
import ftb.lib.api.permission.*;
import ftb.utils.mod.config.FTBUConfigGeneral;
import latmod.lib.*;
import net.minecraft.util.EnumChatFormatting;

import java.io.File;
import java.util.*;

public class Ranks implements IPermissionHandler
{
	private static Ranks instance;
	
	public static Ranks instance()
	{
		if(instance == null) instance = new Ranks();
		return instance;
	}
	
	public static final Rank PLAYER = new Rank("Player");
	public static final Rank ADMIN = new Rank("Admin");
	
	private final File file;
	private Rank defaultRank;
	private final HashMap<String, Rank> ranks = new HashMap<>();
	private final HashMap<UUID, Rank> playerMap = new HashMap<>();
	private boolean generatedAllPermsFile = false;
	
	private Ranks()
	{
		file = new File(FTBLib.folderLocal, "ftbu/ranks.json");
		ADMIN.color = EnumChatFormatting.DARK_GREEN;
		PLAYER.color = EnumChatFormatting.WHITE;
	}
	
	public void reload()
	{
		ranks.clear();
		playerMap.clear();
		defaultRank = null;
		
		if(FTBUConfigGeneral.ranks_enabled.get())
		{
			JsonElement e = LMJsonUtils.fromJson(file);
			
			if(e != null && e.isJsonObject())
			{
				JsonObject o = e.getAsJsonObject();
				Map<String, String> parentRanks = new HashMap<>();
				
				for(Map.Entry<String, JsonElement> entry : o.get("ranks").getAsJsonObject().entrySet())
				{
					Rank r = new Rank(entry.getKey());
					JsonObject o1 = entry.getValue().getAsJsonObject();
					r.setJson(o1);
					if(o1.has("parent")) parentRanks.put(r.ID, o1.get("parent").getAsString());
				}
				
				defaultRank = ranks.get(o.get("default_rank").getAsString());
				
				for(Rank r : ranks.values())
				{
					r.parent = ranks.get(parentRanks.get(r.ID));
				}
			}
		}
		
		if(!generatedAllPermsFile)
		{
			generatedAllPermsFile = true;
			
			try
			{
				List<String> list = new ArrayList<>();
				
				list.add("Modifying this file won't do anything, it just shows all available permission IDs");
				list.add("");
				
				List<ForgePermission> sortedPermissions = new ArrayList<>();
				sortedPermissions.addAll(ForgePermissionRegistry.values(null));
				Collections.sort(sortedPermissions);
				
				for(ForgePermission p : sortedPermissions)
				{
					StringBuilder sb = new StringBuilder(" -- ");
					sb.append(p.ID);
					
					if(p.configData.type != null)
					{
						sb.append(" [");
						sb.append(p.configData.type.name().toLowerCase());
						sb.append(']');
					}
					
					sb.append(" -- ");
					
					list.add(sb.toString());
					
					if(p.configData.info != null)
					{
						for(String s : p.configData.info)
							list.add(s);
					}
					
					list.add("");
				}
				
				LMFileUtils.save(new File(FTBLib.folderLocal, "ftbu/all_permissions.txt"), list);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
	public void saveRanks()
	{
		//file.save();
	}
	
	public Rank getRank(String s)
	{
		return ranks.get(s);
	}
	
	public Rank getRankOf(GameProfile profile)
	{
		if(defaultRank != null)
		{
			Rank r = playerMap.get(profile.getId());
			return (r == null) ? defaultRank : r;
		}
		
		return FTBLib.isOP(profile) ? ADMIN : PLAYER;
	}
	
	public void setRank(UUID player, Rank rank)
	{
		if(defaultRank != null)
		{
			playerMap.put(player, rank);
		}
	}
	
	public JsonElement handlePermission(ForgePermission permission, GameProfile profile)
	{ return getRankOf(profile).getJsonElement(permission); }
}