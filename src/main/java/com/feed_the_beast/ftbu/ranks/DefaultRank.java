package com.feed_the_beast.ftbu.ranks;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * @author LatvianModder
 */
class DefaultRank extends Rank
{
	DefaultRank(String id)
	{
		super(id);
	}

	@Override
	public Event.Result hasPermission(String permission)
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