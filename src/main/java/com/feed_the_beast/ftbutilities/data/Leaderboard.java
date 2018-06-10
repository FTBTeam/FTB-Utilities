package com.feed_the_beast.ftbutilities.data;

import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.math.Ticks;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import net.minecraft.stats.StatBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public class Leaderboard
{
	public final ResourceLocation id;
	private final ITextComponent title;
	private final Function<ForgePlayer, ITextComponent> playerToValue;
	private final Comparator<ForgePlayer> comparator;
	private final Predicate<ForgePlayer> validValue;

	public Leaderboard(ResourceLocation _id, ITextComponent t, Function<ForgePlayer, ITextComponent> v, Comparator<ForgePlayer> c, Predicate<ForgePlayer> vv)
	{
		id = _id;
		title = t;
		playerToValue = v;
		comparator = c.thenComparing((o1, o2) -> o1.getDisplayNameString().compareToIgnoreCase(o2.getDisplayNameString()));
		validValue = vv;
	}

	public final ITextComponent getTitle()
	{
		return title;
	}

	public final Comparator<ForgePlayer> getComparator()
	{
		return comparator;
	}

	public final ITextComponent createValue(ForgePlayer player)
	{
		return playerToValue.apply(player);
	}

	public final boolean hasValidValue(ForgePlayer player)
	{
		return validValue.test(player);
	}

	public static class FromStat extends Leaderboard
	{
		public static final IntFunction<ITextComponent> DEFAULT = value -> new TextComponentString(value <= 0 ? "0" : Integer.toString(value));
		public static final IntFunction<ITextComponent> TIME = value -> new TextComponentString("[" + (int) (value / 72000D + 0.5D) + "h] " + Ticks.get(value).toTimeString());

		public FromStat(ResourceLocation id, ITextComponent t, StatBase statBase, boolean from0to1, IntFunction<ITextComponent> valueToString)
		{
			super(id, t,
					player -> valueToString.apply(player.stats().readStat(statBase)),
					(o1, o2) -> {
						int i = Integer.compare(o1.stats().readStat(statBase), o2.stats().readStat(statBase));
						return from0to1 ? i : -i;
					},
					player -> player.stats().readStat(statBase) > 0);
		}

		public FromStat(ResourceLocation id, StatBase statBase, boolean from0to1, IntFunction<ITextComponent> valueToString)
		{
			this(id, StringUtils.color(statBase.getStatName(), null), statBase, from0to1, valueToString);
		}
	}
}