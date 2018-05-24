package com.feed_the_beast.ftbutilities.ranks;

import com.feed_the_beast.ftblib.lib.config.ConfigBoolean;
import com.feed_the_beast.ftblib.lib.config.ConfigDouble;
import com.feed_the_beast.ftblib.lib.config.ConfigInt;
import com.feed_the_beast.ftblib.lib.config.ConfigTimer;
import com.feed_the_beast.ftblib.lib.config.ConfigValue;
import com.feed_the_beast.ftblib.lib.config.RankConfigAPI;
import com.feed_the_beast.ftblib.lib.config.RankConfigValueInfo;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftblib.lib.io.DataReader;
import com.feed_the_beast.ftblib.lib.math.Ticks;
import com.feed_the_beast.ftblib.lib.util.CommonUtils;
import com.feed_the_beast.ftblib.lib.util.FileUtils;
import com.feed_the_beast.ftblib.lib.util.JsonUtils;
import com.feed_the_beast.ftblib.lib.util.ServerUtils;
import com.feed_the_beast.ftblib.lib.util.StringJoiner;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftblib.lib.util.misc.Node;
import com.feed_the_beast.ftbutilities.FTBUtilitiesCommon;
import com.feed_the_beast.ftbutilities.FTBUtilitiesConfig;
import com.feed_the_beast.ftbutilities.FTBUtilitiesPermissions;
import com.feed_the_beast.ftbutilities.data.NodeEntry;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.authlib.GameProfile;
import net.minecraft.command.ICommand;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.server.permission.DefaultPermissionHandler;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.context.IContext;

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

public class Ranks
{
	public static Ranks INSTANCE;

	public final Universe universe;
	private final Map<String, Rank> ranks = new LinkedHashMap<>();
	private Collection<String> rankNames = null;
	private Collection<String> permissionNodes = null;
	private final Map<UUID, Rank> playerMap = new HashMap<>();
	private Rank defaultPlayerRank, defaultOPRank;

	public Ranks(Universe u)
	{
		universe = u;
	}

	public boolean isValidName(String id)
	{
		if (id.isEmpty() || id.charAt(0) == '–' || id.equals("none") || id.equals("null"))
		{
			return false;
		}

		for (int i = 0; i < id.length(); i++)
		{
			if (!Character.isLowerCase(id.charAt(i)))
			{
				return false;
			}
		}

		return true;
	}

	@Nullable
	public Rank getRank(String id)
	{
		return isValidName(id) ? ranks.get(id) : null;
	}

	@Nullable
	public Rank getRank(@Nullable MinecraftServer server, GameProfile profile, @Nullable IContext context)
	{
		Rank r = FTBUtilitiesConfig.ranks.enabled ? playerMap.get(profile.getId()) : null;
		return (r == null) ? (ServerUtils.isOP(server, profile) ? defaultOPRank : defaultPlayerRank) : r;
	}

	public void addRank(Rank rank)
	{
		ranks.put(rank.getName(), rank);
		updateRankNames();
		saveRanks();
	}

	public void setRank(UUID id, @Nullable Rank r)
	{
		if (r == null)
		{
			playerMap.remove(id);
		}
		else
		{
			playerMap.put(id, r);
		}

		savePlayerRanks();
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

			ServerCommandManager manager = (ServerCommandManager) universe.server.getCommandManager();

			for (ICommand command : manager.getCommands().values())
			{
				addCommandNode(permissionNodes, command, null);
			}

			for (RankConfigValueInfo info : RankConfigAPI.getHandler().getRegisteredConfigs())
			{
				permissionNodes.add(info.node.toString());
			}

			permissionNodes = Arrays.asList(permissionNodes.toArray(StringUtils.EMPTY_ARRAY));
		}

