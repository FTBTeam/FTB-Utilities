package com.feed_the_beast.ftbutilities.ranks;

import com.feed_the_beast.ftblib.lib.util.misc.Node;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class RankNone extends Rank
{
	RankNone(Ranks r, String id)
	{
		super(r, id);
		displayName.getStyle().setColor(TextFormatting.DARK_GRAY);
	}

	@Override
	public boolean isNone()
	{
		return true;
	}

	@Override
	public boolean setPermission(Node node, @Nullable JsonElement json)
	{
		return false;
	}

	@Override
	public Event.Result getPermissionRaw(Node node, boolean matching)
	{
		return Event.Result.DEFAULT;
	}

	@Override
	public JsonElement getConfigRaw(Node node)
	{
		return JsonNull.INSTANCE;
	}
}