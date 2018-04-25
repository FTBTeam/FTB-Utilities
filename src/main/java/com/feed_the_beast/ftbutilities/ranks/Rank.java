package com.feed_the_beast.ftbutilities.ranks;

import com.feed_the_beast.ftblib.FTBLibCommon;
import com.feed_the_beast.ftblib.lib.config.ConfigNull;
import com.feed_the_beast.ftblib.lib.config.ConfigValue;
import com.feed_the_beast.ftblib.lib.config.RankConfigValueInfo;
import com.feed_the_beast.ftblib.lib.util.FinalIDObject;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftblib.lib.util.misc.Node;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.IJsonSerializable;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Rank extends FinalIDObject implements IJsonSerializable
{
	public final Ranks ranks;
	Rank parent;
	public final Map<Node, JsonElement> permissions;
	public final Map<Node, Event.Result> cachedPermissions;
	public final Map<Node, ConfigValue> cachedConfig;
	String syntax;

	public Rank(Ranks r, String id, @Nullable Rank p)
	{
		super(id);
		ranks = r;
		permissions = new LinkedHashMap<>();
		cachedPermissions = new HashMap<>();
		cachedConfig = new HashMap<>();
		syntax = null;
		parent = p;
	}

	public Rank getParent()
	{
		return parent == null ? ranks.builtinPlayerRank : parent;
	}

	public JsonElement getMatchingJson(Node node)
	{
		JsonElement json = permissions.get(node);

		if (json != null)
		{
			return json;
		}

		int parts = 0;

		for (Map.Entry<Node, JsonElement> entry : permissions.entrySet())
		{
			if (entry.getKey().getPartCount() > parts && entry.getKey().matches(node))
			{
				parts = entry.getKey().getPartCount();
				json = entry.getValue();
			}
		}

		return json != null ? json : parent == null ? JsonNull.INSTANCE : parent.getMatchingJson(node);
	}

	public Event.Result hasPermission(Node node)
	{
		Event.Result r = cachedPermissions.get(node);

		if (r == null)
		{
			r = Event.Result.DEFAULT;
			JsonElement json = getMatchingJson(node);

			if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isBoolean())
			{
				r = json.getAsBoolean() ? Event.Result.ALLOW : Event.Result.DENY;
			}

			cachedPermissions.put(node, r);
		}

		return r;
	}

	public ConfigValue getConfig(Node node)
	{
		ConfigValue e = cachedConfig.get(node);

		if (e == null)
		{
			e = ConfigNull.INSTANCE;
			JsonElement json = getMatchingJson(node);

			if (!json.isJsonNull())
			{
				RankConfigValueInfo rconfig = FTBLibCommon.RANK_CONFIGS_MIRROR.get(node);

				if (rconfig != null)
				{
					e = rconfig.defaultValue.copy();
					e.fromJson(json);
				}
			}
		}

		cachedConfig.put(node, e);
		return e;
	}

	@Override
	public JsonElement getSerializableElement()
	{
		JsonObject o = new JsonObject();

		o.addProperty("parent", getParent().getName());

		if (syntax != null)
		{
			o.addProperty("syntax", syntax.replace(StringUtils.FORMATTING_CHAR, '&'));
		}

		JsonObject o1 = new JsonObject();

		for (Map.Entry<Node, JsonElement> e : permissions.entrySet())
		{
			o1.add(e.getKey().toString(), e.getValue());
		}

		o.add("permissions", o1);
		return o;
	}

	@Override
	public void fromJson(JsonElement e)
	{
		parent = null;
		permissions.clear();
		cachedPermissions.clear();
		cachedConfig.clear();
		syntax = null;

		if (!e.isJsonObject())
		{
			return;
		}

		JsonObject o = e.getAsJsonObject();

		if (o.has("parent"))
		{
			parent = ranks.getRank(o.get("parent").getAsString(), null);
		}

		if (o.has("syntax"))
		{
			syntax = o.get("syntax").getAsString().replace('&', StringUtils.FORMATTING_CHAR);
		}

		if (o.has("permissions"))
		{
			JsonElement e1 = o.get("permissions");

			if (e1.isJsonArray())
			{
				JsonArray a = e1.getAsJsonArray();

				for (int i = 0; i < a.size(); i++)
				{
					String id = a.get(i).getAsString();
					char firstChar = id.charAt(0);
					String key = (firstChar == '-' || firstChar == '+' || firstChar == '~') ? id.substring(1) : id;
					permissions.put(Node.get(key), new JsonPrimitive(firstChar != '-'));
				}
			}
			else
			{
				JsonObject o1 = e1.getAsJsonObject();

				for (Map.Entry<String, JsonElement> entry : o1.entrySet())
				{
					permissions.put(Node.get(entry.getKey()), entry.getValue());
				}
			}
		}

		if (o.has("config"))
		{
			for (Map.Entry<String, JsonElement> entry : o.get("config").getAsJsonObject().entrySet())
			{
				permissions.put(Node.get(entry.getKey()), entry.getValue());
			}
		}
	}

	public String getSyntax()
	{
		return syntax == null ? getParent().getSyntax() : syntax;
	}

	public String getFormattedName(String name)
	{
		return getSyntax().replace("$name", name);
	}
}