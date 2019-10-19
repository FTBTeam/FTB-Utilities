package com.feed_the_beast.ftbutilities.ranks;

import com.feed_the_beast.ftblib.lib.config.ConfigValue;
import com.feed_the_beast.ftblib.lib.util.FinalIDObject;
import com.feed_the_beast.ftblib.lib.util.misc.Node;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class Rank extends FinalIDObject
{
	public static final String TAG_DEFAULT_PLAYER = "default_player_rank";
	public static final String TAG_DEFAULT_OP = "default_op_rank";
	public static final HashSet<String> TAGS = new HashSet<>();

	static
	{
		TAGS.add(TAG_DEFAULT_PLAYER);
		TAGS.add(TAG_DEFAULT_OP);
	}

	public static class Entry implements Comparable<Entry>
	{
		public final Node node;
		public String value = "";

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
		displayName = new TextComponentString(getId());
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

	public boolean setPermission(Node node, String value)
	{
		if (value.isEmpty())
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

		for (Entry entry : permissions)
		{
			if (entry.node.equals(node))
			{
				if (!entry.value.equals(value))
				{
					entry.value = value;
					return true;
				}

				return false;
			}
		}

		Entry entry = new Entry(node);
		entry.value = value;
		permissions.add(entry);
		return true;
	}

	public Event.Result getPermissionRaw(Node node, boolean matching)
	{
		Event.Result result = Event.Result.DEFAULT;

		int parts = 0;

		for (Entry entry : permissions)
		{
			if (entry.node.getPartCount() > parts && (entry.value.equals("true") || entry.value.equals("false")) && (matching ? entry.node.matches(node) : entry.node.equals(node)))
			{
				parts = entry.node.getPartCount();
				result = entry.value.equals("true") ? Event.Result.ALLOW : Event.Result.DENY;
			}
		}

		return result != Event.Result.DEFAULT || parent.isNone() ? result : parent.getPermissionRaw(node, matching);
	}

	public String getConfigRaw(Node node)
	{
		for (Entry entry : permissions)
		{
			if (entry.node.equals(node) && !entry.value.isEmpty())
			{
				return entry.value;
			}
		}

		return parent.isNone() ? "" : parent.getConfigRaw(node);
	}
}