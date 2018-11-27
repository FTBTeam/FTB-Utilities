package com.feed_the_beast.ftbutilities.data;

import com.feed_the_beast.ftblib.FTBLib;
import com.feed_the_beast.ftblib.FTBLibCommon;
import com.feed_the_beast.ftblib.events.player.ForgePlayerConfigEvent;
import com.feed_the_beast.ftblib.events.player.ForgePlayerDataEvent;
import com.feed_the_beast.ftblib.events.player.ForgePlayerLoggedInEvent;
import com.feed_the_beast.ftblib.events.player.ForgePlayerLoggedOutEvent;
import com.feed_the_beast.ftblib.lib.EnumMessageLocation;
import com.feed_the_beast.ftblib.lib.config.ConfigGroup;
import com.feed_the_beast.ftblib.lib.config.RankConfigAPI;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.ForgeTeam;
import com.feed_the_beast.ftblib.lib.data.PlayerData;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftblib.lib.math.BlockDimPos;
import com.feed_the_beast.ftblib.lib.math.TeleporterDimPos;
import com.feed_the_beast.ftblib.lib.util.ServerUtils;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftblib.lib.util.misc.IScheduledTask;
import com.feed_the_beast.ftblib.lib.util.misc.Node;
import com.feed_the_beast.ftblib.lib.util.misc.TimeType;
import com.feed_the_beast.ftblib.lib.util.text_components.TextComponentParser;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.FTBUtilitiesConfig;
import com.feed_the_beast.ftbutilities.FTBUtilitiesPermissions;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Function;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = FTBUtilities.MOD_ID)
public class FTBUtilitiesPlayerData extends PlayerData
{
	public static final String TAG_FLY = "fly";
	public static final String TAG_MUTED = "muted";
	public static final String TAG_LAST_CHUNK = "ftbu_lchunk";

	public enum Timer
	{
		HOME(FTBUtilitiesPermissions.HOMES_COOLDOWN, FTBUtilitiesPermissions.HOMES_WARMUP),
		WARP(FTBUtilitiesPermissions.WARPS_COOLDOWN, FTBUtilitiesPermissions.WARPS_WARMUP),
		BACK(FTBUtilitiesPermissions.BACK_COOLDOWN, FTBUtilitiesPermissions.BACK_WARMUP),
		SPAWN(FTBUtilitiesPermissions.SPAWN_COOLDOWN, FTBUtilitiesPermissions.SPAWN_WARMUP),
		TPA(FTBUtilitiesPermissions.TPA_COOLDOWN, FTBUtilitiesPermissions.TPA_WARMUP),
		RTP(FTBUtilitiesPermissions.RTP_COOLDOWN, FTBUtilitiesPermissions.RTP_WARMUP);

		public static final Timer[] VALUES = values();

		private final Node cooldown;
		private final Node warmup;

		Timer(Node c, Node w)
		{
			cooldown = c;
			warmup = w;
		}

		public void teleport(EntityPlayerMP player, Function<EntityPlayerMP, TeleporterDimPos> pos, @Nullable IScheduledTask extraTask)
		{
			Universe universe = Universe.get();
			int seconds = (int) RankConfigAPI.get(player, warmup).getTimer().seconds();

			if (seconds > 0)
			{
				player.sendStatusMessage(StringUtils.color(FTBLib.lang(player, "stand_still", seconds).appendText(" [" + seconds + "]"), TextFormatting.GOLD), true);
				universe.scheduleTask(TimeType.MILLIS, System.currentTimeMillis() + 1000L, new TeleportTask(player, this, seconds, seconds, pos, extraTask));
			}
			else
			{
				new TeleportTask(player, this, 0, 0, pos, extraTask).execute(universe);
			}
		}
	}

	private static class TeleportTask implements IScheduledTask
	{
		private final EntityPlayerMP player;
		private final Timer timer;
		private final BlockDimPos startPos;
		private final Function<EntityPlayerMP, TeleporterDimPos> pos;
		private final float startHP;
		private final int startSeconds, secondsLeft;
		private final IScheduledTask extraTask;

		private TeleportTask(EntityPlayerMP p, Timer t, int ss, int s, Function<EntityPlayerMP, TeleporterDimPos> to, @Nullable IScheduledTask e)
		{
			player = p;
			timer = t;
			startPos = new BlockDimPos(player);
			startHP = player.getHealth();
			pos = to;
			startSeconds = ss;
			secondsLeft = s;
			extraTask = e;
		}

