package com.feed_the_beast.ftbutilities.data;

import com.feed_the_beast.ftblib.FTBLib;
import com.feed_the_beast.ftblib.lib.EnumMessageLocation;
import com.feed_the_beast.ftblib.lib.config.ConfigBoolean;
import com.feed_the_beast.ftblib.lib.config.ConfigEnum;
import com.feed_the_beast.ftblib.lib.config.ConfigGroup;
import com.feed_the_beast.ftblib.lib.config.ConfigString;
import com.feed_the_beast.ftblib.lib.config.RankConfigAPI;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.ForgeTeam;
import com.feed_the_beast.ftblib.lib.data.IHasCache;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftblib.lib.math.BlockDimPos;
import com.feed_the_beast.ftblib.lib.math.TeleporterDimPos;
import com.feed_the_beast.ftblib.lib.math.Ticks;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftblib.lib.util.misc.IScheduledTask;
import com.feed_the_beast.ftblib.lib.util.misc.Node;
import com.feed_the_beast.ftblib.lib.util.misc.TimeType;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.FTBUtilitiesConfig;
import com.feed_the_beast.ftbutilities.FTBUtilitiesPermissions;
import com.feed_the_beast.ftbutilities.ranks.Rank;
import com.google.gson.JsonElement;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author LatvianModder
 */
public class FTBUtilitiesPlayerData implements INBTSerializable<NBTTagCompound>, IHasCache
{
	public enum Timer
	{
		HOME(FTBUtilitiesPermissions.HOMES_COOLDOWN, FTBUtilitiesPermissions.HOMES_WARMUP),
		WARP(FTBUtilitiesPermissions.WARPS_COOLDOWN, FTBUtilitiesPermissions.WARPS_WARMUP),
		BACK(FTBUtilitiesPermissions.BACK_COOLDOWN, FTBUtilitiesPermissions.BACK_WARMUP),
		SPAWN(FTBUtilitiesPermissions.SPAWN_COOLDOWN, FTBUtilitiesPermissions.SPAWN_WARMUP),
		TPA(FTBUtilitiesPermissions.TPA_COOLDOWN, FTBUtilitiesPermissions.TPA_WARMUP);

		public static final Timer[] VALUES = values();

		private final Node cooldown;
		private final Node warmup;

		Timer(Node c, Node w)
		{
			cooldown = c;
			warmup = w;
		}

		public void teleport(EntityPlayerMP player, TeleporterDimPos pos, @Nullable IScheduledTask extraTask)
		{
			Universe universe = Universe.get();
			int seconds = (int) (Ticks.ts(RankConfigAPI.get(player, warmup).getLong()));

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

		public void teleport(EntityPlayerMP player, BlockDimPos pos, @Nullable IScheduledTask extraTask)
		{
			teleport(player, pos.teleporter(), extraTask);
		}
	}

	private static class TeleportTask implements IScheduledTask
	{
		private final EntityPlayerMP player;
		private final Timer timer;
		private final BlockDimPos startPos;
		private final TeleporterDimPos pos;
		private final float startHP;
		private final int startSeconds, secondsLeft;
		private final IScheduledTask extraTask;

