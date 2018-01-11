package com.feed_the_beast.ftbutilities.events;

import com.feed_the_beast.ftbutilities.data.NodeEntry;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class CustomPermissionPrefixesRegistryEvent extends FTBUtilitiesEvent
{
	private final Consumer<NodeEntry> callback;

	public CustomPermissionPrefixesRegistryEvent(Consumer<NodeEntry> c)
	{
		callback = c;
	}

	public void register(NodeEntry entry)
	{
		callback.accept(entry);
	}
}