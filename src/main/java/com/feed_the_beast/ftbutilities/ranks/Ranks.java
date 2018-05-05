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
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.server.permission.DefaultPermissionHandler;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.context.IContext;
import net.minecraftforge.server.permission.context.PlayerContext;

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
	public final BuiltinRank builtinPlayerRank, builtinOPRank;
	private Rank defaultPlayerRank, defaultOPRank;

	public Ranks(Universe u)
	{
		universe = u;
		builtinPlayerRank = new BuiltinPlayerRank(this);
		builtinOPRank = new BuiltinOPRank(this);
	}

	@Nullable
	public Rank getRank(String id, @Nullable Rank nullrank)
	{
		Rank r = ranks.get(id);
		return r == null ? nullrank : r;
	}

	public Rank getDefaultPlayerRank()
	{
		if (defaultPlayerRank == null)
		{
			Rank r = new Rank(this, "player", builtinPlayerRank);
			ranks.put(r.getName(), r);
			defaultPlayerRank = r;
		}

		return defaultPlayerRank;
	}

	public Rank getDefaultOPRank()
	{
		if (defaultOPRank == null)
		{
			Rank r = new Rank(this, "op", builtinOPRank);
			r.syntax = "<" + TextFormatting.DARK_GREEN + "$name" + TextFormatting.RESET + "> ";
			ranks.put(r.getName(), r);
			defaultOPRank = r;
		}

		return defaultOPRank;
	}

	public Rank getRank(@Nullable MinecraftServer server, GameProfile profile, @Nullable IContext context)
	{
		Rank r = FTBUtilitiesConfig.ranks.enabled ? playerMap.get(profile.getId()) : null;
		return (r == null) ? (ServerUtils.isOP(server, profile) ? getDefaultOPRank() : getDefaultPlayerRank()) : r;
	}

	public Rank getRank(EntityPlayerMP player)
	{
		if (player.connection == null || player instanceof FakePlayer)
		{
			return getDefaultPlayerRank();
		}

		return getRank(player.mcServer, player.getGameProfile(), new PlayerContext(player));
	}

	public Rank getRank(ForgePlayer player)
	{
		return player.isOnline() ? getRank(player.getPlayer()) : getRank(player.team.universe.server, player.getProfile(), null);
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
		rankNames.remove(builtinPlayerRank.getName());
		rankNames.remove(builtinOPRank.getName());
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
		File ranksFile;
		JsonElement ranksJson = JsonNull.INSTANCE;

		if (FTBUtilitiesConfig.ranks.enabled)
		{
			ranksFile = new File(CommonUtils.folderLocal, "ftbutilities/ranks.json");
			ranksJson = DataReader.get(ranksFile).safeJson();

			if (ranksFile.exists() && !ranksJson.isJsonObject())
			{
				return false;
			}
		}

		ranks.clear();
		ranks.put(builtinPlayerRank.getName(), builtinPlayerRank);
		ranks.put(builtinOPRank.getName(), builtinOPRank);
		playerMap.clear();
		defaultPlayerRank = null;
		defaultOPRank = null;
		boolean result = true;

		if (FTBUtilitiesConfig.ranks.enabled)
		{
			if (ranksJson.isJsonObject())
			{
				JsonObject json = ranksJson.getAsJsonObject();

				if (json.has("default_ranks") && json.has("ranks"))
				{
					for (Map.Entry<String, JsonElement> entry : json.get("ranks").getAsJsonObject().entrySet())
					{
						ranks.put(entry.getKey(), new Rank(this, entry.getKey(), null));
					}

					for (Map.Entry<String, JsonElement> entry : json.get("ranks").getAsJsonObject().entrySet())
					{
						ranks.get(entry.getKey()).fromJson(entry.getValue());
					}

					JsonObject dr = json.get("default_ranks").getAsJsonObject();
					defaultPlayerRank = dr.has("player") ? ranks.get(dr.get("player").getAsString()) : null;
					defaultOPRank = dr.has("op") ? ranks.get(dr.get("op").getAsString()) : null;

					saveRanks();
				}
			}

			try
			{
				ranksJson = DataReader.get(new File(CommonUtils.folderLocal, "ftbutilities/player_ranks.json")).safeJson();

				if (ranksJson.isJsonObject())
				{
					for (Map.Entry<String, JsonElement> entry : ranksJson.getAsJsonObject().entrySet())
					{
						ForgePlayer player = universe.getPlayer(entry.getKey());

						if (player != null)
						{
							String s = entry.getValue().getAsString();

							if (ranks.containsKey(s))
							{
								playerMap.put(player.getId(), ranks.get(s));
							}
						}
					}

					savePlayerRanks();
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				result = false;
			}
		}

		updateRankNames();
		saveRanks();
		savePlayerRanks();
		return result;
	}

	public void saveRanks()
	{
		JsonObject json = new JsonObject();
		JsonObject o1 = new JsonObject();
		o1.addProperty("player", getDefaultPlayerRank().getName());
		o1.addProperty("op", getDefaultOPRank().getName());
		json.add("default_ranks", o1);
		o1 = new JsonObject();

		for (Rank r : ranks.values())
		{
			JsonElement e = r.getSerializableElement();

			if (!e.isJsonNull())
			{
				o1.add(r.getName(), e);
			}
		}

		json.add("ranks", o1);
		JsonUtils.toJsonSafe(new File(CommonUtils.folderLocal, "ftbutilities/ranks.json"), json);
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

	private void savePlayerRanks()
	{
		JsonObject json = new JsonObject();

		for (Map.Entry<UUID, Rank> entry : playerMap.entrySet())
		{
			ForgePlayer player = universe.getPlayer(entry.getKey());

			if (player != null)
			{
				json.add(player.getName(), new JsonPrimitive(entry.getValue().getName()));
			}
		}

		JsonUtils.toJsonSafe(new File(CommonUtils.folderLocal, "ftbutilities/player_ranks.json"), json);
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