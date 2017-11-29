package com.feed_the_beast.ftbu.handlers;

import com.feed_the_beast.ftbl.api.EventHandler;
import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.IForgeTeam;
import com.feed_the_beast.ftbl.api.RegisterDataProvidersEvent;
import com.feed_the_beast.ftbl.api.RegisterOptionalServerModsEvent;
import com.feed_the_beast.ftbl.api.RegisterSyncDataEvent;
import com.feed_the_beast.ftbl.api.ServerReloadEvent;
import com.feed_the_beast.ftbl.api.player.ForgePlayerConfigEvent;
import com.feed_the_beast.ftbl.api.player.ForgePlayerLoggedInEvent;
import com.feed_the_beast.ftbl.api.player.ForgePlayerLoggedOutEvent;
import com.feed_the_beast.ftbl.api.team.ForgeTeamConfigEvent;
import com.feed_the_beast.ftbl.api.team.ForgeTeamDeletedEvent;
import com.feed_the_beast.ftbl.api.team.ForgeTeamOwnerChangedEvent;
import com.feed_the_beast.ftbl.api.team.ForgeTeamPlayerJoinedEvent;
import com.feed_the_beast.ftbl.api.team.ForgeTeamPlayerLeftEvent;
import com.feed_the_beast.ftbl.api.team.RegisterTeamGuiActionsEvent;
import com.feed_the_beast.ftbl.lib.gui.GuiIcons;
import com.feed_the_beast.ftbl.lib.util.InvUtils;
import com.feed_the_beast.ftbl.lib.util.misc.TeamGuiAction;
import com.feed_the_beast.ftbu.FTBUConfig;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.ServerInfoPage;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunks;
import com.feed_the_beast.ftbu.ranks.Ranks;
import com.feed_the_beast.ftbu.util.Badges;
import com.feed_the_beast.ftbu.util.FTBUPlayerData;
import com.feed_the_beast.ftbu.util.FTBUTeamData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author LatvianModder
 */
@EventHandler
public class FTBLibIntegration
{
	public static final ResourceLocation FTBU_DATA = FTBUFinals.get("data");
	public static final ResourceLocation RELOAD_CONFIG = FTBUFinals.get("config");
	public static final ResourceLocation RELOAD_RANKS = FTBUFinals.get("ranks");
	public static final ResourceLocation RELOAD_SERVER_INFO = FTBUFinals.get("server_info");
	public static final ResourceLocation RELOAD_BADGES = FTBUFinals.get("badges");

	@SubscribeEvent
	public static void registerReloadIds(ServerReloadEvent.RegisterIds event)
	{
		event.register(RELOAD_CONFIG);
		event.register(RELOAD_RANKS);
		event.register(RELOAD_SERVER_INFO);
		event.register(RELOAD_BADGES);
	}

	@SubscribeEvent
	public static void onServerReload(ServerReloadEvent event)
	{
		if (event.reload(RELOAD_CONFIG))
		{
			FTBUConfig.sync();
		}

		if (event.reload(RELOAD_RANKS) && !Ranks.reload())
		{
			event.failedToReload(RELOAD_RANKS);
		}

		if (event.reload(RELOAD_SERVER_INFO))
		{
			ServerInfoPage.reloadCachedInfo();
		}

		if (event.reload(RELOAD_BADGES) && !Badges.reloadServerBadges())
		{
			event.failedToReload(RELOAD_BADGES);
		}
	}

	@SubscribeEvent
	public static void registerOptionalServerMod(RegisterOptionalServerModsEvent event)
	{
		event.register(FTBUFinals.MOD_ID);
	}

	@SubscribeEvent
	public static void registerPlayerDataProvider(RegisterDataProvidersEvent.Player event)
	{
		event.register(FTBU_DATA, FTBUPlayerData::new);
	}

	@SubscribeEvent
	public static void registerTeamDataProvider(RegisterDataProvidersEvent.Team event)
	{
		event.register(FTBU_DATA, FTBUTeamData::new);
	}

