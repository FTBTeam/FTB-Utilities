package latmod.ftbu.mod.client.gui.guide;

import com.google.gson.*;
import latmod.ftbu.api.guide.*;
import net.minecraft.util.*;

import java.lang.reflect.Type;

public class GuideLinkSerializer implements JsonDeserializer<GuideLink>
{
	public static final Gson gson = createGson();
	
	private static final Gson createGson()
	{
		GsonBuilder gb = new GsonBuilder();
		gb.registerTypeAdapter(GuideLink.class, new GuideLinkSerializer());
		gb.registerTypeHierarchyAdapter(IChatComponent.class, new IChatComponent.Serializer());
		gb.registerTypeHierarchyAdapter(ChatStyle.class, new ChatStyle.Serializer());
		gb.registerTypeAdapterFactory(new EnumTypeAdapterFactory());
		return gb.create();
	}
	
	public GuideLink deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		if(json.isJsonNull() || !json.isJsonObject()) return null;
		
		JsonObject o = json.getAsJsonObject();
		
		if(o.has("type") && o.has("link"))
		{
			LinkType type = LinkType.valueOf(o.get("type").getAsString().toUpperCase());
			GuideLink special = new GuideLink(type, o.get("link").getAsString());
			special.title = o.has("title") ? (IChatComponent)context.deserialize(o.get("title"), IChatComponent.class) : new ChatComponentText(special.link);
			if(o.has("hover")) special.hover = (IChatComponent)context.deserialize(o.get("hover"), IChatComponent.class);
			return special;
		}
		
		return null;
	}
}