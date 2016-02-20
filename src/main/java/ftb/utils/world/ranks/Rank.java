package ftb.utils.world.ranks;

import com.google.gson.*;
import ftb.lib.api.permission.ForgePermission;
import latmod.lib.util.FinalIDObject;
import net.minecraft.util.EnumChatFormatting;

import java.util.*;

public class Rank extends FinalIDObject
{
	public Rank parent = null;
	public EnumChatFormatting color = EnumChatFormatting.WHITE;
	public String prefix = "";
	public final Map<ForgePermission, JsonElement> permissions;
	public String badge = "";
	
	public Rank(String id)
	{
		super(id);
		permissions = new HashMap<>();
	}
	
	public void setJson(JsonObject o)
	{
	}
	
	public JsonElement getJsonElement(ForgePermission permission)
	{
		JsonElement e = permissions.get(permission);
		return (e == null) ? ((parent != null) ? parent.getJsonElement(permission) : null) : e;
	}
}