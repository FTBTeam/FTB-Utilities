package com.feed_the_beast.ftbutilities.data;

/**
 * @author LatvianModder
 */
public enum BlockInteractionType
{
	EDIT(false),
	INTERACT(false),
	CNB_BREAK(false),
	CNB_PLACE(false),
	ITEM(true);

	public final boolean defaultResult;

	BlockInteractionType(boolean d)
	{
		defaultResult = d;
	}
}