		@Override
		public void execute(Universe universe)
		{
			if (!startPos.equalsPos(new BlockDimPos(player)) || startHP > player.getHealth())
			{
				player.sendStatusMessage(StringUtils.color(FTBLib.lang(player, "stand_still_failed"), TextFormatting.RED), true);
			}
			else if (secondsLeft <= 1)
			{
				TeleporterDimPos teleporter = pos.apply(player);

				if (teleporter != null)
				{
					teleporter.teleport(player);

					if (player.getRidingEntity() != null)
					{
						teleporter.teleport(player.getRidingEntity());
					}

					FTBUtilitiesPlayerData data = get(universe.getPlayer(player));
					data.lastTeleport[timer.ordinal()] = System.currentTimeMillis();

					if (secondsLeft != 0)
					{
						player.sendStatusMessage(FTBLib.lang(player, "teleporting"), true);
					}

					if (extraTask != null)
					{
						extraTask.execute(universe);
					}
				}
			}
			else
			{
				universe.scheduleTask(TimeType.MILLIS, System.currentTimeMillis() + 1000L, new TeleportTask(player, timer, startSeconds, secondsLeft - 1, pos, extraTask));
				player.sendStatusMessage(new TextComponentString(Integer.toString(secondsLeft - 1)), true);
				player.sendStatusMessage(StringUtils.color(FTBLib.lang(player, "stand_still", startSeconds).appendText(" [" + (secondsLeft - 1) + "]"), TextFormatting.GOLD), true);
			}
		}
	}

	public static FTBUtilitiesPlayerData get(ForgePlayer player)
	{
		return player.getData().get(FTBUtilities.MOD_ID);
	}

	@SubscribeEvent
	public static void registerPlayerData(ForgePlayerDataEvent event)
	{
		event.register(new FTBUtilitiesPlayerData(event.getPlayer()));
	}

