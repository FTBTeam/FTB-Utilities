package com.feed_the_beast.ftbutilities;

import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.math.Ticks;
import com.feed_the_beast.ftbutilities.data.Leaderboard;
import com.feed_the_beast.ftbutilities.events.LeaderboardRegistryEvent;
import net.minecraft.stats.StatList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Comparator;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = FTBUtilities.MOD_ID)
public class FTBUtilitiesLeaderboards
{
	@SubscribeEvent
	public static void registerLeaderboards(LeaderboardRegistryEvent event)
	{
		event.register(new Leaderboard.FromStat(new ResourceLocation(FTBUtilities.MOD_ID, "deaths"), StatList.DEATHS, false, Leaderboard.FromStat.DEFAULT));
		event.register(new Leaderboard.FromStat(new ResourceLocation(FTBUtilities.MOD_ID, "mob_kills"), StatList.MOB_KILLS, false, Leaderboard.FromStat.DEFAULT));
		event.register(new Leaderboard.FromStat(new ResourceLocation(FTBUtilities.MOD_ID, "time_played"), StatList.PLAY_ONE_MINUTE, false, Leaderboard.FromStat.TIME));
		event.register(new Leaderboard.FromStat(new ResourceLocation(FTBUtilities.MOD_ID, "jumps"), StatList.JUMP, false, Leaderboard.FromStat.DEFAULT));

		event.register(new Leaderboard(
				new ResourceLocation(FTBUtilities.MOD_ID, "deaths_per_hour"),
				new TextComponentTranslation("ftbutilities.stat.dph"),
				player ->
				{
					double d = getDPH(player);
					return new TextComponentString(d < 0D ? "-" : String.format("%.2f", d));
				},
				Comparator.comparingDouble(FTBUtilitiesLeaderboards::getDPH).reversed(),
				player -> getDPH(player) >= 0D));

		event.register(new Leaderboard(
				new ResourceLocation(FTBUtilities.MOD_ID, "last_seen"),
				new TextComponentTranslation("ftbutilities.stat.last_seen"),
				player ->
				{
					if (player.isOnline())
					{
						ITextComponent component = new TextComponentTranslation("gui.online");
						component.getStyle().setColor(TextFormatting.GREEN);
						return component;
					}
					else
					{
						long worldTime = player.team.universe.world.getTotalWorldTime();
						int time = (int) (worldTime - player.getLastTimeSeen());
						return Leaderboard.FromStat.TIME.apply(time);
					}
				},
				Comparator.comparingLong(FTBUtilitiesLeaderboards::getRelativeLastSeen),
				player -> player.getLastTimeSeen() != 0L));
	}

	private static long getRelativeLastSeen(ForgePlayer player)
	{
		if (player.isOnline())
		{
			return 0;
		}

		return player.team.universe.ticks.ticks() - player.getLastTimeSeen();
	}

	private static double getDPH(ForgePlayer player)
	{
		int playTime = player.stats().readStat(StatList.PLAY_ONE_MINUTE);

		if (playTime > 0)
		{
			double hours = Ticks.get(playTime).hoursd();

			if (hours >= 1D)
			{
				return (double) player.stats().readStat(StatList.DEATHS) / hours;
			}
		}

		return -1D;
	}
}