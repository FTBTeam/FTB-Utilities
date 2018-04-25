package com.feed_the_beast.ftbutilities.ranks;

import com.feed_the_beast.ftblib.lib.util.misc.Node;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * @author LatvianModder
 */
class BuiltinRank extends Rank
{
	BuiltinRank(Ranks r, String id)
	{
		super(r, id, null);
	}

	@Override
	public Event.Result hasPermission(Node permission)
	{
		return Event.Result.DEFAULT;
	}

	@Override
	public String getSyntax()
	{
		return "<$name> ";
	}

	@Override
	public void fromJson(JsonElement json)
	{
	}

	@Override
	public JsonElement getSerializableElement()
	{
		return JsonNull.INSTANCE;
	}
}