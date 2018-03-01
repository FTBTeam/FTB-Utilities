package com.feed_the_beast.ftbutilities.ranks;

import com.feed_the_beast.ftblib.FTBLibModCommon;
import com.feed_the_beast.ftblib.lib.config.ConfigValue;
import com.feed_the_beast.ftblib.lib.config.RankConfigValueInfo;
import com.feed_the_beast.ftblib.lib.util.FinalIDObject;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftbutilities.FTBUFinals;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.IJsonSerializable;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Rank extends FinalIDObject implements IJsonSerializable
{
	private static final String[] EVENT_RESULT_PREFIX = {"-", "~", "+"};

	Rank parent;
	private final Map<String, Event.Result> permissions;
	private final Map<String, Event.Result> cachedPermissions;
	private final Map<String, ConfigValue> config;
	private final Map<String, ConfigValue> cachedConfig;
	String syntax;

	public Rank(String id)
	{
		super(id);
		permissions = new LinkedHashMap<>();
		cachedPermissions = new HashMap<>();
		config = new LinkedHashMap<>();
		cachedConfig = new HashMap<>();
		syntax = null;
	}

	public Rank(String id, Rank r)
	{
		this(id);
		parent = r;
	}

	public Rank getParent()
	{
		return parent == null ? DefaultPlayerRank.INSTANCE : parent;
	}

	private Event.Result hasPermissionRaw(String permission)
	{
		Event.Result r = permissions.get(permission);
		if (r != null)
		{
			return r;
		}

		String[] splitPermission = permission.split("\\.");

		for (Map.Entry<String, Event.Result> entry : permissions.entrySet())
		{
			if (StringUtils.nodesMatch(splitPermission, entry.getKey().split("\\.")))
			{
				return entry.getValue();
			}
		}

		return getParent().hasPermission(permission);
	}

	public Event.Result hasPermission(String permission)
	{
		Event.Result r = cachedPermissions.get(permission);

		if (r == null)
		{
			r = hasPermissionRaw(permission);
			cachedPermissions.put(permission, r);
		}

		return r;
	}

	public ConfigValue getConfig(String id)
	{
		ConfigValue e = cachedConfig.get(id);

		if (e == null)
		{
			e = config.get(id);

			if (e == null || e.isNull())
			{
				e = getParent().getConfig(id);
			}
		}

		cachedConfig.put(id, e);
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

		JsonArray a1 = new JsonArray();

		for (Map.Entry<String, Event.Result> e : permissions.entrySet())
		{
			a1.add(EVENT_RESULT_PREFIX[e.getValue().ordinal()] + e.getKey());
		}

		o.add("permissions", a1);

		JsonObject o1 = new JsonObject();
		config.forEach((key, value) -> o1.add(key, value.getSerializableElement()));
		o.add("config", o1);

		return o;
	}

	@Override
	public void fromJson(JsonElement e)
	{
		parent = null;
		permissions.clear();
		config.clear();
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
			parent = Ranks.getRank(o.get("parent").getAsString(), null);
		}

		if (o.has("syntax"))
		{
			syntax = o.get("syntax").getAsString().replace('&', StringUtils.FORMATTING_CHAR);
		}

		if (o.has("permissions"))
		{
			JsonArray a = o.get("permissions").getAsJsonArray();

			for (int i = 0; i < a.size(); i++)
			{
				String id = a.get(i).getAsString();
				char firstChar = id.charAt(0);
				String key = (firstChar == '-' || firstChar == '+' || firstChar == '~') ? id.substring(1) : id;
				permissions.put(key, firstChar == '-' ? Event.Result.DENY : (firstChar == '~' ? Event.Result.DEFAULT : Event.Result.ALLOW));
			}
		}

		if (o.has("config"))
		{
			for (Map.Entry<String, JsonElement> entry : o.get("config").getAsJsonObject().entrySet())
			{
				RankConfigValueInfo rconfig = FTBLibModCommon.RANK_CONFIGS_MIRROR.get(entry.getKey());

				if (rconfig != null)
				{
					ConfigValue value = rconfig.defaultValue.copy();
					value.fromJson(entry.getValue());
					config.put(rconfig.id, value);
				}
			}
		}
	}

	public String getSyntax()
	{
		return syntax == null ? getParent().getSyntax() : syntax;
	}

	public String getFormattedName(String name, String tag)
	{
		String s = getSyntax();
		s = s.replace("$name", name);
		s = s.replace("$rank", getName());
		s = s.replace("$tag", tag);
		s = s.replaceAll("\\[]", "");
		return s;
	}
}