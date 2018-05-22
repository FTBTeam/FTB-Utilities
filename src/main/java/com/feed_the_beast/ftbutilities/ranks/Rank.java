package com.feed_the_beast.ftbutilities.ranks;

import com.feed_the_beast.ftblib.lib.config.ConfigValue;
import com.feed_the_beast.ftblib.lib.util.FinalIDObject;
import com.feed_the_beast.ftblib.lib.util.JsonUtils;
import com.feed_the_beast.ftblib.lib.util.misc.Node;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class Rank extends FinalIDObject
{
	public static class Entry
	{
		public final Node node;
		public JsonElement json = JsonNull.INSTANCE;

		public Entry(Node n)
		{
			node = n;
		}
	}

	public final Ranks ranks;
	public final Collection<Rank> parents;
	public final Collection<String> tags;
	public final Map<Node, Entry> permissions;
	public final Map<Node, Event.Result> cachedPermissions;
	public final Map<Node, ConfigValue> cachedConfig;

	public Rank(Ranks r, String id)
	{
		super(id);
		ranks = r;
		parents = new LinkedHashSet<>();
		tags = new LinkedHashSet<>();
		permissions = new LinkedHashMap<>();
		cachedPermissions = new HashMap<>();
		cachedConfig = new HashMap<>();
		setDefaults();
	}

	public void setDefaults()
	{
		parents.clear();
		tags.clear();
		permissions.clear();
		cachedPermissions.clear();
		cachedConfig.clear();
	}

	public String setPermission(Node node, @Nullable JsonElement json)
	{
		if (JsonUtils.isNull(json))
		{
			return permissions.remove(node) != null ? "none" : "";
		}
		else if (json.isJsonObject())
		{
			return "";
		}

		Entry entry = permissions.get(node);

		if (entry == null)
		{
			entry = new Entry(node);
			permissions.put(entry.node, entry);
		}

		if (!entry.json.equals(json))
		{
			entry.json = json;
			return json.toString();
		}

		return "";
	}

	public Event.Result getPermissionRaw(Node node)
	{
		Event.Result result = Event.Result.DEFAULT;

		int parts = 0;

		for (Entry entry : permissions.values())
		{
			if (entry.node.getPartCount() > parts && entry.json.isJsonPrimitive() && entry.json.getAsJsonPrimitive().isBoolean() && entry.node.matches(node))
			{
				parts = entry.node.getPartCount();
				result = entry.json.getAsBoolean() ? Event.Result.ALLOW : Event.Result.DENY;
			}
		}

		if (result != Event.Result.DEFAULT)
		{
			return result;
		}

		for (Rank parent : parents)
		{
			result = parent.getPermissionRaw(node);

			if (result != Event.Result.DEFAULT)
			{
				return result;
			}
		}

		return Event.Result.DEFAULT;
	}

	public JsonElement getConfigRaw(Node node)
	{
		Entry entry = permissions.get(node);

		if (entry != null && !JsonUtils.isNull(entry.json))
		{
			return entry.json;
		}

		for (Rank parent : parents)
		{
			JsonElement json = parent.getConfigRaw(node);

			if (!JsonUtils.isNull(json))
			{
				return json;
			}
		}

		return JsonNull.INSTANCE;
	}
}