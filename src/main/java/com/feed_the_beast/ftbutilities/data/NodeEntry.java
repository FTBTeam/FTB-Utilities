package com.feed_the_beast.ftbutilities.data;

import com.feed_the_beast.ftblib.lib.util.misc.Node;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public final class NodeEntry
{
	private Node node;
	private DefaultPermissionLevel level;
	private String desc;

	public NodeEntry(Node n, DefaultPermissionLevel l, @Nullable String d)
	{
		node = n;
		level = l;
		desc = d;
	}

	public Node getNode()
	{
		return node;
	}

	public DefaultPermissionLevel getLevel()
	{
		return level;
	}

	@Nullable
	public String getDescription()
	{
		return desc;
	}

	public int hashCode()
	{
		return node.hashCode();
	}

	public boolean equals(Object o)
	{
		return o == this || o instanceof NodeEntry && node.equals(((NodeEntry) o).node);
	}
}