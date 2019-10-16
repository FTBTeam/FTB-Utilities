package com.feed_the_beast.ftbutilities.ranks;

import com.feed_the_beast.ftblib.lib.config.RankConfigAPI;
import com.feed_the_beast.ftblib.lib.config.RankConfigValueInfo;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftblib.lib.io.DataReader;
import com.feed_the_beast.ftblib.lib.util.FileUtils;
import com.feed_the_beast.ftblib.lib.util.JsonUtils;
import com.feed_the_beast.ftblib.lib.util.ServerUtils;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftblib.lib.util.misc.Node;
import com.feed_the_beast.ftbutilities.FTBUtilitiesCommon;
import com.feed_the_beast.ftbutilities.FTBUtilitiesConfig;
import com.feed_the_beast.ftbutilities.data.NodeEntry;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.server.permission.DefaultPermissionHandler;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.context.IContext;
import net.minecraftforge.server.permission.context.PlayerContext;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Ranks
{
	public static Ranks INSTANCE;

	public static boolean isActive()
	{
		return FTBUtilitiesConfig.ranks.enabled && INSTANCE != null && PermissionAPI.getPermissionHandler() == FTBUtilitiesPermissionHandler.INSTANCE;
	}

	public static Event.Result getPermissionResult(@Nullable MinecraftServer server, GameProfile profile, Node node, @Nullable IContext context, boolean matching)
	{
		if (!isActive())
		{
			return Event.Result.DEFAULT;
		}
		else if (context != null && context.getWorld() != null && context.getWorld().isRemote)
		{
			if (FTBUtilitiesConfig.ranks.crash_client_side_permissions)
			{
				throw new RuntimeException("Do not check permissions on client side! Node: " + node);
			}

			return Event.Result.DEFAULT;
		}

		Rank rank = INSTANCE.getRank(server, profile, context);

		if (rank.isNone())
		{
			return Event.Result.DEFAULT;
		}

		Event.Result result = rank.cachedPermissions.get(node);

		if (result == null)
		{
			result = rank.getPermissionRaw(node, matching);
			rank.cachedPermissions.put(node, result);
		}

		return result;
	}

	public static Event.Result getPermissionResult(EntityPlayerMP player, Node node, boolean matching)
	{
		if (!isActive())
		{
			return Event.Result.DEFAULT;
		}

		return getPermissionResult(player.server, player.getGameProfile(), node, new PlayerContext(player), matching);
	}

	public static boolean isValidName(@Nullable String id)
	{
		if (id == null || id.isEmpty() || id.equals("none"))
		{
			return false;
		}

		for (int i = 0; i < id.length(); i++)
		{
			char c = id.charAt(i);

			if (c != '_' && (c < '0' || c > '9') && (c < 'a' || c > 'z'))
			{
				return false;
			}
		}

		return true;
	}

	public final Universe universe;
	public final Rank none;
	public final Map<String, Rank> ranks = new LinkedHashMap<>();
	private Collection<String> rankNames = null;
	private Collection<String> permissionNodes = null;
	public final Map<UUID, Rank> playerMap = new HashMap<>();
	private Rank defaultPlayerRank, defaultOPRank;
	public final Map<Node, CommandOverride> commands = new LinkedHashMap<>();

	public Ranks(Universe u)
	{
		universe = u;
		none = new RankNone(this, "none");
		none.parent = none;
	}

	public Rank getRank(String id)
	{
		Rank rank = isValidName(id) ? ranks.get(id) : null;
		return rank == null ? none : rank;
	}

	public Rank getDefaultPlayerRank()
	{
		if (defaultPlayerRank == null)
		{
			defaultPlayerRank = none;

			for (Rank rank : ranks.values())
			{
				if (rank.tags.contains(Rank.TAG_DEFAULT_PLAYER))
				{
					defaultPlayerRank = rank;
				}
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
				if (rank.tags.contains(Rank.TAG_DEFAULT_OP))
				{
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

	@Nullable
	public Rank getSetRank(GameProfile profile)
	{
		return isActive() && profile.getId() != null ? playerMap.get(profile.getId()) : null;
	}

	public Rank getRank(@Nullable MinecraftServer server, GameProfile profile, @Nullable IContext context)
	{
		Rank r = getSetRank(profile);
		return r == null ? (ServerUtils.isOP(server, profile) ? getDefaultOPRank() : getDefaultPlayerRank()) : r;
	}

	public void addRank(Rank rank)
	{
		if (!rank.isNone() && ranks.put(rank.getId(), rank) != rank)
		{
			universe.clearCache();
			saveRanks();
		}
	}

	public boolean removeRank(Rank rank)
	{
		if (!rank.isNone() && ranks.remove(rank.getId()) != null)
		{
			if (playerMap.values().removeIf(r -> r == rank))
			{
				savePlayerRanks();
			}

			for (Rank rank1 : ranks.values())
			{
				if (rank1.parent == rank)
				{
					rank1.parent = none;
				}
			}

			universe.clearCache();
			saveRanks();
			return true;
		}

		return false;
	}

	public boolean setRank(UUID id, Rank rank)
	{
		boolean result;

		if (rank.isNone())
		{
			result = playerMap.remove(id) != null;
		}
		else
		{
			result = playerMap.put(id, rank) != rank;
		}

		if (result)
		{
			universe.clearCache();
			savePlayerRanks();
			return true;
		}

		return false;
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

	public boolean reload()
	{
		ranks.clear();
		playerMap.clear();
		clearCache();

		if (!isActive())
		{
			return true;
		}

		boolean result = true;
		boolean loadedOldFile = false;

		File ranksFile = new File(universe.server.getDataDirectory(), "local/ftbutilities/ranks.json");
		JsonElement ranksJson = DataReader.get(ranksFile).safeJson();
		Map<String, String> rankParents = new HashMap<>();

		if (ranksJson.isJsonObject())
		{
			JsonObject json = ranksJson.getAsJsonObject();

			if (json.has("ranks"))
			{
				for (Map.Entry<String, JsonElement> entry : json.get("ranks").getAsJsonObject().entrySet())
				{
					if (!isValidName(entry.getKey()))
					{
						ranks.put(entry.getKey(), new Rank(this, entry.getKey()));
					}
				}

				for (Map.Entry<String, JsonElement> rankEntry : json.get("ranks").getAsJsonObject().entrySet())
				{
					if (!isValidName(rankEntry.getKey()))
					{
						continue;
					}

					Rank rank = new Rank(this, rankEntry.getKey());
					ranks.put(rank.getId(), rank);

					JsonElement json0 = rankEntry.getValue();

					if (json0.isJsonObject())
					{
						JsonObject o = json0.getAsJsonObject();
						if (o.has("parent"))
						{
							rankParents.put(rank.getId(), o.get("parent").getAsString());
						}

						if (o.has("permissions"))
						{
							JsonElement e1 = o.get("permissions");

							if (e1.isJsonArray())
							{
								JsonArray a = e1.getAsJsonArray();

								for (int i = 0; i < a.size(); i++)
								{
									String id = a.get(i).getAsString();
									char firstChar = id.charAt(0);
									String key = (firstChar == '-' || firstChar == '+' || firstChar == '~') ? id.substring(1) : id;
									key = key.replace("command.ftb.reload", "command.ftblib.reload");
									key = key.replace("command.ftb.team", "command.ftblib.team");
									key = key.replace("command.ftb.my_settings", "command.ftblib.my_settings");
									key = key.replace("command.ftb", "command.ftbutilities");
									rank.setPermission(Node.get(key), firstChar == '-' ? JsonUtils.JSON_FALSE : JsonUtils.JSON_TRUE);
								}
							}
							else
							{
								JsonObject o1 = e1.getAsJsonObject();

								for (Map.Entry<String, JsonElement> entry : o1.entrySet())
								{
									String key = entry.getKey();
									key = key.replace("command.ftb.reload", "command.ftblib.reload");
									key = key.replace("command.ftb.team", "command.ftblib.team");
									key = key.replace("command.ftb.my_settings", "command.ftblib.my_settings");
									key = key.replace("command.ftb", "command.ftbutilities");
									rank.setPermission(Node.get(key), entry.getValue());
								}
							}
						}

						if (o.has("config"))
						{
							for (Map.Entry<String, JsonElement> entry : o.get("config").getAsJsonObject().entrySet())
							{
								rank.setPermission(Node.get(entry.getKey()), entry.getValue());
							}
						}
					}
				}

				if (json.has("default_ranks"))
				{
					JsonObject dr = json.get("default_ranks").getAsJsonObject();

					if (dr.has("player"))
					{
						Rank rank = getRank(dr.get("player").getAsString());

						if (!rank.isNone())
						{
							rank.tags.add(Rank.TAG_DEFAULT_PLAYER);
						}
					}

					if (dr.has("op"))
					{
						Rank rank = getRank(dr.get("op").getAsString());

						if (!rank.isNone())
						{
							rank.tags.add(Rank.TAG_DEFAULT_OP);
						}
					}
				}
			}

			loadedOldFile = true;
			FileUtils.deleteSafe(ranksFile);
		}

		ranksFile = FTBUtilitiesConfig.ranks.load_from_config_folder ? new File(Loader.instance().getConfigDir(), "ftbutilities_ranks.txt") : new File(universe.server.getDataDirectory(), "local/ftbutilities/ranks.txt");

		if (!loadedOldFile && !ranksFile.exists())
		{
			Rank pRank = new Rank(this, "player");
			ranks.put(pRank.getId(), pRank);
			pRank.tags.add(Rank.TAG_DEFAULT_PLAYER);
			pRank.setPermission(Node.get("example.permission"), JsonUtils.JSON_TRUE);
			pRank.setPermission(Node.get("example.other_permission"), JsonUtils.JSON_FALSE);

			Rank oRank = new Rank(this, "admin");
			ranks.put(oRank.getId(), oRank);
			oRank.tags.add(Rank.TAG_DEFAULT_OP);
			oRank.parent = pRank;
			oRank.setPermission(Node.get("example.other_permission"), JsonUtils.JSON_TRUE);
			saveRanks();
		}

		Rank currentRank = null;

		for (String line : DataReader.get(ranksFile).safeStringList())
		{
			if (line.isEmpty() || line.startsWith("//"))
			{
				continue;
			}

			if (line.startsWith("[") && line.endsWith("]"))
			{
				String[] iss = line.substring(1, line.length() - 1).split(" is ", 2);
				String[] extendss = iss[0].split(" extends ", 2);

				currentRank = new Rank(this, StringUtils.removeAllWhitespace(extendss[0]));

				if (isValidName(currentRank.getId()))
				{
					ranks.put(currentRank.getId(), currentRank);
				}

				String parent = "";

				if (iss.length == 2)
				{
					for (String tag : iss[1].split(","))
					{
						String s = StringUtils.removeAllWhitespace(tag);

						if (!s.isEmpty())
						{
							currentRank.tags.add(s);
						}
					}
				}

				if (extendss.length == 2)
				{
					parent = StringUtils.removeAllWhitespace(extendss[1]);
				}

				rankParents.put(currentRank.getId(), parent);
			}
			else if (currentRank != null)
			{
				String[] s1 = line.split(":", 2);

				if (s1.length == 2)
				{
					String[] s2 = s1[1].split(" //");
					JsonElement json = DataReader.get(s2[0].trim()).safeJson();

					if (!JsonUtils.isNull(json))
					{
						currentRank.setPermission(Node.get(s1[0].trim()), json);
					}
				}
			}
			else
			{
				result = false;
			}
		}

		for (Rank rank : ranks.values())
		{
			Rank rankp = getRank(rankParents.get(rank.getId()));

			if (rankp != rank)
			{
				rank.parent = rankp;
			}
		}

		File playerRanksFile = new File(universe.server.getDataDirectory(), "local/ftbutilities/player_ranks.json");
		ranksJson = DataReader.get(playerRanksFile).safeJson();

		if (ranksJson.isJsonObject())
		{
			for (Map.Entry<String, JsonElement> entry : ranksJson.getAsJsonObject().entrySet())
			{
				ForgePlayer player = universe.getPlayer(entry.getKey());

				if (player != null)
				{
					Rank rank = getRank(entry.getValue().getAsString());

					if (!rank.isNone())
					{
						playerMap.put(player.getId(), rank);
					}
				}
			}

			FileUtils.deleteSafe(playerRanksFile);
		}

		playerRanksFile = new File(universe.server.getDataDirectory(), "local/ftbutilities/player_ranks.txt");

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

					if (!rank.isNone())
					{
						playerMap.put(player.getId(), rank);
					}
				}
			}
		}

		//savePlayerRanks();
		return result;
	}

	public void saveRanks()
	{
		List<String> list = new ArrayList<>();
		list.add("// For more info visit https://latvian.dev/mods/ftbutilities/wiki/ranks/");

		StringBuilder line = new StringBuilder();

		for (Rank rank : ranks.values())
		{
			list.add("");

			line.setLength(0);
			line.append('[');
			line.append(rank);

			if (!rank.parent.isNone())
			{
				line.append(" extends ");
				line.append(rank.parent);
			}

			for (String tag : rank.tags)
			{
				line.append(" is ");
				line.append(tag);
			}

			line.append(']');
			list.add(line.toString());

			for (Rank.Entry entry : rank.permissions)
			{
				list.add(entry.node + ": " + entry.json);
			}
		}

		FileUtils.saveSafe(new File(universe.server.getDataDirectory(), "local/ftbutilities/ranks.txt"), list);
	}

	public void clearCache()
	{
		rankNames = null;
		permissionNodes = null;
		defaultPlayerRank = null;
		defaultOPRank = null;

		for (Rank rank : ranks.values())
		{
			rank.cachedPermissions.clear();
			rank.cachedConfig.clear();
		}
	}

	public void savePlayerRanks()
	{
		List<String> list = new ArrayList<>();
		list.add("// For more info visit https://latvian.dev/mods/ftbutilities/wiki/ranks/");
		list.add("");

		for (Map.Entry<UUID, Rank> entry : playerMap.entrySet())
		{
			ForgePlayer player = universe.getPlayer(entry.getKey());

			if (player != null)
			{
				list.add(player + ": " + entry.getValue());
			}
			else
			{
				list.add(StringUtils.fromUUID(entry.getKey()) + ": " + entry.getValue());
			}
		}

		FileUtils.saveSafe(new File(universe.server.getDataDirectory(), "local/ftbutilities/player_ranks.txt"), list);
	}
}