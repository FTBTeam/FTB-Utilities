package com.feed_the_beast.ftbu.api.events.registry;

import com.feed_the_beast.ftbu.api.NodeEntry;
import com.feed_the_beast.ftbu.api.events.FTBUtilitiesEvent;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class RegisterCustomPermissionPrefixesEvent extends FTBUtilitiesEvent
{
	private final Consumer<NodeEntry> callback;

	public RegisterCustomPermissionPrefixesEvent(Consumer<NodeEntry> c)
	{
		callback = c;
	}

	public void register(NodeEntry entry)
	{
		callback.accept(entry);
	}
}