	@SubscribeEvent
	public static void registerSyncData(RegisterSyncDataEvent event)
	{
		event.register(FTBUFinals.MOD_ID, new FTBUSyncData());
	}

	@SubscribeEvent
	public static void onPlayerLoggedIn(ForgePlayerLoggedInEvent event)
	{
		if (event.getPlayer().isFake())
		{
			return;
		}

		EntityPlayerMP ep = event.getPlayer().getPlayer();

		if (event.isFirstLogin())
		{
			if (FTBUConfig.login.enable_starting_items)
			{
				for (ItemStack stack : FTBUConfig.login.getStartingItems())
				{
					InvUtils.giveItem(ep, stack.copy());
				}
			}
		}

		if (FTBUConfig.login.enable_motd)
		{
			for (ITextComponent t : FTBUConfig.login.getMOTD())
			{
				ep.sendMessage(t);
			}
		}

		ClaimedChunks.INSTANCE.markDirty();
	}

	@SubscribeEvent
	public static void onPlayerLoggedOut(ForgePlayerLoggedOutEvent event)
	{
		ClaimedChunks.INSTANCE.markDirty();
		Badges.update(event.getPlayer().getId());
	}

	@SubscribeEvent
	public static void getPlayerSettings(ForgePlayerConfigEvent event)
	{
		FTBUPlayerData.get(event.getPlayer()).addConfig(event);
	}
	
	/*
	public void printMessage(@Nullable IForgePlayer from, ITextComponent message)
	{
		ITextComponent name = StringUtils.color(new TextComponentString(Universe.INSTANCE.getPlayer(message.getSender()).getProfile().getName()), color.getValue().getTextFormatting());
		ITextComponent msg = FTBLibLang.TEAM_CHAT_MESSAGE.textComponent(name, message);
		msg.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, FTBLibLang.CLICK_HERE.textComponent()));
		msg.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/team msg "));

		for (EntityPlayerMP ep : getOnlineTeamPlayers(EnumTeamStatus.MEMBER))
		{
			ep.sendMessage(msg);
		}
	}*/

	@SubscribeEvent
	public static void getTeamSettings(ForgeTeamConfigEvent event)
	{
		FTBUTeamData.get(event.getTeam()).addConfig(event);
	}

	@SubscribeEvent
	public static void onTeamDeleted(ForgeTeamDeletedEvent event)
	{
		//printMessage(FTBLibLang.TEAM_DELETED.textComponent(getTitle()));
		ClaimedChunks.INSTANCE.unclaimAllChunks(event.getTeam(), null);
	}

	@SubscribeEvent
	public static void onTeamPlayerJoined(ForgeTeamPlayerJoinedEvent event)
	{
		//printMessage(FTBLibLang.TEAM_MEMBER_JOINED.textComponent(player.getName()));
	}

	@SubscribeEvent
	public static void onTeamPlayerLeft(ForgeTeamPlayerLeftEvent event)
	{
		//printMessage(FTBLibLang.TEAM_MEMBER_LEFT.textComponent(player.getName()));
	}

	@SubscribeEvent
	public static void onTeamOwnerChanged(ForgeTeamOwnerChangedEvent event)
	{
		//printMessage(FTBLibLang.TEAM_TRANSFERRED_OWNERSHIP.textComponent(p1.getName()));
	}

	@SubscribeEvent
	public static void registerTeamGuiActions(RegisterTeamGuiActionsEvent event)
	{
		event.register(new TeamGuiAction(FTBUFinals.get("chat"), new TextComponentTranslation("sidebar_button.ftbu.chats.team"), GuiIcons.CHAT, -10)
		{
			@Override
			public boolean isAvailable(IForgeTeam team, IForgePlayer player, NBTTagCompound data)
			{
				return false;
			}

			@Override
			public void onAction(IForgeTeam team, IForgePlayer player, NBTTagCompound data)
			{
			}
		});
	}
}