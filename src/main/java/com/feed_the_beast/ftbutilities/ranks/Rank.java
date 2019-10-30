package com.feed_the_beast.ftbutilities.ranks;

import com.feed_the_beast.ftblib.lib.util.FinalIDObject;
import com.feed_the_beast.ftblib.lib.util.StringJoiner;
import com.feed_the_beast.ftblib.lib.util.misc.Node;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Rank extends FinalIDObject implements Comparable<Rank>
{
	public static final Node NODE_PARENT = Node.get("parent");
	public static final Node NODE_DEFAULT_PLAYER = Node.get("default_player_rank");
	public static final Node NODE_DEFAULT_OP = Node.get("default_op_rank");
	public static final Node NODE_POWER = Node.get("power");

	public static class Entry implements Comparable<Entry>
	{
		public final Node node;
		public String value = "";
		public String comment = "";

		public Entry(Node n)
		{
			node = n;
		}

		@Override
		public int compareTo(Entry o)
		{
			return node.compareTo(o.node);
		}

		@Override
		public String toString()
		{
			return node + ":" + value;
		}
	}

	public final Ranks ranks;
	private int power;
	protected ITextComponent displayName;
	private Set<Rank> parents;
	public final List<Entry> permissions;
	public String comment;

	public Rank(Ranks r, String id)
	{
		super(id);
		displayName = new TextComponentString(getId());
		displayName.getStyle().setColor(TextFormatting.DARK_GREEN);
		ranks = r;
		permissions = new ArrayList<>();
		comment = "";
		power = -1;
	}

	public int getPower()
	{
		if (power == -1)
		{
			String s = getConfigSelf(NODE_POWER);

			if (s.isEmpty())
			{
				power = 0;
			}
			else
			{
				power = MathHelper.clamp(Integer.parseInt(s), 0, Integer.MAX_VALUE - 1);
			}
		}

		return power;
	}

	public boolean isPlayer()
	{
		return false;
	}

	public void clearCache()
	{
		parents = null;
		power = -1;
	}

	public ITextComponent getDisplayName()
	{
		return displayName;
	}

	public Set<Rank> getParents()
	{
		if (parents == null)
		{
			List<Rank> list = new ArrayList<>();

			for (String s : getConfigSelf(NODE_PARENT).split(","))
			{
				Rank r = ranks.getRank(s.trim());

				if (r != null && !r.isPlayer())
				{
					list.add(r);
				}
			}

			list.sort(null);
			parents = new LinkedHashSet<>(list);
		}

		return parents;
	}

	public boolean addParent(@Nullable Rank rank)
	{
		if (rank == null || rank.isPlayer())
		{
			return false;
		}

		parents = getParents();

		if (parents.add(rank))
		{
			setPermission(NODE_PARENT, StringJoiner.with(", ").join(parents));
			parents = null;
			return true;
		}

		return false;
	}

	public boolean removeParent(Rank rank)
	{
		parents = getParents();

		if (parents.remove(rank))
		{
			setPermission(NODE_PARENT, StringJoiner.with(", ").join(parents));
			parents = null;
			return true;
		}

		return false;
	}

	public boolean clearParents()
	{
		power = -1;
		parents = null;
		return setPermission(NODE_PARENT, "") != null;
	}

	@Nullable
	public Entry setPermission(String node, Object value)
	{
		return setPermission(Node.get(node), String.valueOf(value));
	}

	@Nullable
	public Entry setPermission(Node node, String value)
	{
		if (value.isEmpty())
		{
			Iterator<Entry> iterator = permissions.iterator();

			while (iterator.hasNext())
			{
				Entry entry = iterator.next();
				if (entry.node.equals(node))
				{
					iterator.remove();
					return entry;
				}
			}

			return null;
		}

		for (Entry entry : permissions)
		{
			if (entry.node.equals(node))
			{
				if (!entry.value.equals(value))
				{
					entry.value = value;
					return entry;
				}

				return null;
			}
		}

		Entry entry = new Entry(node);
		entry.value = value;
		permissions.add(entry);
		return entry;
	}

	public Event.Result getPermissionSelf(Node node, boolean matching)
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

		return result;
	}

	public Event.Result getPermission(Node node, boolean matching)
	{
		Event.Result result = getPermissionSelf(node, matching);

		if (result == Event.Result.DEFAULT)
		{
			for (Rank parent : getParents())
			{
				result = parent.getPermission(node, matching);

				if (result != Event.Result.DEFAULT)
				{
					return result;
				}
			}
		}

		return result;
	}

	public String getConfigSelf(Node node)
	{
		for (Entry entry : permissions)
		{
			if (entry.node.equals(node) && !entry.value.isEmpty())
			{
				return entry.value;
			}
		}

		return "";
	}

	public String getConfig(Node node)
	{
		String s = getConfigSelf(node);

		if (!s.isEmpty())
		{
			return s;
		}

		for (Rank parent : getParents())
		{
			s = parent.getConfig(node);

			if (!s.isEmpty())
			{
				return s;
			}
		}

		return "";
	}

	public boolean add()
	{
		return ranks.ranks.put(getId(), this) != this;
	}

	public boolean remove()
	{
		if (ranks.ranks.remove(getId()) != null)
		{
			for (Rank rank : ranks.ranks.values())
			{
				rank.removeParent(this);
			}

			for (Rank rank : ranks.playerRanks.values())
			{
				rank.removeParent(this);
			}

			return true;
		}

		return false;
	}

	@Override
	public int compareTo(Rank o)
	{
		return Integer.compare(o.getPower(), getPower());
	}

	public boolean isDefaultPlayerRank()
	{
		return getConfigSelf(NODE_DEFAULT_PLAYER).equals("true");
	}

	public boolean isDefaultOPRank()
	{
		return getConfigSelf(NODE_DEFAULT_OP).equals("true");
	}
}