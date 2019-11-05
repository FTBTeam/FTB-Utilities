package com.feed_the_beast.ftbutilities.ranks;

import com.feed_the_beast.ftblib.lib.config.ConfigNull;
import com.feed_the_beast.ftblib.lib.config.ConfigValue;
import com.feed_the_beast.ftblib.lib.config.RankConfigAPI;
import com.feed_the_beast.ftblib.lib.config.RankConfigValueInfo;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftblib.lib.io.DataReader;
import com.feed_the_beast.ftblib.lib.util.FileUtils;
import com.feed_the_beast.ftblib.lib.util.ServerUtils;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftblib.lib.util.misc.Node;
import com.feed_the_beast.ftbutilities.FTBUtilitiesCommon;
import com.feed_the_beast.ftbutilities.FTBUtilitiesConfig;
import com.feed_the_beast.ftbutilities.FTBUtilitiesPermissions;
import com.feed_the_beast.ftbutilities.data.NodeEntry;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.server.permission.DefaultPermissionHandler;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class Ranks
{
	public static Ranks INSTANCE;
	public static Pattern RANK_NAME_PATTERN = Pattern.compile("^[a-z0-9_]+$");

	public static boolean isActive()
	{
		return FTBUtilitiesConfig.ranks.enabled && INSTANCE != null && PermissionAPI.getPermissionHandler() == FTBUtilitiesPermissionHandler.INSTANCE;
	}

	public static boolean isValidName(@Nullable String id)
	{
		return id != null && !id.isEmpty() && !id.equals("none") && RANK_NAME_PATTERN.matcher(id).matches();
	}

	public final Universe universe;
	public final Map<String, Rank> ranks;
	private Collection<String> rankNames;
	private Collection<String> permissionNodes;
	public final Map<UUID, PlayerRank> playerRanks;
	private Rank defaultPlayerRank, defaultOPRank;
	public final Map<Node, CommandOverride> commands;
	private File ranksFile;
	private final Map<RankCacheKey, Event.Result> permissionCache;
	private final Map<RankCacheKey, ConfigValue> configCache;

	public Ranks(Universe u)
	{
		universe = u;
		ranks = new LinkedHashMap<>();
		rankNames = null;
		permissionNodes = null;
		playerRanks = new LinkedHashMap<>();
		defaultPlayerRank = null;
		defaultOPRank = null;
		commands = new LinkedHashMap<>();
		ranksFile = null;
		permissionCache = new HashMap<>();
		configCache = new HashMap<>();
	}

	public boolean reload()
	{
		ranks.clear();
		playerRanks.clear();
		clearCache();

		if (!isActive())
		{
			return true;
		}

		boolean save = false;

		ranksFile = new File(universe.server.getDataDirectory(), "local/ftbutilities/ranks.txt");

		if (!ranksFile.exists())
		{
			Rank pRank = new Rank(this, "player");
			pRank.add();
			pRank.setPermission(Rank.NODE_DEFAULT_PLAYER, "true");
			pRank.setPermission(Rank.NODE_POWER, "1");
			pRank.setPermission("example.permission", true);
			pRank.setPermission("example.other_permission", false);
			pRank.setPermission("example.permission_with_value", 0);

			Rank vRank = new Rank(this, "vip");
			vRank.add();
			vRank.addParent(pRank);
			vRank.setPermission(Rank.NODE_POWER, "20");
			vRank.setPermission("ftbutilities.chat.name_format", "<&bVIP {name}&r>");
			vRank.setPermission("example.other_permission", true);
			vRank.setPermission("example.permission_with_value", 15);

			Rank aRank = new Rank(this, "admin");
			aRank.add();
			aRank.addParent(vRank);
			aRank.setPermission(Rank.NODE_DEFAULT_OP, "true");
			aRank.setPermission(Rank.NODE_POWER, "100");
			aRank.setPermission("ftbutilities.chat.name_format", "<&2{name}&r>");
			aRank.setPermission("example.permission_with_value", 100);

			PlayerRank fpRank = new PlayerRank(this, UUID.fromString("069be141-3c1b-45c3-b3b1-60d3f9fcd236"), "FakeForgePlayer");
			fpRank.add();
			fpRank.addParent(vRank);
			fpRank.setPermission("example.permission_with_value", 150);
			save = true;
		}

		Rank currentRank = null;
		String lastComment = "";

		for (String line : DataReader.get(ranksFile).safeStringList())
		{
			if (line.isEmpty())
			{
				lastComment = "";
			}
			else if (line.startsWith("//"))
			{
				lastComment = line.substring(2).trim();
			}
			else if (line.startsWith("[") && line.endsWith("]"))
			{
				String linein = line.substring(1, line.length() - 1);

				if (linein.isEmpty())
				{
					currentRank = null;
					continue;
				}

				String[] iss = linein.split(" is ", 2);
				String[] extendss = iss[0].split(" extends ", 2);

				String rankID = StringUtils.removeAllWhitespace(extendss[0]);

				if (rankID.isEmpty())
				{
					currentRank = null;
					continue;
				}

				UUID rankUUID = StringUtils.fromString(rankID);

				if (rankUUID != null && universe.getPlayer(rankUUID) != null)
				{
					currentRank = getPlayerRank(universe.getPlayer(rankUUID).getProfile());
				}
				else
				{
					currentRank = new Rank(this, rankID);
				}

				currentRank.comment = lastComment;
				lastComment = "";

				if (!currentRank.isPlayer())
				{
					if (isValidName(currentRank.getId()))
					{
						currentRank.add();
						currentRank.setPermission(Rank.NODE_POWER, String.valueOf(ranks.size()));
					}
					else
					{
						currentRank = null;
						continue;
					}
				}

				if (extendss.length == 2)
				{
					currentRank.setPermission(Rank.NODE_PARENT, StringUtils.removeAllWhitespace(extendss[1]));
					save = true;
				}

				if (iss.length == 2)
				{
					for (String tag : iss[1].split(","))
					{
						String s = StringUtils.removeAllWhitespace(tag);

						if (!s.isEmpty())
						{
							currentRank.setPermission(s, true);
							save = true;
						}
					}
				}
			}
			else if (currentRank != null)
			{
				String[] s1 = line.split(":", 2);

				if (s1.length == 2)
				{
					String value = s1[1].trim();

					if (!value.isEmpty())
					{
						Rank.Entry entry = currentRank.setPermission(Node.get(s1[0].trim()), value);

						if (entry != null)
						{
							entry.comment = lastComment;
						}
					}
				}

				lastComment = "";
			}
		}

		File playerRanksFile = new File(universe.server.getDataDirectory(), "local/ftbutilities/player_ranks.txt");

		if (playerRanksFile.exists())
		{
			for (String s : DataReader.get(playerRanksFile).safeStringList())
			{
				if (s.isEmpty() || s.startsWith("//"))
				{
					continue;
				}

				String[] s1 = s.split(":", 2);

				if (s1.length == 2)
				{
					ForgePlayer player = universe.getPlayer(s1[0].trim());

					if (player != null)
					{
						Rank rank = getRank(s1[1].trim());

						if (rank != null && !rank.isPlayer())
						{
							getPlayerRank(player.getProfile()).addParent(rank);
						}
					}
				}
			}

			playerRanksFile.delete();
			save = true;
		}

		File badgeFile = new File(universe.server.getDataDirectory(), "local/ftbutilities/server_badges.txt");

		if (badgeFile.exists())
		{
			for (String s : DataReader.get(badgeFile).safeStringList())
			{
				if (s.isEmpty() || s.startsWith("//"))
				{
					continue;
				}

				String[] s1 = s.trim().split(":", 2);

				if (s1.length == 2)
				{
					ForgePlayer player = universe.getPlayer(s1[0].trim());

					if (player != null)
					{
						getPlayerRank(player.getProfile()).setPermission(FTBUtilitiesPermissions.BADGE, s1[1].trim());
					}
				}
			}

			badgeFile.delete();
			save = true;
		}

		if (save)
		{
			save();
		}

		return true;
	}

	public void save()
	{
		universe.clearCache();

		List<String> list = new ArrayList<>();
		list.add("// For more info visit https://github.com/FTBTeam/FTB-Ranks/wiki");

		List<Rank> rankList = new ArrayList<>(ranks.size() + playerRanks.size());
		rankList.addAll(ranks.values());
		rankList.addAll(playerRanks.values());
		rankList.sort(null);
		Collections.reverse(rankList);

		for (Rank rank : rankList)
		{
			if (rank.permissions.isEmpty())
			{
				continue;
			}

			list.add("");

			if (!rank.comment.isEmpty())
			{
				list.add("// " + rank.comment);
			}

			list.add("[" + rank.getId() + "]");

			for (Rank.Entry entry : rank.permissions)
			{
				if (!entry.comment.isEmpty())
				{
					list.add("// " + entry.comment);
				}

				list.add(entry.node + ": " + entry.value);
			}
		}

		FileUtils.saveSafe(ranksFile, list);
	}

	@Nullable
	public Rank getRank(String id)
	{
		if (id.isEmpty() || id.equals("none"))
		{
			return null;
		}

		Rank rank = ranks.get(id);

		if (rank == null)
		{
			ForgePlayer player = universe.getPlayer(id);

			if (player != null)
			{
				return getPlayerRank(player.getProfile());
			}
		}

		return rank;
	}

	public Rank getDefaultPlayerRank()
	{
		if (defaultPlayerRank == null)
		{
			for (Rank rank : ranks.values())
			{
				if (rank.isDefaultPlayerRank())
				{
					defaultPlayerRank = rank;
					break;
				}
			}

			int power = Integer.MAX_VALUE;

			for (Rank rank : ranks.values())
			{
				if (rank.getPower() <= power)
				{
					power = rank.getPower();
					defaultPlayerRank = rank;
				}
			}

			if (defaultPlayerRank == null)
			{
				throw new NullPointerException("Default player rank is null! ");
			}
		}

		return defaultPlayerRank;
	}

	public Rank getDefaultOPRank()
	{
		if (defaultOPRank == null)
		{
			for (Rank rank : ranks.values())
			{
				if (rank.isDefaultOPRank())
				{
					defaultOPRank = rank;
					return defaultOPRank;
				}
			}

			int power = 0;

			for (Rank rank : ranks.values())
			{
				if (rank.getPower() >= power)
				{
					power = rank.getPower();
					defaultOPRank = rank;
				}
			}

			if (defaultOPRank == null)
			{
				defaultOPRank = getDefaultPlayerRank();
			}
		}

		return defaultOPRank;
	}

	public PlayerRank getPlayerRank(GameProfile profile)
	{
		UUID id = profile.getId();

		if (id == null)
		{
			throw new NullPointerException("Null UUID in profile " + profile.getName() + "!");
		}

		PlayerRank rank = playerRanks.get(id);

		if (rank == null)
		{
			rank = new PlayerRank(this, id, profile.getName() == null ? "" : profile.getName());
			rank.add();
		}

		return rank;
	}

	public PlayerRank getPlayerRank(EntityPlayer player)
	{
		return getPlayerRank(player.getGameProfile());
	}

	public Event.Result getPermissionResult(@Nullable MinecraftServer server, GameProfile profile, Node node, @Nullable World world, boolean matching)
	{
		if (!isActive() || profile.getId() == null)
		{
			return Event.Result.DEFAULT;
		}
		else if (world != null && world.isRemote)
		{
			if (FTBUtilitiesConfig.ranks.crash_client_side_permissions)
			{
				throw new RuntimeException("Do not check permissions on client side! Node: " + node);
			}

			return Event.Result.DEFAULT;
		}

		RankCacheKey key = new RankCacheKey(profile.getId(), node);

		Event.Result result = permissionCache.get(key);

		if (result != null)
		{
			return result;
		}

		PlayerRank rank = getPlayerRank(profile);
		result = rank.getPermission(node, matching);

		if (result == Event.Result.DEFAULT && !ranks.isEmpty())
		{
			result = (ServerUtils.isOP(server, profile) ? getDefaultOPRank() : getDefaultPlayerRank()).getPermission(node, matching);
		}

		permissionCache.put(key, result);
		return result;
	}

	public Event.Result getPermissionResult(EntityPlayerMP player, Node node, boolean matching)
	{
		if (!isActive())
		{
			return Event.Result.DEFAULT;
		}

		return getPermissionResult(player.server, player.getGameProfile(), node, player.world, matching);
	}

	public ConfigValue getConfigValue(MinecraftServer server, GameProfile profile, Node node)
	{
		if (profile.getId() == null)
		{
			return ConfigNull.INSTANCE;
		}

		RankCacheKey key = new RankCacheKey(profile.getId(), node);

		ConfigValue value = configCache.get(key);

		if (value != null)
		{
			return value;
		}

		value = ConfigNull.INSTANCE;

		Rank rank = getPlayerRank(profile);
		String string = rank.getConfig(node);

		if (string.isEmpty() && !ranks.isEmpty())
		{
			string = (ServerUtils.isOP(server, profile) ? getDefaultOPRank() : getDefaultPlayerRank()).getConfig(node);
		}

		if (!string.isEmpty())
		{
			RankConfigValueInfo info = RankConfigAPI.getHandler().getInfo(node);

			if (info != null)
			{
				value = info.defaultValue.copy();
				value.setValueFromString(server, string, false);
			}
		}

		configCache.put(key, value);
		return value;
	}

	public Collection<String> getPermissionNodes()
	{
		if (permissionNodes == null)
		{
			permissionNodes = new LinkedHashSet<>();

			for (String s : FTBUtilitiesPermissionHandler.INSTANCE.getRegisteredNodes())
			{
				DefaultPermissionLevel level = DefaultPermissionHandler.INSTANCE.getDefaultPermissionLevel(s);
				String desc = DefaultPermissionHandler.INSTANCE.getNodeDescription(s);
				Node node = Node.get(s);
				boolean printNode = true;

				for (NodeEntry entry : FTBUtilitiesCommon.CUSTOM_PERM_PREFIX_REGISTRY)
				{
					if (entry.getNode().matches(node))
					{
						if (entry.level != null && level == entry.level && desc.isEmpty())
						{
							printNode = false;
						}

						break;
					}
				}

				if (printNode)
				{
					permissionNodes.add(s);
				}
			}

			for (NodeEntry entry : FTBUtilitiesCommon.CUSTOM_PERM_PREFIX_REGISTRY)
			{
				permissionNodes.add(entry.node.toString());
			}

			for (Node node : commands.keySet())
			{
				permissionNodes.add(node.toString());
			}

			for (RankConfigValueInfo info : RankConfigAPI.getHandler().getRegisteredConfigs())
			{
				permissionNodes.add(info.node.toString());
			}

			permissionNodes = Arrays.asList(permissionNodes.toArray(StringUtils.EMPTY_ARRAY));
		}

		return permissionNodes;
	}

	public Collection<String> getRankNames(boolean includeNone)
	{
		if (!includeNone)
		{
			return ranks.keySet();
		}

		if (rankNames == null)
		{
			rankNames = new ArrayList<>(ranks.keySet());
			rankNames.add("none");
			rankNames = Arrays.asList(rankNames.toArray(StringUtils.EMPTY_ARRAY));
		}

		return rankNames;
	}

	public void clearCache()
	{
		rankNames = null;
		permissionNodes = null;
		defaultPlayerRank = null;
		defaultOPRank = null;
		permissionCache.clear();
		configCache.clear();

		for (Rank rank : ranks.values())
		{
			rank.clearCache();
		}
	}
}