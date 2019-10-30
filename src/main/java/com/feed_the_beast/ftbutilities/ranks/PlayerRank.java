package com.feed_the_beast.ftbutilities.ranks;

import com.feed_the_beast.ftblib.lib.util.StringUtils;
import net.minecraft.util.text.TextFormatting;

import java.util.UUID;

/**
 * @author LatvianModder
 */
public class PlayerRank extends Rank
{
	public final UUID uuid;

	PlayerRank(Ranks r, UUID id, String name)
	{
		super(r, StringUtils.fromUUID(id));
		displayName.getStyle().setColor(TextFormatting.YELLOW);
		uuid = id;
		comment = name;
	}

	@Override
	public int getPower()
	{
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean isPlayer()
	{
		return true;
	}

	@Override
	public boolean add()
	{
		return ranks.playerRanks.put(uuid, this) != this;
	}

	@Override
	public boolean remove()
	{
		if (!permissions.isEmpty() || !getParents().isEmpty())
		{
			permissions.clear();
			clearParents();
			return true;
		}

		return false;
	}

	@Override
	public boolean isDefaultPlayerRank()
	{
		return false;
	}

	@Override
	public boolean isDefaultOPRank()
	{
		return false;
	}
}