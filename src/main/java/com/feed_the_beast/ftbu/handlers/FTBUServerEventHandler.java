package com.feed_the_beast.ftbu.handlers;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.gui.GuiLang;
import com.feed_the_beast.ftbl.lib.util.text_components.TextComponentCountdown;
import com.feed_the_beast.ftbu.FTBUCommon;
import com.feed_the_beast.ftbu.FTBUConfig;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.api.FTBUtilitiesAPI;
import com.feed_the_beast.ftbu.api.IRank;
import com.feed_the_beast.ftbu.api.Leaderboard;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.Comparator;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = FTBUFinals.MOD_ID)
public class FTBUServerEventHandler
{
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onServerChatEvent(ServerChatEvent event)
	{
		String msg = event.getMessage().trim();

		if (FTBUConfig.ranks.override_chat)
		{
			IRank rank = FTBUtilitiesAPI.API.getRank(event.getPlayer().getGameProfile());

			ITextComponent main = new TextComponentString("");
			ITextComponent name = new TextComponentString(rank.getFormattedName(event.getPlayer().getDisplayNameString()));

			name.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + event.getPlayer().getName() + " "));

			NBTTagCompound hoverNBT = new NBTTagCompound();
			String s = EntityList.getEntityString(event.getPlayer());
			hoverNBT.setString("id", event.getPlayer().getCachedUniqueIdString());

			if (s != null)
			{
				hoverNBT.setString("type", s);
			}

			hoverNBT.setString("name", event.getPlayer().getName());

			name.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new TextComponentString(hoverNBT.toString())));
			name.getStyle().setInsertion(event.getPlayer().getName());

			main.appendSibling(name);
			main.appendSibling(ForgeHooks.newChatWithLinks(msg));

			event.setComponent(main);
		}
	}

	@SubscribeEvent
	public static void registerRegistries(RegistryEvent.NewRegistry event)
	{
		FTBUCommon.LEADERBOARDS = new RegistryBuilder<Leaderboard>().setName(FTBUFinals.get("leaderboards")).setType(Leaderboard.class).setMaxID(255).create();
	}

	@SubscribeEvent
	public static void registerLeaderboards(RegistryEvent.Register<Leaderboard> event)
	{
		event.getRegistry().register(new Leaderboard.FromStat(StatList.DEATHS, false).setRegistryName(FTBUFinals.MOD_ID + ":deaths"));
		event.getRegistry().register(new Leaderboard.FromStat(StatList.MOB_KILLS, false).setRegistryName(FTBUFinals.MOD_ID + ":mob_kills"));
		event.getRegistry().register(new Leaderboard.FromStat(StatList.PLAY_ONE_MINUTE, false).setRegistryName(FTBUFinals.MOD_ID + ":time_played"));

		event.getRegistry().register(new Leaderboard(
				new TextComponentTranslation("ftbu.stat.dph"),
				player -> new TextComponentString(String.format("%.2f", getDPH(player))),
				Comparator.comparingDouble(FTBUServerEventHandler::getDPH).reversed(),
				player -> getDPH(player) > 0D)
				.setRegistryName(FTBUFinals.MOD_ID + ":deaths_per_hour"));

		event.getRegistry().register(new Leaderboard(
				new TextComponentTranslation("ftbu.stat.last_seen"),
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
						return new TextComponentCountdown(player.getLastTimeSeen());
					}
				},
				Comparator.comparingLong(IForgePlayer::getLastTimeSeen),
				player -> player.getLastTimeSeen() != 0L)
				.setRegistryName(FTBUFinals.MOD_ID + ":last_seen"));
	}

	private static double getDPH(IForgePlayer player)
	{
		int deaths = player.stats().readStat(StatList.DEATHS);
		int playTime = player.stats().readStat(StatList.PLAY_ONE_MINUTE);
		return deaths > 0 && playTime > 0 ? (deaths / (playTime / 60D)) : 0D;
	}
}