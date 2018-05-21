package com.feed_the_beast.ftbutilities.ranks;

import com.feed_the_beast.ftblib.lib.config.ConfigValueInfo;
import com.feed_the_beast.ftblib.lib.config.RankConfigAPI;
import com.feed_the_beast.ftblib.lib.config.RankConfigValueInfo;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftblib.lib.io.DataReader;
import com.feed_the_beast.ftblib.lib.util.CommonUtils;
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
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.server.permission.DefaultPermissionHandler;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.context.IContext;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Ranks
{
	public static Ranks INSTANCE;

	public final Universe universe;
	private final Map<String, Rank> ranks = new LinkedHashMap<>();
	private final Collection<String> rankNames = new ArrayList<>();
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

	public Collection<String> getRankNames()
	{
		return rankNames;
	}

	public void updateRankNames()
	{
		rankNames.clear();
		rankNames.addAll(ranks.keySet());
		rankNames.add("none");
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

		File ranksFile = new File(CommonUtils.folderLocal, "ftbutilities/ranks.json");
		JsonElement ranksJson = DataReader.get(ranksFile).safeJson();

		if (ranksJson.isJsonObject())
		{
			JsonObject json = ranksJson.getAsJsonObject();

			if (json.has("default_ranks") && json.has("ranks"))
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
							rank.parent = ranks.get(o.get("parent").getAsString());
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

				JsonObject dr = json.get("default_ranks").getAsJsonObject();
				defaultPlayerRank = dr.has("player") ? ranks.get(dr.get("player").getAsString()) : null;
				defaultOPRank = dr.has("op") ? ranks.get(dr.get("op").getAsString()) : null;
			}

			ranksFile.delete();
		}

		ranksFile = new File(CommonUtils.folderLocal, "ftbutilities/ranks.txt");
		Rank currentRank = null;

		for (String s : DataReader.get(ranksFile).safeStringList())
		{
			if (s.startsWith("#"))
			{
				currentRank = new Rank(this, StringUtils.removeAllWhitespace(s.substring(1)));
				ranks.put(currentRank.getName(), currentRank);
			}
			else if (currentRank != null)
			{
				String[] s1 = s.split(":", 2);

				if (s1.length == 2)
				{
					Node node = Node.get(StringUtils.removeAllWhitespace(s1[0]));
					String[] s2 = s1[1].split("//");
					JsonElement json = DataReader.get(StringUtils.trimAllWhitespace(s2[0])).safeJson();

					if (!JsonUtils.isNull(json))
					{
						Rank.Entry entry = new Rank.Entry(node);
						entry.json = json;

						if (s2.length == 2)
						{
							entry.comment = StringUtils.trimAllWhitespace(s2[1]);
						}

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
			Rank.Entry parent = rank.permissions.remove(Node.get("parent"));
			Rank.Entry isDefaultPlayerRank = rank.permissions.remove(Node.get("default_player_rank"));
			Rank.Entry isDefaultOPRank = rank.permissions.remove(Node.get("default_op_rank"));

			if (parent != null)
			{
				rank.parent = ranks.get(parent.json.getAsString());
			}

			if (isDefaultPlayerRank != null && isDefaultPlayerRank.json.isJsonPrimitive() && isDefaultPlayerRank.json.getAsJsonPrimitive().isBoolean() && isDefaultPlayerRank.json.getAsBoolean())
			{
				defaultPlayerRank = rank;
			}

			if (isDefaultOPRank != null && isDefaultOPRank.json.isJsonPrimitive() && isDefaultOPRank.json.getAsJsonPrimitive().isBoolean() && isDefaultOPRank.json.getAsBoolean())
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

			playerRanksFile.delete();
		}

		playerRanksFile = new File(CommonUtils.folderLocal, "ftbutilities/player_ranks.txt");

		for (String s : DataReader.get(playerRanksFile).safeStringList())
		{
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
		boolean first = true;

		for (Rank rank : ranks.values())
		{
			if (first)
			{
				first = false;
			}
			else
			{
				list.add("");
			}

			if (rank.comment.isEmpty())
			{
				list.add("# " + rank);
			}
			else
			{
				list.add("# " + rank + " // " + rank.comment);
			}

			if (rank.parent != null)
			{
				list.add("parent: \"" + rank.parent + "\"");
			}

			if (rank == defaultPlayerRank)
			{
				list.add("default_player_rank: true");
			}

			if (rank == defaultOPRank && rank != defaultPlayerRank)
			{
				list.add("default_op_rank: true");
			}

			for (Rank.Entry entry : rank.permissions.values())
			{
				if (entry.comment.isEmpty())
				{
					list.add(entry.node + ": " + entry.json);
				}
				else
				{
					list.add(entry.node + ": " + entry.json + " // " + entry.comment);
				}
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
					if (level == cprefix.getLevel() && desc.isEmpty())
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

		allNodes.sort(StringUtils.ID_COMPARATOR);
		List<String> list = new ArrayList<>();

		list.add("<html><head><title>Permissions</title><style>");
		list.add("table{font-family:arial, sans-serif;border-collapse:collapse;}");
		list.add("td,th{border:1px solid #666666;text-align:left;padding:8px;}");
		list.add("td.all{background-color:#72FF85;}");
		list.add("td.op{background-color:#42A3FF;}");
		list.add("td.none{background-color:#FF4242;}");
		list.add("</style></head><body><h1>Permissions</h1><h3>Modifying this file won't have any effect!</h3><table>");
		list.add("<tr><th>Permission Node</th><th></th><th>Info</th></tr>");

		for (NodeEntry entry : allNodes)
		{
			list.add("<tr><td>" + entry.getNode() + "</td><td class='" + entry.getLevel().name().toLowerCase() + "'>" + entry.getLevel() + "</td><td>");

			if (entry.getDescription() != null)
			{
				for (String s1 : entry.getDescription().split("\n"))
				{
					list.add("<p>" + s1 + "</p>");
				}
			}

			list.add("</td></tr>");
		}

		list.add("</table></body></html>");
		FileUtils.saveSafe(new File(CommonUtils.folderLocal, "ftbutilities/all_permissions.html"), list);

		list = new ArrayList<>();
		list.add("<html><head><title>Rank Configs</title>");
		list.add("<style>table{font-family: arial, sans-serif;border-collapse: collapse;}td,th{border:1px solid #666666;text-align: left;padding:8px;}p,ul{margin:4px;}</style>");
		list.add("</head><body><h1>Rank Configs</h1><h3>Modifying this file won't have any effect!</h3><table>");
		list.add("<tr><th>Rank Config</th><th>Def Value</th><th>Info</th></tr>");

		List<String> infoList = new ArrayList<>();

		for (RankConfigValueInfo info : RankConfigAPI.getHandler().getRegisteredConfigs())
		{
			ConfigValueInfo p = new ConfigValueInfo(info.node, info.defaultValue);
			list.add("<tr><td>" + info.node + "</td><td>");

			p.defaultValue.addInfo(p, infoList);
			List<String> variants = p.defaultValue.getVariants();

			if (!infoList.isEmpty() || !variants.isEmpty())
			{
				list.add("<ul><li>Default: " + p.defaultValue.getSerializableElement() + "</li>");
				list.add("<li>OP Default: " + info.defaultOPValue.getSerializableElement() + "</li>");

				for (String s : infoList)
				{
					if (!s.contains("Def:"))
					{
						list.add("<li>" + TextFormatting.getTextWithoutFormattingCodes(s) + "</li>");
					}
				}

				infoList.clear();

				if (!variants.isEmpty())
				{
					list.add("<li>Variants:<ul>");
					variants = new ArrayList<>(variants);
					variants.sort(StringUtils.IGNORE_CASE_COMPARATOR);

					for (String s : variants)
					{
						list.add("<li>" + s + "</li>");
					}

					list.add("</ul></li>");
				}

				list.add("</ul>");
			}
			else
			{
				list.add("Default: " + p.defaultValue.getSerializableElement());
			}

			list.add("</td><td>");

			String infoS = (p.displayName == null ? new TextComponentTranslation("rank_config." + info.node) : p.displayName).getUnformattedText();

			if (!infoS.isEmpty())
			{
				for (String s1 : infoS.split("\\\\n"))
				{
					list.add("<p>" + s1 + "</p>");
				}
			}

			list.add("</td></tr>");
		}

		list.add("</table></body></html>");
		FileUtils.saveSafe(new File(CommonUtils.folderLocal, "ftbutilities/all_configs.html"), list);

		list = new ArrayList<>();

		for (String node : PermissionAPI.getPermissionHandler().getRegisteredNodes())
		{
			list.add(node + ": " + DefaultPermissionHandler.INSTANCE.getDefaultPermissionLevel(node));
		}

		Collections.sort(list);
		list.add(0, "");
		list.add(0, "Modifying this file won't have any effect!");
		list.add(0, PermissionAPI.getPermissionHandler().getRegisteredNodes().size() + " nodes in total");
		FileUtils.saveSafe(new File(CommonUtils.folderLocal, "ftbutilities/all_permissions_full_list.txt"), list);
	}
}