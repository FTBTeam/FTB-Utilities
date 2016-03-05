package ftb.utils.ranks;

import com.google.gson.*;
import ftb.lib.api.permissions.*;
import latmod.lib.json.IJsonObject;
import latmod.lib.util.FinalIDObject;
import net.minecraft.command.*;
import net.minecraft.util.EnumChatFormatting;

import java.util.*;

public final class Rank extends FinalIDObject implements IJsonObject
{
	public Rank parent = null;
	public EnumChatFormatting color = EnumChatFormatting.WHITE;
	public String prefix = "";
	public String badge = "";
	public final Map<String, Boolean> permissions;
	public final Map<RankConfig, JsonElement> config;
	
	public Rank(String id)
	{
		super(id);
		permissions = new LinkedHashMap<>();
		config = new LinkedHashMap<>();
	}
	
	public Boolean handlePermission(String permission)
	{
		if(permissions.containsKey("*"))
		{
			return permissions.get("*");
		}
		
		for(Map.Entry<String, Boolean> e : permissions.entrySet())
		{
		}
		
		return null;
	}
	
	public JsonElement handleRankConfig(RankConfig permission)
	{
		if(this == Ranks.PLAYER) return permission.getDefaultPlayerValue();
		else if(this == Ranks.ADMIN) return permission.getDefaultOPValue();
		
		JsonElement e = config.get(permission);
		return (e == null) ? ((parent != null) ? parent.handleRankConfig(permission) : null) : e;
	}
	
	public JsonElement getJson()
	{
		JsonObject o = new JsonObject();
		
		o.add("parent", new JsonPrimitive(parent == null ? "" : parent.getID()));
		o.add("color", new JsonPrimitive(color.getFriendlyName()));
		o.add("prefix", new JsonPrimitive(prefix));
		o.add("badge", new JsonPrimitive(badge));
		
		if(!permissions.isEmpty())
		{
			JsonArray a1 = new JsonArray();
			
			for(Map.Entry<String, Boolean> e : permissions.entrySet())
			{
				a1.add(new JsonPrimitive((e.getValue().booleanValue() ? "+" : "-") + e.getKey()));
			}
			
			o.add("permissions", a1);
		}
		
		if(!config.isEmpty())
		{
			JsonObject o1 = new JsonObject();
			
			for(Map.Entry<RankConfig, JsonElement> e : config.entrySet())
			{
				o1.add(e.getKey().getID(), e.getValue());
			}
			
			o.add("config", o1);
		}
		
		return o;
	}
	
	public void setJson(JsonElement e)
	{
		JsonObject o = e.getAsJsonObject();
		parent = o.has("parent") ? Ranks.instance().ranks.get(o.get("parent").getAsString()) : null;
		color = o.has("color") ? EnumChatFormatting.getValueByName(o.get("color").getAsString()) : EnumChatFormatting.WHITE;
		prefix = o.has("prefix") ? o.get("prefix").getAsString() : "";
		badge = o.has("badge") ? o.get("badge").getAsString() : "";
		permissions.clear();
		config.clear();
		
		if(o.has("permissions"))
		{
			JsonArray a = o.get("permissions").getAsJsonArray();
			
			for(int i = 0; i < a.size(); i++)
			{
				String id = a.get(i).getAsString();
				char firstChar = id.charAt(0);
				boolean b = firstChar == '-';
				permissions.put((firstChar == '-' || firstChar == '+') ? id.substring(1) : id, b);
			}
		}
		
		if(o.has("config"))
		{
			for(Map.Entry<String, JsonElement> entry : o.get("config").getAsJsonObject().entrySet())
			{
				RankConfig c = ForgePermissionRegistry.getConfig(entry.getKey());
				
				if(c != null && !entry.getValue().isJsonNull())
				{
					config.put(c, entry.getValue());
				}
			}
		}
	}
	
	public boolean allowCommand(ICommand cmd, ICommandSender sender)
	{
		Boolean b = handlePermission("command." + cmd.getCommandName());
		if(b != null) return b.booleanValue();
		if(parent == null) return cmd.canCommandSenderUseCommand(sender);
		return parent.allowCommand(cmd, sender);
	}
}