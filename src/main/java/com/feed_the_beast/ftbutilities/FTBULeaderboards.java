package com.feed_the_beast.ftbutilities;

import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.gui.GuiLang;
import com.feed_the_beast.ftblib.lib.util.CommonUtils;
import com.feed_the_beast.ftbutilities.data.Leaderboard;
import net.minecraft.stats.StatList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.Comparator;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = FTBUFinals.MOD_ID)
public class FTBULeaderboards
{
	@SubscribeEvent
	public static void registerRegistries(RegistryEvent.NewRegistry event)
	{
		FTBUCommon.LEADERBOARDS = new RegistryBuilder<Leaderboard>().setName(FTBUFinals.get("leaderboards")).setType(Leaderboard.class).setMaxID(255).create();
	}

	@SubscribeEvent
	public static void registerLeaderboards(RegistryEvent.Register<Leaderboard> event)
	{
		event.getRegistry().register(new Leaderboard.FromStat(StatList.DEATHS, false, Leaderboard.FromStat.DEFAULT).setRegistryName(FTBUFinals.MOD_ID + ":deaths"));
		event.getRegistry().register(new Leaderboard.FromStat(StatList.MOB_KILLS, false, Leaderboard.FromStat.DEFAULT).setRegistryName(FTBUFinals.MOD_ID + ":mob_kills"));
		event.getRegistry().register(new Leaderboard.FromStat(StatList.PLAY_ONE_MINUTE, false, Leaderboard.FromStat.TIME).setRegistryName(FTBUFinals.MOD_ID + ":time_played"));

		event.getRegistry().register(new Leaderboard(
				new TextComponentTranslation("ftbutilities.stat.dph"),
				player ->
				{
					double d = getDPH(player);
					return new TextComponentString(d < 0D ? "-" : String.format("%.2f", d));
				},
				Comparator.comparingDouble(FTBULeaderboards::getDPH).reversed(),
				player -> getDPH(player) >= 0D)
				.setRegistryName(FTBUFinals.MOD_ID + ":deaths_per_hour"));

		event.getRegistry().register(new Leaderboard(
				new TextComponentTranslation("ftbutilities.stat.last_seen"),
				player ->
				{
					if (player.isOnline())
					{
						ITextComponent component = GuiLang.ONLINE.textComponent(null);
						component.getStyle().setColor(TextFormatting.GREEN);
						return component;
					}
					else
					{
						long worldTime = CommonUtils.getWorldTime();
						int time = (int) (worldTime - player.getLastTimeSeen());
						return Leaderboard.FromStat.TIME.apply(time);
					}
				},
				Comparator.comparingLong(FTBULeaderboards::getRelativeLastSeen),
				player -> player.getLastTimeSeen() != 0L)
				.setRegistryName(FTBUFinals.MOD_ID + ":last_seen"));
	}

	private static long getRelativeLastSeen(ForgePlayer player)
	{
		long worldTime = CommonUtils.getWorldTime();
		if (player.isOnline())
		{
			return 0;
		}

		return worldTime - player.getLastTimeSeen();
	}

	private static double getDPH(ForgePlayer player)
	{
		int playTime = player.stats().readStat(StatList.PLAY_ONE_MINUTE);

		if (playTime > 0)
		{
			double hours = (double) playTime / CommonUtils.TICKS_HOUR;

			if (hours >= 1D)
			{
				return (double) player.stats().readStat(StatList.DEATHS) / hours;
			}
		}

		return -1D;
	}
}