package com.feed_the_beast.ftbutilities.integration;

import com.feed_the_beast.ftblib.events.RegisterAdminPanelActionsEvent;
import com.feed_the_beast.ftblib.events.RegisterSyncDataEvent;
import com.feed_the_beast.ftblib.events.ServerReloadEvent;
import com.feed_the_beast.ftblib.events.player.ForgePlayerConfigEvent;
import com.feed_the_beast.ftblib.events.player.ForgePlayerDataEvent;
import com.feed_the_beast.ftblib.events.player.ForgePlayerLoggedInEvent;
import com.feed_the_beast.ftblib.events.player.ForgePlayerLoggedOutEvent;
import com.feed_the_beast.ftblib.events.team.ForgeTeamConfigEvent;
import com.feed_the_beast.ftblib.events.team.ForgeTeamDataEvent;
import com.feed_the_beast.ftblib.events.team.ForgeTeamDeletedEvent;
import com.feed_the_beast.ftblib.events.team.RegisterTeamGuiActionsEvent;
import com.feed_the_beast.ftblib.events.universe.UniverseClearCacheEvent;
import com.feed_the_beast.ftblib.lib.EventHandler;
import com.feed_the_beast.ftblib.lib.config.ConfigBoolean;
import com.feed_the_beast.ftblib.lib.config.ConfigGroup;
import com.feed_the_beast.ftblib.lib.config.ConfigInt;
import com.feed_the_beast.ftblib.lib.config.ConfigString;
import com.feed_the_beast.ftblib.lib.config.IConfigCallback;
import com.feed_the_beast.ftblib.lib.data.AdminPanelAction;
import com.feed_the_beast.ftblib.lib.data.FTBLibAPI;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.gui.GuiIcons;
import com.feed_the_beast.ftblib.lib.icon.ItemIcon;
import com.feed_the_beast.ftblib.lib.util.CommonUtils;
import com.feed_the_beast.ftblib.lib.util.InvUtils;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.FTBUtilitiesConfig;
import com.feed_the_beast.ftbutilities.FTBUtilitiesPermissions;
import com.feed_the_beast.ftbutilities.data.Badges;
import com.feed_the_beast.ftbutilities.data.ClaimedChunks;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesPlayerData;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesTeamData;
import com.feed_the_beast.ftbutilities.handlers.FTBUtilitiesSyncData;
import com.feed_the_beast.ftbutilities.net.MessageViewCrashList;
import com.feed_the_beast.ftbutilities.ranks.Ranks;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.GameRules;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.util.OptionalInt;

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
	public static void onCacheCleared(UniverseClearCacheEvent event)
	{
		if (Ranks.INSTANCE != null)
		{
			Ranks.INSTANCE.clearCache();
		}
	}

	@SubscribeEvent
	public static void registerPlayerData(ForgePlayerDataEvent event)
	{
		event.register(FTBUtilities.MOD_ID, new FTBUtilitiesPlayerData(event.getPlayer()));
	}

	@SubscribeEvent
	public static void registerTeamData(ForgeTeamDataEvent event)
	{
		event.register(FTBUtilities.MOD_ID, new FTBUtilitiesTeamData(event.getTeam()));
	}

	@SubscribeEvent
	public static void registerSyncData(RegisterSyncDataEvent event)
	{
		event.register(FTBUtilities.MOD_ID, new FTBUtilitiesSyncData());
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

		if (ClaimedChunks.isActive())
		{
			ClaimedChunks.instance.markDirty();
		}
	}

	@SubscribeEvent
	public static void onPlayerLoggedOut(ForgePlayerLoggedOutEvent event)
	{
		if (ClaimedChunks.isActive())
		{
			ClaimedChunks.instance.markDirty();
		}

		Badges.update(event.getPlayer().getId());
	}

	@SubscribeEvent
	public static void getPlayerSettings(ForgePlayerConfigEvent event)
	{
		FTBUtilitiesPlayerData.get(event.getPlayer()).addConfig(event.getConfig());
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
		FTBUtilitiesTeamData.get(event.getTeam()).addConfig(event);
	}

	@SubscribeEvent
	public static void onTeamDeleted(ForgeTeamDeletedEvent event)
	{
		//printMessage(FTBLibLang.TEAM_DELETED.textComponent(getTitle()));\

		if (ClaimedChunks.isActive())
		{
			ClaimedChunks.instance.unclaimAllChunks(event.getTeam(), OptionalInt.empty());
		}
	}

	/*
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
	*/

	@SubscribeEvent
	public static void registerTeamGuiActions(RegisterTeamGuiActionsEvent event)
	{
		/*event.register(new Action(new ResourceLocation(FTBUtilities.MOD_ID+":chat"), new TextComponentTranslation("sidebar_button." + FTBUtilities.MOD_ID + ".chats.team"), GuiIcons.CHAT, -10)
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
		});*/
	}

	@SubscribeEvent
	public static void registerAdminPanelActions(RegisterAdminPanelActionsEvent event)
	{
		event.register(new AdminPanelAction(FTBUtilities.MOD_ID, "crash_reports", ItemIcon.getItemIcon(new ItemStack(Blocks.BARRIER)), 0)
		{
			@Override
			public Type getType(ForgePlayer player, NBTTagCompound data)
			{
				return Type.fromBoolean(player.hasPermission(FTBUtilitiesPermissions.VIEW_CRASH_REPORTS));
			}

			@Override
			public void onAction(ForgePlayer player, NBTTagCompound data)
			{
				new MessageViewCrashList(new File(CommonUtils.folderMinecraft, "crash-reports")).sendTo(player.getPlayer());
			}
		});

		event.register(new AdminPanelAction(FTBUtilities.MOD_ID, "edit_world", GuiIcons.GLOBE, 0)
		{
			@Override
			public Type getType(ForgePlayer player, NBTTagCompound data)
			{
				return Type.fromBoolean(player.hasPermission(FTBUtilitiesPermissions.EDIT_WORLD_GAMERULES));
			}

			@Override
			public void onAction(ForgePlayer player, NBTTagCompound data)
			{
				ConfigGroup group = new ConfigGroup(new TextComponentTranslation("admin_panel.ftbutilities.edit_world"));

				if (player.hasPermission(FTBUtilitiesPermissions.EDIT_WORLD_GAMERULES))
				{
					GameRules gameRules = player.team.universe.world.getGameRules();

					for (String key : gameRules.getRules())
					{
						switch (getType(gameRules, key))
						{
							case BOOLEAN_VALUE:
								group.add("gamerules", key, new ConfigBoolean(gameRules.getBoolean(key))
								{
									@Override
									public boolean getBoolean()
									{
										return gameRules.getBoolean(key);
									}

									@Override
									public void setBoolean(boolean value)
									{
										gameRules.setOrCreateGameRule(key, Boolean.toString(value));
									}
								}).setDisplayName(new TextComponentString(StringUtils.camelCaseToWords(key)));
								break;
							case NUMERICAL_VALUE:
								group.add("gamerules", key, new ConfigInt(gameRules.getInt(key))
								{
									@Override
									public int getInt()
									{
										return gameRules.getInt(key);
									}

									@Override
									public void setInt(int value)
									{
										gameRules.setOrCreateGameRule(key, Integer.toString(value));
									}
								}).setDisplayName(new TextComponentString(StringUtils.camelCaseToWords(key)));
								break;
							default:
								group.add("gamerules", key, new ConfigString(gameRules.getString(key))
								{
									@Override
									public String getString()
									{
										return gameRules.getString(key);
									}

									@Override
									public void setString(String value)
									{
										gameRules.setOrCreateGameRule(key, value);
									}
								}).setDisplayName(new TextComponentString(StringUtils.camelCaseToWords(key)));
						}
					}
				}

				FTBLibAPI.editServerConfig(player.getPlayer(), group, IConfigCallback.DEFAULT);
			}

			private GameRules.ValueType getType(GameRules gameRules, String key)
			{
				if (gameRules.areSameType(key, GameRules.ValueType.BOOLEAN_VALUE))
				{
					return GameRules.ValueType.BOOLEAN_VALUE;
				}
				else if (gameRules.areSameType(key, GameRules.ValueType.NUMERICAL_VALUE))
				{
					return GameRules.ValueType.NUMERICAL_VALUE;
				}
				else if (gameRules.areSameType(key, GameRules.ValueType.FUNCTION))
				{
					return GameRules.ValueType.FUNCTION;
				}

				return GameRules.ValueType.ANY_VALUE;
			}
		});
	}
}