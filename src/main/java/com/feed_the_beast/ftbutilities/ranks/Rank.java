package com.feed_the_beast.ftbutilities.ranks;

import com.feed_the_beast.ftblib.lib.config.ConfigValue;
import com.feed_the_beast.ftblib.lib.util.FinalIDObject;
import com.feed_the_beast.ftblib.lib.util.JsonUtils;
import com.feed_the_beast.ftblib.lib.util.misc.Node;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class Rank extends FinalIDObject
{
	public static final String TAG_DEFAULT_PLAYER = "default_player_rank";
	public static final String TAG_DEFAULT_OP = "default_op_rank";

	public static class Entry implements Comparable<Entry>
	{
		public final Node node;
		public JsonElement json = JsonNull.INSTANCE;

		public Entry(Node n)
		{
			node = n;
		}

		@Override
		public int compareTo(Entry o)
		{
			return node.compareTo(o.node);
		}
	}

	public final Ranks ranks;
	protected ITextComponent displayName;
	public Rank parent;
	public final Collection<String> tags;
	public final List<Entry> permissions;
	public final Map<Node, Event.Result> cachedPermissions;
	public final Map<Node, ConfigValue> cachedConfig;

	public Rank(Ranks r, String id)
	{
		super(id);
		displayName = new TextComponentString(getName());
		displayName.getStyle().setColor(TextFormatting.DARK_GREEN);
		ranks = r;
		parent = ranks.none;
		tags = new LinkedHashSet<>();
		permissions = new ArrayList<>();
		cachedPermissions = new HashMap<>();
		cachedConfig = new HashMap<>();
	}

	public boolean isNone()
	{
		return false;
	}

	public ITextComponent getDisplayName()
	{
		return displayName;
	}

	public boolean setPermission(Node node, @Nullable JsonElement json)
	{
		if (JsonUtils.isNull(json))
		{
			Iterator<Entry> iterator = permissions.iterator();

			while (iterator.hasNext())
			{
				if (iterator.next().node.equals(node))
				{
					iterator.remove();
					return true;
				}
			}

			return false;
		}
		else if (json.isJsonObject())
		{
			return false;
		}

		for (Entry entry : permissions)
		{
			if (entry.node.equals(node))
			{
				if (!entry.json.equals(json))
				{
					entry.json = json;
					return true;
				}

				return false;
			}
		}

		Entry entry = new Entry(node);
		entry.json = json;
		permissions.add(entry);
		return true;
	}

	public Event.Result getPermissionRaw(Node node, boolean matching)
	{
		Event.Result result = Event.Result.DEFAULT;

		int parts = 0;

		for (Entry entry : permissions)
		{
			if (entry.node.getPartCount() > parts && entry.json.isJsonPrimitive() && entry.json.getAsJsonPrimitive().isBoolean() && (matching ? entry.node.matches(node) : entry.node.equals(node)))
			{
				parts = entry.node.getPartCount();
				result = entry.json.getAsBoolean() ? Event.Result.ALLOW : Event.Result.DENY;
			}
		}

		return result != Event.Result.DEFAULT || parent.isNone() ? result : parent.getPermissionRaw(node, matching);
	}

	public JsonElement getConfigRaw(Node node)
	{
		for (Entry entry : permissions)
		{
			if (entry.node.equals(node) && !JsonUtils.isNull(entry.json))
			{
				return entry.json;
			}
		}

		return parent.isNone() ? JsonNull.INSTANCE : parent.getConfigRaw(node);
	}
}