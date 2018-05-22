package com.feed_the_beast.ftbutilities.ranks;

import com.feed_the_beast.ftblib.lib.config.ConfigBoolean;
import com.feed_the_beast.ftblib.lib.config.ConfigValue;
import com.feed_the_beast.ftblib.lib.config.RankConfigAPI;
import com.feed_the_beast.ftblib.lib.config.RankConfigValueInfo;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftblib.lib.io.DataReader;
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
import net.minecraftforge.server.command.CommandTreeBase;
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

	@Nullable
	public Rank getRank(String id)
	{
		return ranks.get(id);
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

			if (Universe.loaded())
			{
				ServerCommandManager manager = (ServerCommandManager) Universe.get().server.getCommandManager();

				for (ICommand command : manager.getCommands().values())
				{
					addCommandNode(permissionNodes, Node.COMMAND, command);
				}
			}

			for (RankConfigValueInfo info : RankConfigAPI.getHandler().getRegisteredConfigs())
			{
				permissionNodes.add(info.node.toString());
			}

			permissionNodes = Arrays.asList(permissionNodes.toArray(StringUtils.EMPTY_ARRAY));
		}

		return permissionNodes;
	}

	private void addCommandNode(Collection<String> permissionNodes, Node parent, ICommand command)
	{
		Node node = parent.append(command.getName());
		permissionNodes.add(node.toString());

		if (command instanceof CommandTreeBase)
		{
			for (ICommand command1 : ((CommandTreeBase) command).getSubCommands())
			{
				addCommandNode(permissionNodes, node, command1);
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
					ranks.put(entry.getKey(), new Rank(this, entry.getKey()));
				}

				for (Map.Entry<String, JsonElement> rankEntry : json.get("ranks").getAsJsonObject().entrySet())
				{
					Rank rank = ranks.get(rankEntry.getKey());
					rank.setDefaults();

					JsonElement json0 = rankEntry.getValue();

					if (json0.isJsonObject())
					{
						JsonObject o = json0.getAsJsonObject();
						if (o.has("parent"))
						{
							Rank p = ranks.get(o.get("parent").getAsString());

							if (p != null)
							{
								rank.parents.add(p);
							}
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
									Rank.Entry entry = new Rank.Entry(Node.get(key));
									entry.json = firstChar == '-' ? JsonUtils.JSON_FALSE : JsonUtils.JSON_TRUE;
									rank.permissions.put(entry.node, entry);
								}
							}
							else
							{
								JsonObject o1 = e1.getAsJsonObject();

								for (Map.Entry<String, JsonElement> entry : o1.entrySet())
								{
									Rank.Entry entry1 = new Rank.Entry(Node.get(entry.getKey()));
									entry1.json = entry.getValue();
									rank.permissions.put(entry1.node, entry1);
								}
							}
						}

						if (o.has("config"))
						{
							for (Map.Entry<String, JsonElement> entry : o.get("config").getAsJsonObject().entrySet())
							{
								Rank.Entry entry1 = new Rank.Entry(Node.get(entry.getKey()));
								entry1.json = entry.getValue();
								rank.permissions.put(entry1.node, entry1);
							}
						}
					}
				}

				if (json.has("default_ranks"))
				{
					JsonObject dr = json.get("default_ranks").getAsJsonObject();
					defaultPlayerRank = dr.has("player") ? ranks.get(dr.get("player").getAsString()) : null;
					defaultOPRank = dr.has("op") ? ranks.get(dr.get("op").getAsString()) : null;
				}
			}

			loadedOldFile = true;
			FileUtils.delete(ranksFile);
		}

		ranksFile = new File(CommonUtils.folderLocal, "ftbutilities/ranks.txt");

		if (!loadedOldFile && !ranksFile.exists())
		{
			Rank pRank = new Rank(this, "player");
			ranks.put(pRank.getName(), pRank);
			defaultPlayerRank = pRank;

			Rank oRank = new Rank(this, "admin");
			ranks.put(oRank.getName(), oRank);
			defaultOPRank = oRank;
			oRank.parents.add(pRank);
			Rank.Entry nameColor = new Rank.Entry(FTBUtilitiesPermissions.CHAT_NAME.color);
			nameColor.json = new JsonPrimitive("dark_green");
			oRank.permissions.put(nameColor.node, nameColor);
		}

		Rank currentRank = null;
		Map<String, LinkedHashSet<String>> rankParents = new LinkedHashMap<>();

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
				ranks.put(currentRank.getName(), currentRank);
				LinkedHashSet<String> parents = new LinkedHashSet<>();

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
					for (String tag : extendss[1].split(","))
					{
						String s = StringUtils.removeAllWhitespace(tag);

						if (!s.isEmpty())
						{
							parents.add(s);
						}
					}
				}

				rankParents.put(currentRank.getName(), parents);
			}
			else if (currentRank != null)
			{
				String[] s1 = line.split(":", 2);

				if (s1.length == 2)
				{
					Node node = Node.get(StringUtils.removeAllWhitespace(s1[0]));
					JsonElement json = DataReader.get(StringUtils.trimAllWhitespace(s1[1])).safeJson();

					if (!JsonUtils.isNull(json))
					{
						Rank.Entry entry = new Rank.Entry(node);
						entry.json = json;
						currentRank.permissions.put(entry.node, entry);
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
			for (String s : rankParents.get(rank.getName()))
			{
				Rank r = ranks.get(s);

				if (r != null)
				{
					rank.parents.add(r);
				}
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
					Rank rank = ranks.get(entry.getValue().getAsString());

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
					Rank rank = ranks.get(StringUtils.trimAllWhitespace(s1[1]));

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
		list.add("// Add [name extends parent_name] to make this rank include all permissions from parent.");
		list.add("// Add [name is default_player_rank] or [name is default_op_rank] to make this rank default for players/ops that don't have a rank set explicitly.");
		list.add("// ");
		list.add("// For more info visit https://guides.latmod.com/ftbutilities/ranks/");

		StringBuilder line = new StringBuilder();

		for (Rank rank : ranks.values())
		{
			list.add("");

			line.setLength(0);
			line.append('[');
			line.append(rank);

			if (!rank.parents.isEmpty())
			{
				line.append(" extends ");
				line.append(StringJoiner.with(", ").join(rank.parents));
			}

			if (!rank.tags.isEmpty())
			{
				line.append(" is ");
				line.append(StringJoiner.with(", ").join(rank.tags));
			}

			line.append(']');
			list.add(line.toString());

			for (Rank.Entry entry : rank.permissions.values())
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
		Universe.get().clearCache();

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
			String desc = new TextComponentTranslation(info.node.toString()).getUnformattedText();
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
		list.add("</style></head><body><h1>Permissions</h1><h3>Modifying this file won't have any effect!</h3><table>");
		list.add("<tr><th>Node</th><th>Player</th><th>OP</th><th>Variants</th><th>Info</th></tr>");

		for (NodeEntry entry : allNodes)
		{
			list.add("<tr><td>" + entry.getNode() + "</td><td class='" + classOf(entry.player) + "'>" + entry.player.getSerializableElement() + "</td><td class='" + classOf(entry.op) + "'>" + entry.op.getSerializableElement() + "</td><td>");

			if (entry.player instanceof ConfigBoolean)
			{
				list.add("true / false");
			}
			else
			{
				List<String> variants = entry.player.getVariants();

				if (!variants.isEmpty())
				{
					variants = new ArrayList<>(variants);
					variants.sort(StringUtils.IGNORE_CASE_COMPARATOR);

					for (String s : variants)
					{
						list.add("<p>" + TextFormatting.getTextWithoutFormattingCodes(s) + "</p>");
					}
				}
			}

			list.add("</td><td>");

			if (entry.desc.isEmpty())
			{
				for (String s1 : entry.desc.split("\n"))
				{
					list.add("<p>" + s1 + "</p>");
				}
			}

			list.add("</td></tr>");
		}

		list.add("</table></body></html>");
		FileUtils.saveSafe(new File(CommonUtils.folderLocal, "ftbutilities/all_permissions.html"), list);
		FileUtils.delete(new File(CommonUtils.folderLocal, "ftbutilities/all_configs.html"));

		list = new ArrayList<>();

		for (String node : PermissionAPI.getPermissionHandler().getRegisteredNodes())
		{
			list.add(node + ": " + DefaultPermissionHandler.INSTANCE.getDefaultPermissionLevel(node));
		}

		Collections.sort(list);
		list.add(0, PermissionAPI.getPermissionHandler().getRegisteredNodes().size() + " nodes in total");
		list.add(1, "Modifying this file won't have any effect!");
		list.add(2, "");
		FileUtils.saveSafe(new File(CommonUtils.folderLocal, "ftbutilities/all_permissions_full_list.txt"), list);
	}
}