		return permissionNodes;
	}

	private void addCommandNode(Collection<String> permissionNodes, ICommand command, @Nullable Map<String, String> commandUsage)
	{
		if (command instanceof CommandTreeOverride)
		{
			for (ICommand command1 : ((CommandTreeOverride) command).getSubCommands())
			{
				addCommandNode(permissionNodes, command1, commandUsage);
			}
		}
		else if (command instanceof CommandOverride)
		{
			String node = ((CommandOverride) command).node.toString();
			permissionNodes.add(node);

			if (commandUsage != null)
			{
				commandUsage.put(node, new TextComponentTranslation(command.getUsage(universe.server)).getUnformattedText());
			}
		}
	}

	public void updatePermissionNodes()
	{
		permissionNodes = null;
	}

	public Collection<String> getRankNames()
	{
		if (rankNames == null)
		{
			rankNames = new ArrayList<>(ranks.keySet());
			rankNames.add("none");
			rankNames = Arrays.asList(rankNames.toArray(StringUtils.EMPTY_ARRAY));
		}

		return rankNames;
	}

	public void updateRankNames()
	{
		rankNames = null;
		updatePermissionNodes();
	}

	public void removeNodeFromCaches(Node node)
	{
		for (Rank rank : ranks.values())
		{
			rank.cachedPermissions.remove(node);
			rank.cachedConfig.remove(node);
		}
	}

	public boolean reload()
	{
		ranks.clear();
		playerMap.clear();
		defaultPlayerRank = null;
		defaultOPRank = null;

		if (!FTBUtilitiesConfig.ranks.enabled)
		{
			return true;
		}

		boolean result = true;
		boolean loadedOldFile = false;

		File ranksFile = new File(CommonUtils.folderLocal, "ftbutilities/ranks.json");
		JsonElement ranksJson = DataReader.get(ranksFile).safeJson();

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
					Rank rank = getRank(rankEntry.getKey());
					rank.setDefaults();

					JsonElement json0 = rankEntry.getValue();

					if (json0.isJsonObject())
					{
						JsonObject o = json0.getAsJsonObject();
						if (o.has("parent"))
						{
							rank.parent = getRank(o.get("parent").getAsString());
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
						Rank r = getRank(dr.get("player").getAsString());

						if (r != null)
						{
							r.tags.add("default_player_rank");
						}
					}

					if (dr.has("op"))
					{
						Rank r = getRank(dr.get("op").getAsString());

						if (r != null)
						{
							r.tags.add("default_op_rank");
						}
					}
				}
			}

			loadedOldFile = true;
			FileUtils.delete(ranksFile);
		}

		ranksFile = FTBUtilitiesConfig.ranks.load_from_config_folder ? new File(CommonUtils.folderConfig, "ftbutilities_ranks.txt") : new File(CommonUtils.folderLocal, "ftbutilities/ranks.txt");

		if (!loadedOldFile && !ranksFile.exists())
		{
			Rank pRank = new Rank(this, "player");
			ranks.put(pRank.getName(), pRank);
			pRank.tags.add("default_player_rank");
			pRank.setPermission(Node.get("example.permission"), JsonUtils.JSON_TRUE);
			pRank.setPermission(Node.get("example.other_permission"), JsonUtils.JSON_FALSE);

			Rank oRank = new Rank(this, "admin");
			ranks.put(oRank.getName(), oRank);
			oRank.tags.add("default_op_rank");
			oRank.parent = pRank;
			oRank.setPermission(FTBUtilitiesPermissions.CHAT_NAME.color, new JsonPrimitive("dark_green"));
		}

		Rank currentRank = null;
		Map<String, String> rankParents = new HashMap<>();

		for (String line : DataReader.get(ranksFile).safeStringList())
		{
			if (line.isEmpty() || line.startsWith("//"))
			{
				continue;
			}

			if (line.startsWith("[") && line.endsWith("]"))
			{
				String iss[] = line.substring(1, line.length() - 1).split(" is ", 2);
				String extendss[] = iss[0].split(" extends ", 2);

				currentRank = new Rank(this, StringUtils.removeAllWhitespace(extendss[0]));

				if (isValidName(currentRank.getName()))
				{
					ranks.put(currentRank.getName(), currentRank);
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

				rankParents.put(currentRank.getName(), parent);
			}
			else if (currentRank != null)
			{
				String[] s1 = line.split(": ", 2);

				if (s1.length == 2)
				{
					String[] s2 = s1[1].split(" // ");
					JsonElement json = DataReader.get(StringUtils.trimAllWhitespace(s2[0])).safeJson();

					if (!JsonUtils.isNull(json))
					{
						currentRank.setPermission(Node.get(StringUtils.removeAllWhitespace(s1[0])), json);
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
			Rank r = getRank(rankParents.get(rank.getName()));

			if (r != rank)
			{
				rank.parent = r;
			}

			if (rank.tags.contains("default_player_rank"))
			{
				defaultPlayerRank = rank;
			}

			if (rank.tags.contains("default_op_rank"))
			{
				defaultOPRank = rank;
			}
		}

		if (defaultOPRank == null)
		{
			defaultOPRank = defaultPlayerRank;
		}

		saveRanks();
		updateRankNames();

		File playerRanksFile = new File(CommonUtils.folderLocal, "ftbutilities/player_ranks.json");
		ranksJson = DataReader.get(playerRanksFile).safeJson();

		if (ranksJson.isJsonObject())
		{
			for (Map.Entry<String, JsonElement> entry : ranksJson.getAsJsonObject().entrySet())
			{
				ForgePlayer player = universe.getPlayer(entry.getKey());

				if (player != null)
				{
					Rank rank = getRank(entry.getValue().getAsString());

					if (rank != null)
					{
						playerMap.put(player.getId(), rank);
					}
				}
			}

			FileUtils.delete(playerRanksFile);
		}

		playerRanksFile = new File(CommonUtils.folderLocal, "ftbutilities/player_ranks.txt");

		for (String s : DataReader.get(playerRanksFile).safeStringList())
		{
			if (s.isEmpty() || s.startsWith("//"))
			{
				continue;
			}

			String[] s1 = s.split(":", 2);

			if (s1.length == 2)
			{
				ForgePlayer player = universe.getPlayer(StringUtils.trimAllWhitespace(s1[0]));

				if (player != null)
				{
					Rank rank = getRank(StringUtils.trimAllWhitespace(s1[1]));

					if (rank != null)
					{
						playerMap.put(player.getId(), rank);
					}
				}
			}
		}

		savePlayerRanks();
		return result;
	}

	public void saveRanks()
	{
		List<String> list = new ArrayList<>();
		list.add("// This file stores rank definitions.");
		list.add("// ");
		list.add("// [name]");
		list.add("// permission: value");
		list.add("// ");
		list.add("// Add [name extends parent_name] to make this rank include all permissions from parent_name rank.");
		list.add("// Add [name is default_player_rank] or [name is default_op_rank] to make this rank default for players/ops that don't have a rank set explicitly.");
		list.add("// ");
		list.add("// For more info visit https://guides.latmod.com/ftbutilities/ranks/");
		list.add("// To see the list of permission nodes, open all_permissions.html in browser or all_permissions_full_list.txt");

		StringBuilder line = new StringBuilder();

		for (Rank rank : ranks.values())
		{
			list.add("");

			line.setLength(0);
			line.append('[');
			line.append(rank);

			if (rank.parent != null)
			{
				line.append(" extends ");
				line.append(rank.parent);
			}

			if (!rank.tags.isEmpty())
			{
				line.append(" is ");
				line.append(StringJoiner.with(", ").join(rank.tags));
			}

			line.append(']');
			list.add(line.toString());

			for (Rank.Entry entry : rank.permissions)
			{
				list.add(entry.node + ": " + entry.json);
			}
		}

		FileUtils.saveSafe(new File(CommonUtils.folderLocal, "ftbutilities/ranks.txt"), list);
	}

	public void saveAndUpdate(MinecraftServer server, Node node)
	{
		removeNodeFromCaches(node);
		saveRanks();
		universe.clearCache();

		for (EntityPlayerMP player : server.getPlayerList().getPlayers())
		{
			server.getPlayerList().updatePermissionLevel(player);
		}
	}

	public void savePlayerRanks()
	{
		List<String> list = new ArrayList<>();
		list.add("// This file stores player ranks.");
		list.add("// ");
		list.add("// Username: Rank");
		list.add("// UUID: Rank");
		list.add("// ");
		list.add("// For more info visit https://guides.latmod.com/ftbutilities/ranks/");
		list.add("");

		for (Map.Entry<UUID, Rank> entry : playerMap.entrySet())
		{
			ForgePlayer player = universe.getPlayer(entry.getKey());

			if (player != null)
			{
				list.add(player + ": " + entry.getValue());
			}
		}

		FileUtils.saveSafe(new File(CommonUtils.folderLocal, "ftbutilities/player_ranks.txt"), list);
	}

	private String classOf(ConfigValue value)
	{
		if (value instanceof ConfigBoolean)
		{
			return value.getBoolean() ? "true" : "false";
		}
		else
		{
			return "other";
		}
	}

	private String fixHTML(JsonElement json)
	{
		return json.toString().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}

	public void generateExampleFiles()
	{
		List<NodeEntry> allNodes = new ArrayList<>(FTBUtilitiesCommon.CUSTOM_PERM_PREFIX_REGISTRY);

		for (String s : PermissionAPI.getPermissionHandler().getRegisteredNodes())
		{
			DefaultPermissionLevel level = DefaultPermissionHandler.INSTANCE.getDefaultPermissionLevel(s);
			String desc = PermissionAPI.getPermissionHandler().getNodeDescription(s);
			Node node = Node.get(s);

			boolean printNode = true;

			for (NodeEntry cprefix : FTBUtilitiesCommon.CUSTOM_PERM_PREFIX_REGISTRY)
			{
				if (cprefix.getNode().matches(node))
				{
					if (cprefix.level != null && level == cprefix.level && desc.isEmpty())
					{
						printNode = false;
					}

					break;
				}
			}

			if (printNode)
			{
				allNodes.add(new NodeEntry(node, level, desc));
			}
		}

		for (RankConfigValueInfo info : RankConfigAPI.getHandler().getRegisteredConfigs())
		{
			String desc = new TextComponentTranslation("rank_config." + info.node).getUnformattedText();
			allNodes.add(new NodeEntry(info.node, info.defaultValue, info.defaultOPValue, desc.equals(info.node.toString()) ? "" : desc, null));
		}

		allNodes.sort(null);
		List<String> list = new ArrayList<>();

		list.add("<html><head><title>Permissions</title><style>");
		list.add("table{font-family:arial, sans-serif;border-collapse:collapse;}");
		list.add("td,th{border:1px solid #666666;text-align:left;padding:8px;}");
		list.add("th{background-color:#CCCCCC;}");
		list.add("p{margin:0;}");
		list.add("tr:nth-child(even){background-color:#D8D8D8;}");
		list.add("tr:nth-child(odd){background-color:#EEEEEE;}");
		list.add("td.true{background-color:#72FF85AA;}");
		list.add("td.false{background-color:#FF6666AA;}");
		list.add("td.other{background-color:#42A3FFAA;}");
		list.add("th,td.true,td.false,td.other{text-align:center;}");
		list.add("</style></head><body><h1>Permissions</h1><h3>Modifying this file won't have any effect, edit ranks.txt!</h3><table>");
		list.add("<tr><th>Node</th><th>Type</th><th>Player</th><th>OP</th><th>Info (Mouse over for variants)</th></tr>");

		for (NodeEntry entry : allNodes)
		{
			list.add("<tr>");
			list.add("<td><code>" + entry.getNode() + "</code></td>");
			list.add("<td><code>" + entry.player.getName() + "</code></td>");
			list.add("<td class='" + classOf(entry.player) + "'><code>" + fixHTML(entry.player.getSerializableElement()) + "</code></td>");
			list.add("<td class='" + classOf(entry.op) + "'><code>" + fixHTML(entry.op.getSerializableElement()) + "</code></td><td title='");

			List<String> variants = new ArrayList<>();

			if (entry.player instanceof ConfigBoolean)
			{
				variants.add("true");
				variants.add("false");
			}
			else if (entry.player instanceof ConfigInt)
			{
				int min = ((ConfigInt) entry.player).getMin();
				int max = ((ConfigInt) entry.player).getMax();
				variants.add(String.format("%s to %s", min == Integer.MIN_VALUE ? "-\u221E" : String.valueOf(min), max == Integer.MAX_VALUE ? "\u221E" : String.valueOf(max)));
			}
			else if (entry.player instanceof ConfigDouble)
			{
				double min = ((ConfigDouble) entry.player).getMin();
				double max = ((ConfigDouble) entry.player).getMax();

				variants.add(String.format("%s to %s", min == Double.NEGATIVE_INFINITY ? "-\u221E" : StringUtils.formatDouble(min), max == Double.POSITIVE_INFINITY ? "\u221E" : StringUtils.formatDouble(max)));
			}
			else if (entry.player instanceof ConfigTimer)
			{
				long max = ((ConfigTimer) entry.player).getMax();
				variants.add(String.format("0s to %s", max == Long.MAX_VALUE ? "\u221E" : Ticks.toString(max)));
			}
			else
			{
				variants = new ArrayList<>(entry.player.getVariants());
				variants.sort(StringUtils.IGNORE_CASE_COMPARATOR);
			}

			for (String s : variants)
			{
				list.add(TextFormatting.getTextWithoutFormattingCodes(s) + " ");
			}

			list.add("'>");

			if (!entry.desc.isEmpty())
			{
				for (String s1 : entry.desc.split("\n"))
				{
					list.add("<p>" + s1 + "</p>");
				}
			}

			list.add("</td></tr>");
		}

		list.add("</table><br><table><tr><th>Available command nodes</th><th>Usage</th></tr>");
		ServerCommandManager manager = (ServerCommandManager) universe.server.getCommandManager();
		LinkedHashSet<String> commandNodes0 = new LinkedHashSet<>();
		Map<String, String> commandUsage = new HashMap<>();

		for (ICommand command : manager.getCommands().values())
		{
			addCommandNode(commandNodes0, command, commandUsage);
		}

		List<String> commandNodes = new ArrayList<>(commandNodes0);
		commandNodes.sort(null);

		for (String s : commandNodes)
		{
			list.add("<tr><td><code>" + s + "</code></td><td>" + commandUsage.get(s) + "</td></tr>");
		}

		list.add("</table>");

		list.add("</body></html>");
		FileUtils.saveSafe(new File(CommonUtils.folderLocal, "ftbutilities/all_permissions.html"), list);
		FileUtils.delete(new File(CommonUtils.folderLocal, "ftbutilities/all_configs.html"));

		list = new ArrayList<>();

		for (String node : PermissionAPI.getPermissionHandler().getRegisteredNodes())
		{
			list.add(node + ": " + DefaultPermissionHandler.INSTANCE.getDefaultPermissionLevel(node));
		}

		Collections.sort(list);
		list.add(0, PermissionAPI.getPermissionHandler().getRegisteredNodes().size() + " nodes in total");
		list.add(1, "Modifying this file won't have any effect, edit ranks.txt!");
		list.add(2, "");
		list.add("");
		list.add("Available command nodes:");
		list.add("");
		list.addAll(commandNodes);
		FileUtils.saveSafe(new File(CommonUtils.folderLocal, "ftbutilities/all_permissions_full_list.txt"), list);
	}
}