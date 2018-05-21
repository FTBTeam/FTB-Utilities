package com.feed_the_beast.ftbutilities.ranks;

import com.feed_the_beast.ftblib.lib.config.ConfigValue;
import com.feed_the_beast.ftblib.lib.util.FinalIDObject;
import com.feed_the_beast.ftblib.lib.util.JsonUtils;
import com.feed_the_beast.ftblib.lib.util.misc.Node;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Rank extends FinalIDObject
{
	public static class Entry
	{
		public final Node node;
		public JsonElement json = JsonNull.INSTANCE;
		public String comment = "";

		public Entry(Node n)
		{
			node = n;
		}
	}

	public final Ranks ranks;
	public Rank parent;
	public final Map<Node, Entry> permissions;
	public final Map<Node, Event.Result> cachedPermissions;
	public final Map<Node, ConfigValue> cachedConfig;
	public String comment;

	public Rank(Ranks r, String id)
	{
		super(id);
		ranks = r;
		permissions = new LinkedHashMap<>();
		cachedPermissions = new HashMap<>();
		cachedConfig = new HashMap<>();
		setDefaults();
	}

	public void setDefaults()
	{
		parent = null;
		permissions.clear();
		cachedPermissions.clear();
		cachedConfig.clear();
		comment = "";
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

		return result != Event.Result.DEFAULT ? result : parent == null ? Event.Result.DEFAULT : parent.getPermissionRaw(node);
	}

	public JsonElement getConfigRaw(Node node)
	{
		Entry entry = permissions.get(node);

		if (entry != null && !JsonUtils.isNull(entry.json))
		{
			return entry.json;
		}

		return parent == null ? JsonNull.INSTANCE : parent.getConfigRaw(node);
	}
}