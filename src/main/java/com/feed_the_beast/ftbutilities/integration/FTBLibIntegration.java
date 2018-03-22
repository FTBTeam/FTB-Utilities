package com.feed_the_beast.ftbutilities.integration;

import com.feed_the_beast.ftblib.events.RegisterOptionalServerModsEvent;
import com.feed_the_beast.ftblib.events.RegisterSyncDataEvent;
import com.feed_the_beast.ftblib.events.ServerReloadEvent;
import com.feed_the_beast.ftblib.events.player.ForgePlayerConfigEvent;
import com.feed_the_beast.ftblib.events.player.ForgePlayerDataEvent;
import com.feed_the_beast.ftblib.events.player.ForgePlayerLoggedInEvent;
import com.feed_the_beast.ftblib.events.player.ForgePlayerLoggedOutEvent;
import com.feed_the_beast.ftblib.events.team.ForgeTeamConfigEvent;
import com.feed_the_beast.ftblib.events.team.ForgeTeamDataEvent;
import com.feed_the_beast.ftblib.events.team.ForgeTeamDeletedEvent;
import com.feed_the_beast.ftblib.events.team.ForgeTeamOwnerChangedEvent;
import com.feed_the_beast.ftblib.events.team.ForgeTeamPlayerJoinedEvent;
import com.feed_the_beast.ftblib.events.team.ForgeTeamPlayerLeftEvent;
import com.feed_the_beast.ftblib.events.team.RegisterTeamGuiActionsEvent;
import com.feed_the_beast.ftblib.lib.EventHandler;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.TeamGuiAction;
import com.feed_the_beast.ftblib.lib.gui.GuiIcons;
import com.feed_the_beast.ftblib.lib.util.InvUtils;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.FTBUtilitiesConfig;
import com.feed_the_beast.ftbutilities.data.Badges;
import com.feed_the_beast.ftbutilities.data.ClaimedChunks;
import com.feed_the_beast.ftbutilities.data.FTBUPlayerData;
import com.feed_the_beast.ftbutilities.data.FTBUTeamData;
import com.feed_the_beast.ftbutilities.handlers.FTBUSyncData;
import com.feed_the_beast.ftbutilities.ranks.Ranks;
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
	public static final ResourceLocation RELOAD_CONFIG = new ResourceLocation(FTBUtilities.MOD_ID, "config");
	public static final ResourceLocation RELOAD_RANKS = new ResourceLocation(FTBUtilities.MOD_ID, "ranks");
	public static final ResourceLocation RELOAD_BADGES = new ResourceLocation(FTBUtilities.MOD_ID, "badges");
	public static final ResourceLocation LOGIN_STARTING_ITEMS = new ResourceLocation(FTBUtilities.MOD_ID, "starting_items");

	@SubscribeEvent
	public static void registerReloadIds(ServerReloadEvent.RegisterIds event)
	{
		event.register(RELOAD_CONFIG);
		event.register(RELOAD_RANKS);
		event.register(RELOAD_BADGES);
	}

	@SubscribeEvent
	public static void onServerReload(ServerReloadEvent event)
	{
		if (event.reload(RELOAD_CONFIG))
		{
			FTBUtilitiesConfig.sync();
		}

		if (event.reload(RELOAD_RANKS) && !Ranks.INSTANCE.reload())
		{
			event.failedToReload(RELOAD_RANKS);
		}

		if (event.reload(RELOAD_BADGES) && !Badges.reloadServerBadges(event.getUniverse()))
		{
			event.failedToReload(RELOAD_BADGES);
		}
	}

	@SubscribeEvent
	public static void registerOptionalServerMod(RegisterOptionalServerModsEvent event)
	{
		event.register(FTBUtilities.MOD_ID);
	}

	@SubscribeEvent
	public static void registerPlayerData(ForgePlayerDataEvent event)
	{
		event.register(FTBUtilities.MOD_ID, new FTBUPlayerData(event.getPlayer()));
	}

	@SubscribeEvent
	public static void registerTeamData(ForgeTeamDataEvent event)
	{
		event.register(FTBUtilities.MOD_ID, new FTBUTeamData(event.getTeam()));
	}

	@SubscribeEvent
	public static void registerSyncData(RegisterSyncDataEvent event)
	{
		event.register(FTBUtilities.MOD_ID, new FTBUSyncData());
	}

	@SubscribeEvent
	public static void onPlayerLoggedIn(ForgePlayerLoggedInEvent event)
	{
		EntityPlayerMP player = event.getPlayer().getPlayer();

		if (event.isFirstLogin(LOGIN_STARTING_ITEMS))
		{
			if (FTBUtilitiesConfig.login.enable_starting_items)
			{
				for (ItemStack stack : FTBUtilitiesConfig.login.getStartingItems())
				{
					InvUtils.giveItem(player, stack.copy());
				}
			}
		}

		if (FTBUtilitiesConfig.login.enable_motd)
		{
			for (ITextComponent t : FTBUtilitiesConfig.login.getMOTD())
			{
				player.sendMessage(t);
			}
		}

		if (ClaimedChunks.instance != null)
		{
			ClaimedChunks.instance.markDirty();
		}
	}

	@SubscribeEvent
	public static void onPlayerLoggedOut(ForgePlayerLoggedOutEvent event)
	{
		if (ClaimedChunks.instance != null)
		{
			ClaimedChunks.instance.markDirty();
		}

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
		ClaimedChunks.instance.unclaimAllChunks(event.getTeam(), null);
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
		event.register(new TeamGuiAction(new ResourceLocation(FTBUtilities.MOD_ID, "chat"), new TextComponentTranslation("sidebar_button." + FTBUtilities.MOD_ID + ".chats.team"), GuiIcons.CHAT, -10)
		{
			@Override
			public Type getType(ForgePlayer player, NBTTagCompound data)
			{
				return Type.INVISIBLE;
			}

			@Override
			public void onAction(ForgePlayer player, NBTTagCompound data)
			{
			}
		});
	}
}