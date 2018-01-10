package com.feed_the_beast.ftbutilities.ranks;

import com.feed_the_beast.ftblib.FTBLibModCommon;
import com.feed_the_beast.ftblib.lib.config.RankConfigValueInfo;
import com.feed_the_beast.ftblib.lib.util.CommonUtils;
import com.feed_the_beast.ftblib.lib.util.FileUtils;
import com.feed_the_beast.ftblib.lib.util.JsonUtils;
import com.feed_the_beast.ftblib.lib.util.ServerUtils;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftbutilities.FTBUCommon;
import com.feed_the_beast.ftbutilities.FTBUConfig;
import com.feed_the_beast.ftbutilities.FTBUFinals;
import com.feed_the_beast.ftbutilities.data.NodeEntry;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.authlib.GameProfile;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.server.permission.DefaultPermissionHandler;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Ranks
{
	private static final Map<String, Rank> RANKS = new LinkedHashMap<>();
	private static final Collection<String> RANK_NAMES = new ArrayList<>();
	private static final Map<UUID, Rank> PLAYER_MAP = new HashMap<>();
	private static Rank defaultPlayerRank, defaultOPRank;
	public static final Collection<String> CMD_PERMISSION_NODES = new HashSet<>();

	@Nullable
	public static Rank getRank(String id, @Nullable Rank nullrank)
	{
		if (id.equals(DefaultPlayerRank.INSTANCE.getName()))
		{
			return DefaultPlayerRank.INSTANCE;
		}
		else if (id.equals(DefaultOPRank.INSTANCE.getName()))
		{
			return DefaultOPRank.INSTANCE;
		}

		Rank r = RANKS.get(id);
		return r == null ? nullrank : r;
	}

	public static Rank getDefaultPlayerRank()
	{
		if (defaultPlayerRank == null)
		{
			Rank r = new Rank("player");
			r.parent = DefaultPlayerRank.INSTANCE;
			RANKS.put(r.getName(), r);
			defaultPlayerRank = r;
		}

		return defaultPlayerRank;
	}

	public static Rank getDefaultOPRank()
	{
		if (defaultOPRank == null)
		{
			Rank r = new Rank("op");
			r.parent = DefaultOPRank.INSTANCE;
			r.syntax = "<" + TextFormatting.DARK_GREEN + "$name" + TextFormatting.RESET + "> ";
			RANKS.put(r.getName(), r);
			defaultOPRank = r;
		}

		return defaultOPRank;
	}

	public static Rank getRank(GameProfile profile)
	{
		Rank r = FTBUConfig.ranks.enabled ? PLAYER_MAP.get(profile.getId()) : null;
		return (r == null) ? (ServerUtils.isOP(profile) ? getDefaultOPRank() : getDefaultPlayerRank()) : r;
	}

	public static void addRank(Rank rank)
	{
		RANKS.put(rank.getName(), rank);
		updateRankNames();
		saveRanks();
	}

	public static void setRank(UUID id, @Nullable Rank r)
	{
		if (r == null)
		{
			PLAYER_MAP.remove(id);
		}
		else
		{
			PLAYER_MAP.put(id, r);
		}

		saveRanks();
	}

	public static Collection<String> getRankNames()
	{
		return RANK_NAMES;
	}

	public static void updateRankNames()
	{
		RANK_NAMES.clear();
		RANK_NAMES.addAll(RANKS.keySet());
		RANK_NAMES.add("none");
		RANK_NAMES.remove(DefaultPlayerRank.INSTANCE.getName());
		RANK_NAMES.remove(DefaultOPRank.INSTANCE.getName());
	}

	public static boolean reload()
	{
		FTBUFinals.LOGGER.info("Loading ranks..");

		RANKS.clear();
		RANKS.put(DefaultPlayerRank.INSTANCE.getName(), DefaultPlayerRank.INSTANCE);
		RANKS.put(DefaultOPRank.INSTANCE.getName(), DefaultOPRank.INSTANCE);
		PLAYER_MAP.clear();
		defaultPlayerRank = null;
		defaultOPRank = null;
		boolean result = true;

		if (FTBUConfig.ranks.enabled)
		{
			JsonElement e = JsonUtils.fromJson(new File(CommonUtils.folderLocal, "ftbutilities/ranks.json"));

			if (e.isJsonObject())
			{
				JsonObject o = e.getAsJsonObject();

				if (o.has("default_ranks") && o.has("ranks"))
				{
					for (Map.Entry<String, JsonElement> entry : o.get("ranks").getAsJsonObject().entrySet())
					{
						RANKS.put(entry.getKey(), new Rank(entry.getKey()));
					}

					for (Map.Entry<String, JsonElement> entry : o.get("ranks").getAsJsonObject().entrySet())
					{
						RANKS.get(entry.getKey()).fromJson(entry.getValue());
					}

					JsonObject dr = o.get("default_ranks").getAsJsonObject();
					defaultPlayerRank = dr.has("player") ? RANKS.get(dr.get("player").getAsString()) : null;
					defaultOPRank = dr.has("op") ? RANKS.get(dr.get("op").getAsString()) : null;
				}
			}

			try
			{
				e = JsonUtils.fromJson(new File(CommonUtils.folderLocal, "ftbutilities/player_ranks.json"));

				if (e.isJsonObject())
				{
					for (Map.Entry<String, JsonElement> entry : e.getAsJsonObject().entrySet())
					{
						UUID id = StringUtils.fromString(entry.getKey());
						if (id != null)
						{
							String s = entry.getValue().getAsString();

							if (RANKS.containsKey(s))
							{
								PLAYER_MAP.put(id, RANKS.get(s));
							}
						}
					}
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
		return result;
	}

	private static void saveRanks()
	{
		JsonObject o = new JsonObject();
		JsonObject o1 = new JsonObject();
		o1.addProperty("player", getDefaultPlayerRank().getName());
		o1.addProperty("op", getDefaultOPRank().getName());
		o.add("default_ranks", o1);
		o1 = new JsonObject();

		for (Rank r : RANKS.values())
		{
			JsonElement e = r.getSerializableElement();

			if (!e.isJsonNull())
			{
				o1.add(r.getName(), e);
			}
		}

		o.add("ranks", o1);

		JsonUtils.toJson(o, new File(CommonUtils.folderLocal, "ftbutilities/ranks.json"));

		final JsonObject o2 = new JsonObject();
		PLAYER_MAP.forEach((key, value) -> o2.add(StringUtils.fromUUID(key), new JsonPrimitive(value.getName())));
		JsonUtils.toJson(o2, new File(CommonUtils.folderLocal, "ftbutilities/player_ranks.json"));
	}

	static boolean checkCommandPermission(MinecraftServer server, ICommandSender sender, ICommand parent, String permission)
	{
		if (sender instanceof EntityPlayerMP)
		{
			Event.Result result = getRank(((EntityPlayerMP) sender).getGameProfile()).hasPermission(permission);
			return result == Event.Result.DEFAULT ? parent.checkPermission(server, sender) : (result == Event.Result.ALLOW);
		}

		return parent.checkPermission(server, sender);
	}

	public static void generateExampleFiles()
	{
		List<NodeEntry> allNodes = new ArrayList<>();

		for (NodeEntry node : FTBUCommon.CUSTOM_PERM_PREFIX_REGISTRY)
		{
			allNodes.add(new NodeEntry(node.getName() + "*", node.getLevel(), node.getDescription()));
		}

		for (String s : PermissionAPI.getPermissionHandler().getRegisteredNodes())
		{
			DefaultPermissionLevel level = DefaultPermissionHandler.INSTANCE.getDefaultPermissionLevel(s);
			String desc = PermissionAPI.getPermissionHandler().getNodeDescription(s);

			boolean printNode = true;

			for (NodeEntry cprefix : FTBUCommon.CUSTOM_PERM_PREFIX_REGISTRY)
			{
				if (s.startsWith(cprefix.getName()))
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
				allNodes.add(new NodeEntry(s, level, desc));
			}
		}

		allNodes.sort(StringUtils.ID_COMPARATOR);
		List<String> list = new ArrayList<>();

		try
		{
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
				list.add("<tr><td>" + entry.getName() + "</td><td class='" + entry.getLevel().name().toLowerCase() + "'>" + entry.getLevel() + "</td><td>");

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
			FileUtils.save(new File(CommonUtils.folderLocal, "ftbutilities/all_permissions.html"), list);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		List<RankConfigValueInfo> sortedRankConfigKeys = new ArrayList<>(FTBLibModCommon.RANK_CONFIGS_MIRROR.values());
		sortedRankConfigKeys.sort(StringUtils.ID_COMPARATOR);

		try
		{
			list.clear();
			list.add("<html><head><title>Rank Configs</title>");
			list.add("<style>table{font-family: arial, sans-serif;border-collapse: collapse;}td,th{border:1px solid #666666;text-align: left;padding:8px;}p,ul{margin:4px;}</style>");
			list.add("</head><body><h1>Rank Configs</h1><h3>Modifying this file won't have any effect!</h3><table>");
			list.add("<tr><th>Rank Config</th><th>Def Value</th><th>Info</th></tr>");

			List<String> infoList = new ArrayList<>();

			for (RankConfigValueInfo p : sortedRankConfigKeys)
			{
				list.add("<tr><td>" + p.id + "</td><td>");

				p.defaultValue.addInfo(p, infoList);
				List<String> variants = p.defaultValue.getVariants();

				if (!infoList.isEmpty() || !variants.isEmpty())
				{
					list.add("<ul><li>Default: " + p.defaultValue.getSerializableElement() + "</li>");
					list.add("<li>OP Default: " + p.defaultOPValue.getSerializableElement() + "</li>");

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

				String info = StringUtils.translate(p.displayName.isEmpty() ? ("rank_config." + p.id) : p.displayName);

				if (!info.isEmpty())
				{
					for (String s1 : info.split("\\\\n"))
					{
						list.add("<p>" + s1 + "</p>");
					}
				}

				list.add("</td></tr>");
			}

			list.add("</table></body></html>");
			FileUtils.save(new File(CommonUtils.folderLocal, "ftbutilities/all_configs.html"), list);

			list.clear();

			for (String node : PermissionAPI.getPermissionHandler().getRegisteredNodes())
			{
				list.add(node + ": " + DefaultPermissionHandler.INSTANCE.getDefaultPermissionLevel(node));
			}

			list.addAll(CMD_PERMISSION_NODES);
			Collections.sort(list);
			list.add(0, "");
			list.add(0, "Modifying this file won't have any effect!");
			list.add(0, PermissionAPI.getPermissionHandler().getRegisteredNodes().size() + " nodes in total");
			FileUtils.save(new File(CommonUtils.folderLocal, "ftbutilities/all_permissions_full_list.txt"), list);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}