	@SubscribeEvent
	public static void onPlayerLoggedIn(ForgePlayerLoggedInEvent event)
	{
		EntityPlayerMP player = event.getPlayer().getPlayer();

		if (ServerUtils.isFirstLogin(player, "ftbutilities_starting_items"))
		{
			if (FTBUtilitiesConfig.login.enable_starting_items)
			{
				for (ItemStack stack : FTBUtilitiesConfig.login.getStartingItems())
				{
					ItemHandlerHelper.giveItemToPlayer(player, stack.copy());
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

		EntityPlayerMP player = event.getPlayer().getPlayer();
		FTBUtilitiesUniverseData.updateBadge(player.getUniqueID());
		player.getEntityData().removeTag(TAG_LAST_CHUNK);
	}

	@SubscribeEvent
	public static void onPlayerClone(PlayerEvent.Clone event)
	{
		event.getEntity().getEntityData().removeTag(TAG_LAST_CHUNK);
	}

	@SubscribeEvent
	public static void getPlayerSettings(ForgePlayerConfigEvent event)
	{
		get(event.getPlayer()).addConfig(event.getConfig());
	}

	private boolean renderBadge = true;
	private boolean disableGlobalBadge = false;
	private boolean enablePVP = true;
	private String nickname = "";
	private EnumMessageLocation afkMesageLocation = EnumMessageLocation.CHAT;

	public ForgeTeam lastChunkTeam;
	public final Collection<ForgePlayer> tpaRequestsFrom;
	public long afkTime;
	private ITextComponent cachedNameForChat;

	private BlockDimPos lastDeath, lastSafePos;
	private long[] lastTeleport;
	public final BlockDimPosStorage homes;

	private FTBUtilitiesPlayerData(ForgePlayer player)
	{
		super(player);
		homes = new BlockDimPosStorage();
		tpaRequestsFrom = new HashSet<>();
		lastTeleport = new long[Timer.VALUES.length];
	}

	@Override
	public String getID()
	{
		return FTBUtilities.MOD_ID;
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("RenderBadge", renderBadge);
		nbt.setBoolean("DisableGlobalBadges", disableGlobalBadge);
		nbt.setBoolean("EnablePVP", enablePVP);
		nbt.setTag("Homes", homes.serializeNBT());

		if (lastDeath != null)
		{
			nbt.setIntArray("LastDeath", lastDeath.toIntArray());
		}

		nbt.setString("Nickname", nickname);
		nbt.setString("AFK", EnumMessageLocation.NAME_MAP.getName(afkMesageLocation));
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		renderBadge = !nbt.hasKey("RenderBadge") || nbt.getBoolean("RenderBadge");
		disableGlobalBadge = nbt.getBoolean("DisableGlobalBadges");
		enablePVP = !nbt.hasKey("EnablePVP") || nbt.getBoolean("EnablePVP");
		homes.deserializeNBT(nbt.getCompoundTag("Homes"));
		lastDeath = BlockDimPos.fromIntArray(nbt.getIntArray("LastDeath"));
		nickname = nbt.getString("Nickname");
		afkMesageLocation = EnumMessageLocation.NAME_MAP.get(nbt.getString("AFK"));
	}

	private void addConfig(ConfigGroup main)
	{
		ConfigGroup config = main.getGroup(FTBUtilities.MOD_ID);
		config.setDisplayName(new TextComponentString(FTBUtilities.MOD_NAME));

		config.addBool("render_badge", () -> renderBadge, v -> renderBadge = v, true);
		config.addBool("disable_global_badge", () -> disableGlobalBadge, v -> disableGlobalBadge = v, false);
		config.addBool("enable_pvp", () -> enablePVP, v -> enablePVP = v, true);

		if (FTBUtilitiesConfig.commands.nick && player.hasPermission(FTBUtilitiesPermissions.CHAT_NICKNAME_SET))
		{
			config.addString("nickname", () -> nickname, v -> nickname = v, "");
		}

		if (FTBUtilitiesConfig.afk.isEnabled(player.team.universe.server))
		{
			config.addEnum("afk", () -> afkMesageLocation, v -> afkMesageLocation = v, EnumMessageLocation.NAME_MAP);
		}
	}

	public boolean renderBadge()
	{
		return renderBadge;
	}

	public boolean disableGlobalBadge()
	{
		return disableGlobalBadge;
	}

	public boolean enablePVP()
	{
		return enablePVP;
	}

	public String getNickname()
	{
		return nickname;
	}

	public void setNickname(String name)
	{
		nickname = name.equals(player.getName()) ? "" : name;
		player.markDirty();
		clearCache();
	}

	public EnumMessageLocation getAFKMessageLocation()
	{
		return afkMesageLocation;
	}

	public void setLastDeath(@Nullable BlockDimPos pos)
	{
		lastDeath = pos;
		player.markDirty();
	}

	@Nullable
	public BlockDimPos getLastDeath()
	{
		return lastDeath;
	}

	public void setLastSafePos(@Nullable BlockDimPos pos)
	{
		lastSafePos = pos;
		player.markDirty();
	}

	@Nullable
	public BlockDimPos getLastSafePos()
	{
		return lastSafePos;
	}

	public void checkTeleportCooldown(ICommandSender sender, Timer timer) throws CommandException
	{
		long cooldown = lastTeleport[timer.ordinal()] + player.getRankConfig(timer.cooldown).getTimer().millis() - System.currentTimeMillis();

		if (cooldown > 0)
		{
			throw FTBLib.error(sender, "cant_use_now_cooldown", StringUtils.getTimeString(cooldown));
		}
	}

	@Override
	public void clearCache()
	{
		cachedNameForChat = null;

		if (player.isOnline())
		{
			player.getPlayer().refreshDisplayName();
		}
	}

	public ITextComponent getNameForChat()
	{
		if (cachedNameForChat != null)
		{
			return cachedNameForChat;
		}

		String text = player.getRankConfig(FTBUtilitiesPermissions.CHAT_NAME_FORMAT).getString();

		try
		{
			cachedNameForChat = TextComponentParser.parse(text, FTBLibCommon.chatFormattingSubstituteFunction(player));
		}
		catch (Exception ex)
		{
			String s = "Error parsing " + text + ": " + ex.getLocalizedMessage();
			FTBUtilities.LOGGER.error(s);
			cachedNameForChat = new TextComponentString("BrokenFormatting");
			cachedNameForChat.getStyle().setColor(TextFormatting.RED);
			cachedNameForChat.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(s)));
		}

		cachedNameForChat.appendText(" ");
		return cachedNameForChat;
	}
}