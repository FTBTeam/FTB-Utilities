package com.feed_the_beast.ftbutilities.ranks;

import com.feed_the_beast.ftblib.lib.util.misc.Node;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.Event;

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
	public boolean setPermission(Node node, String value)
	{
		return false;
	}

	@Override
	public Event.Result getPermissionRaw(Node node, boolean matching)
	{
		return Event.Result.DEFAULT;
	}

	@Override
	public String getConfigRaw(Node node)
	{
		return "";
	}
}