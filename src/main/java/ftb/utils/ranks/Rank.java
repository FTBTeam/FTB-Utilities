package ftb.utils.ranks;

import com.google.gson.*;
import ftb.lib.api.permission.*;
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
	public final Map<ForgePermission, JsonElement> permissions;
	
	private final Map<String, Boolean> commandPermissions;
	private int commands = 0;
	
	public Rank(String id)
	{
		super(id);
		permissions = new LinkedHashMap<>();
		commandPermissions = new HashMap<>();
	}
	
	public JsonElement handlePermission(ForgePermission permission)
	{
		if(this == Ranks.PLAYER) return permission.getDefaultPlayerValue();
		else if(this == Ranks.ADMIN) return permission.getDefaultOPValue();
		
		JsonElement e = permissions.get(permission);
		return (e == null) ? ((parent != null) ? parent.handlePermission(permission) : null) : e;
	}
	
	public JsonElement getJson()
	{
		JsonObject o = new JsonObject();
		
		o.add("parent", new JsonPrimitive(parent == null ? "" : parent.ID));
		o.add("color", new JsonPrimitive(color.getFriendlyName()));
		o.add("prefix", new JsonPrimitive(prefix));
		o.add("badge", new JsonPrimitive(badge));
		
		JsonObject o1 = new JsonObject();
		
		for(Map.Entry<ForgePermission, JsonElement> e : permissions.entrySet())
		{
			o1.add(e.getKey().ID, e.getValue());
		}
		
		o.add("permissions", o1);
		
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
		commandPermissions.clear();
		
		if(o.has("permissions"))
		{
			for(Map.Entry<String, JsonElement> entry : o.get("permissions").getAsJsonObject().entrySet())
			{
				String id = entry.getKey();
				
				if(id.startsWith("command."))
				{
					commandPermissions.put(id.substring(8), entry.getValue().getAsBoolean());
					permissions.put(new CommandPermission(id), entry.getValue());
				}
				else
				{
					for(ForgePermission p : ForgePermissionRegistry.values(entry.getKey()))
					{
						permissions.put(p, entry.getValue());
					}
				}
			}
		}
		
		commands = 0;
		if(commandPermissions.containsKey("**")) commands = 2;
		else if(commandPermissions.containsKey("*")) commands = 1;
	}
	
	public boolean allowCommand(ICommand cmd, ICommandSender sender)
	{
		if(commands == 2) return true;
		else if(commands == 1) return cmd.canCommandSenderUseCommand(sender);
		Boolean b = commandPermissions.get(cmd.getCommandName());
		if(b != null) return b.booleanValue();
		return parent != null && parent.allowCommand(cmd, sender);
	}
}