		private TeleportTask(EntityPlayerMP p, Timer t, int ss, int s, TeleporterDimPos to, @Nullable IScheduledTask e)
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
			if (!startPos.equalsPos(new BlockDimPos(player)) || startHP != player.getHealth())
			{
				player.sendStatusMessage(StringUtils.color(FTBLib.lang(player, "stand_still_failed"), TextFormatting.RED), true);
			}
			else if (secondsLeft <= 1)
			{
				pos.teleport(player);
				FTBUtilitiesPlayerData data = FTBUtilitiesPlayerData.get(universe.getPlayer(player));
				data.lastTeleport[timer.ordinal()] = universe.world.getTotalWorldTime();

				if (secondsLeft != 0)
				{
					player.sendStatusMessage(FTBLib.lang(player, "teleporting"), true);
				}

				if (extraTask != null)
				{
					extraTask.execute(universe);
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

	public final ForgePlayer player;

	private final ConfigBoolean renderBadge = new ConfigBoolean(true);
	private final ConfigBoolean disableGlobalBadge = new ConfigBoolean(false);
	private final ConfigBoolean enablePVP = new ConfigBoolean(true);
	private final ConfigString nickname = new ConfigString("");
	private final ConfigEnum<EnumMessageLocation> afkMesageLocation = new ConfigEnum<>(EnumMessageLocation.NAME_MAP);

	public ForgeTeam lastChunkTeam;
	public final Collection<ForgePlayer> tpaRequestsFrom;
	public long afkTicks;
	private ITextComponent cachedNameForChat;

	private BlockDimPos lastDeath, lastSafePos;
	private long[] lastTeleport;
	public final BlockDimPosStorage homes;
	private boolean fly;

	public FTBUtilitiesPlayerData(ForgePlayer p)
	{
		player = p;
		homes = new BlockDimPosStorage();
		tpaRequestsFrom = new HashSet<>();
		lastTeleport = new long[Timer.VALUES.length];
	}

	public static FTBUtilitiesPlayerData get(ForgePlayer player)
	{
		return player.getData().get(FTBUtilities.MOD_ID);
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("RenderBadge", renderBadge.getBoolean());
		nbt.setBoolean("DisableGlobalBadges", disableGlobalBadge.getBoolean());
		nbt.setBoolean("EnablePVP", enablePVP.getBoolean());
		nbt.setTag("Homes", homes.serializeNBT());
		nbt.setBoolean("AllowFlying", fly);

		if (lastDeath != null)
		{
			nbt.setIntArray("LastDeath", lastDeath.toIntArray());
		}

		nbt.setString("Nickname", nickname.getString());
		nbt.setString("AFK", afkMesageLocation.getString());
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		renderBadge.setBoolean(!nbt.hasKey("RenderBadge") || nbt.getBoolean("RenderBadge"));
		disableGlobalBadge.setBoolean(nbt.getBoolean("DisableGlobalBadges"));
		enablePVP.setBoolean(!nbt.hasKey("EnablePVP") || nbt.getBoolean("EnablePVP"));
		homes.deserializeNBT(nbt.getCompoundTag("Homes"));
		fly = nbt.getBoolean("AllowFlying");
		lastDeath = BlockDimPos.fromIntArray(nbt.getIntArray("LastDeath"));
		nickname.setString(nbt.getString("Nickname"));
		afkMesageLocation.setValue(nbt.getString("AFK"));
	}

	public void addConfig(ConfigGroup group)
	{
		group.setGroupName(FTBUtilities.MOD_ID, new TextComponentString(FTBUtilities.MOD_NAME));
		group.add(FTBUtilities.MOD_ID, "render_badge", renderBadge);
		group.add(FTBUtilities.MOD_ID, "disable_global_badge", disableGlobalBadge);
		group.add(FTBUtilities.MOD_ID, "enable_pvp", enablePVP);

		if (FTBUtilitiesConfig.commands.nick && player.hasPermission(FTBUtilitiesPermissions.NICKNAME_SET))
		{
			group.add(FTBUtilities.MOD_ID, "nickname", nickname);
		}

		if (FTBUtilitiesConfig.afk.isEnabled(player.team.universe.server))
		{
			group.add(FTBUtilities.MOD_ID, "afk", afkMesageLocation);
		}
	}

	public boolean renderBadge()
	{
		return renderBadge.getBoolean();
	}

	public boolean disableGlobalBadge()
	{
		return disableGlobalBadge.getBoolean();
	}

	public boolean enablePVP()
	{
		return enablePVP.getBoolean();
	}

	public String getNickname()
	{
		return nickname.getString();
	}

	public void setNickname(String name)
	{
		nickname.setString(name.equals(player.getName()) ? "" : name);
		player.markDirty();
		clearCache();
	}

	public EnumMessageLocation getAFKMessageLocation()
	{
		return afkMesageLocation.getValue();
	}

	public void setFly(boolean v)
	{
		fly = v;
		player.markDirty();
	}

	public boolean getFly()
	{
		return fly;
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
		long cooldown = lastTeleport[timer.ordinal()] + player.getRankConfig(timer.cooldown).getLong() - player.team.universe.world.getTotalWorldTime();

		if (cooldown > 0)
		{
			throw FTBLib.error(sender, "cant_use_now_cooldown", StringUtils.getTimeStringTicks(cooldown));
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

	public ITextComponent getNameForChat(Rank rank)
	{
		if (cachedNameForChat != null)
		{
			return cachedNameForChat;
		}

		cachedNameForChat = new TextComponentString("");

		JsonElement json0 = rank.getConfigRaw(FTBUtilitiesPermissions.CHAT_PREFIX_PART_COUNT);
		int partCount = json0.isJsonPrimitive() ? json0.getAsInt() : 0;

		if (partCount <= 0)
		{
			cachedNameForChat.appendText("<");
		}
		else
		{
			for (int i = 0; i < partCount; i++)
			{
				FTBUtilitiesPermissions.ChatPart chatPart = new FTBUtilitiesPermissions.ChatPart("prefix." + (i + 1));
				json0 = rank.getConfigRaw(chatPart.text);

				if (json0.isJsonPrimitive())
				{
					cachedNameForChat.appendSibling(chatPart.format(rank, new TextComponentString(json0.getAsString()), FTBUtilitiesPermissions.CHAT_PREFIX));
				}
			}
		}

		json0 = rank.getConfigRaw(FTBUtilitiesPermissions.CHAT_NAME.text);

		if (json0.isJsonPrimitive() && !json0.getAsString().isEmpty())
		{
			cachedNameForChat.appendSibling(FTBUtilitiesPermissions.CHAT_NAME.format(rank, new TextComponentString(json0.getAsString()), null));
		}
		else
		{
			cachedNameForChat.appendSibling(FTBUtilitiesPermissions.CHAT_NAME.format(rank, player.getDisplayName(), null));
		}

		json0 = rank.getConfigRaw(FTBUtilitiesPermissions.CHAT_SUFFIX_PART_COUNT);
		partCount = json0.isJsonPrimitive() ? json0.getAsInt() : 0;

		if (partCount <= 0)
		{
			cachedNameForChat.appendText(">");
		}
		else
		{
			for (int i = 0; i < partCount; i++)
			{
				FTBUtilitiesPermissions.ChatPart chatPart = new FTBUtilitiesPermissions.ChatPart("suffix." + (i + 1));
				json0 = rank.getConfigRaw(chatPart.text);

				if (json0.isJsonPrimitive())
				{
					cachedNameForChat.appendSibling(chatPart.format(rank, new TextComponentString(json0.getAsString()), FTBUtilitiesPermissions.CHAT_SUFFIX));
				}
			}
		}

		cachedNameForChat.appendText(" ");
		return cachedNameForChat;
	}
}