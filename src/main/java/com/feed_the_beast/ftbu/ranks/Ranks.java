package com.feed_the_beast.ftbu.ranks;

import com.feed_the_beast.ftbl.api.config.IConfigTree;
import com.feed_the_beast.ftbl.api.config.IConfigValue;
import com.feed_the_beast.ftbl.api.rankconfig.IRankConfig;
import com.feed_the_beast.ftbl.api.rankconfig.RankConfigAPI;
import com.feed_the_beast.ftbl.lib.config.ConfigTree;
import com.feed_the_beast.ftbl.lib.util.LMFileUtils;
import com.feed_the_beast.ftbl.lib.util.LMJsonUtils;
import com.feed_the_beast.ftbl.lib.util.LMStringUtils;
import com.feed_the_beast.ftbl.lib.util.LMUtils;
import com.feed_the_beast.ftbu.api.IRank;
import com.feed_the_beast.ftbu.api_impl.FTBUtilitiesAPI_Impl;
import com.feed_the_beast.ftbu.config.FTBUConfigGeneral;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraftforge.server.permission.DefaultPermissionHandler;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public enum Ranks
{
    INSTANCE;

    public final Map<String, IRank> RANKS = new LinkedHashMap<>();
    public final Map<UUID, IRank> PLAYER_MAP = new HashMap<>();
    public IRank defaultRank;
    public IConfigTree ranksConfigTree;
    private final Collection<NodeEntry> CUSTOM_PERM_PREFIX_REGISTRY = new HashSet<>();
    public final List<NodeEntry> ALL_NODES = new ArrayList<>();

    public void registerCustomPermPrefix(NodeEntry entry)
    {
        CUSTOM_PERM_PREFIX_REGISTRY.add(entry);
    }

    public void reload()
    {
        RANKS.clear();
        PLAYER_MAP.clear();
        defaultRank = null;
        ranksConfigTree = new ConfigTree();

        if(FTBUConfigGeneral.RANKS_ENABLED.getBoolean())
        {
            try
            {
                JsonElement e = LMJsonUtils.fromJson(new File(LMUtils.folderLocal, "ftbu/ranks.json"));

                if(e.isJsonObject())
                {
                    JsonObject o = e.getAsJsonObject();

                    for(Map.Entry<String, JsonElement> entry : o.get("ranks").getAsJsonObject().entrySet())
                    {
                        RANKS.put(entry.getKey(), new Rank(entry.getKey()));
                    }

                    for(Map.Entry<String, JsonElement> entry : o.get("ranks").getAsJsonObject().entrySet())
                    {
                        RANKS.get(entry.getKey()).fromJson(entry.getValue().getAsJsonObject());
                    }

                    defaultRank = RANKS.get(o.get("default_rank").getAsString());
                }
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
                defaultRank = null;
            }
        }
        else
        {
            try
            {
                JsonElement e = LMJsonUtils.fromJson(new File(LMUtils.folderLocal, "ftbu/ranks_default.json"));

                if(e.isJsonObject())
                {
                    JsonObject o = e.getAsJsonObject();
                    JsonElement rp = o.get(DefaultPlayerRank.INSTANCE.getName());

                    if(rp != null && rp.isJsonObject())
                    {
                        DefaultPlayerRank.INSTANCE.fromJson(rp);
                    }

                    JsonElement ro = o.get(DefaultPlayerRank.INSTANCE.getName());

                    if(ro != null && ro.isJsonObject())
                    {
                        DefaultOPRank.INSTANCE.fromJson(ro);
                    }
                }
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }

        try
        {
            JsonElement e = LMJsonUtils.fromJson(new File(LMUtils.folderLocal, "ftbu/player_ranks.json"));

            if(e.isJsonObject())
            {
                for(Map.Entry<String, JsonElement> entry : e.getAsJsonObject().entrySet())
                {
                    UUID id = LMStringUtils.fromString(entry.getKey());
                    if(id != null)
                    {
                        String s = entry.getValue().getAsString();

                        if(RANKS.containsKey(s))
                        {
                            PLAYER_MAP.put(id, RANKS.get(s));
                        }
                    }
                }
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            defaultRank = null;
        }

        saveRanks();
    }

    public void saveRanks()
    {
        if(defaultRank != null)
        {
            JsonObject o = new JsonObject();
            o.add("default_rank", new JsonPrimitive(defaultRank.getName()));
            JsonObject o1 = new JsonObject();

            for(IRank r : RANKS.values())
            {
                o1.add(r.getName(), r.getSerializableElement());
            }

            o.add("ranks", o1);
            LMJsonUtils.toJson(new File(LMUtils.folderLocal, "ftbu/ranks.json"), o);

            JsonObject o2 = new JsonObject();
            PLAYER_MAP.forEach((key, value) -> o2.add(LMStringUtils.fromUUID(key), new JsonPrimitive(value.getName())));
            LMJsonUtils.toJson(new File(LMUtils.folderLocal, "ftbu/player_ranks.json"), o2);
        }
        else
        {
            JsonObject o = new JsonObject();
            o.add(DefaultPlayerRank.INSTANCE.getName(), DefaultPlayerRank.INSTANCE.getSerializableElement());
            o.add(DefaultOPRank.INSTANCE.getName(), DefaultOPRank.INSTANCE.getSerializableElement());
            LMJsonUtils.toJson(new File(LMUtils.folderLocal, "ftbu/default_ranks.json"), o);
        }
    }

    public void generateExampleFiles()
    {
        List<String> list = new ArrayList<>();

        try
        {
            Random random = new Random(50382L);

            list.add("# Ranks & Permissions #");
            list.add("");
            list.add("If you have enabled ranks, edit ranks.json. If not, default_ranks.json");
            list.add("Player UUID:Rank map is saved in player_ranks.json");
            list.add("Format:");
            list.add("");
            list.add("{");
            list.add("  \"" + LMStringUtils.fromUUID(new UUID(0L, 0L)) + "\":\"some_rank\",");
            list.add("  \"" + LMStringUtils.fromUUID(new UUID(random.nextLong(), random.nextLong())) + "\":\"other_rank\"");
            list.add("}");
            list.add("");
            list.add("-");
            LMFileUtils.save(new File(LMUtils.folderLocal, "ftbu/readme.txt"), list);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        ALL_NODES.clear();

        for(NodeEntry node : CUSTOM_PERM_PREFIX_REGISTRY)
        {
            ALL_NODES.add(new NodeEntry(node.getName() + "*", node.getLevel(), node.getDescription()));
        }

        for(String s : FTBUtilitiesAPI_Impl.INSTANCE.getRegisteredNodes())
        {
            DefaultPermissionLevel level = DefaultPermissionHandler.INSTANCE.getDefaultPermissionLevel(s);
            String desc = FTBUtilitiesAPI_Impl.INSTANCE.getNodeDescription(s);

            boolean printNode = true;

            for(NodeEntry cprefix : CUSTOM_PERM_PREFIX_REGISTRY)
            {
                if(s.startsWith(cprefix.getName()))
                {
                    if(level == cprefix.getLevel() && desc.isEmpty())
                    {
                        printNode = false;
                    }

                    break;
                }
            }

            if(printNode)
            {
                ALL_NODES.add(new NodeEntry(s, level, desc));
            }
        }

        Collections.sort(ALL_NODES, LMStringUtils.ID_COMPARATOR);

        try
        {
            list.clear();
            list.add("<html><head><title>Permissions</title>");
            list.add("<style>table{font-family: arial, sans-serif;border-collapse: collapse;}td,th{border:1px solid #666666;text-align: left;padding:8px;}</style>");
            list.add("</head><body><h1>Permissions</h1><table>");
            list.add("<tr><th>Permission Node</th><th></th><th>Info</th></tr>");

            for(NodeEntry entry : ALL_NODES)
            {
                list.add("<tr><td>" + entry.getName() + "</td><td bgcolor=\"" + entry.getColor() + "\">" + entry.getLevel() + "</td><td>");

                if(entry.getDescription() != null)
                {
                    for(String s1 : entry.getDescription().split("\n"))
                    {
                        list.add("<p>" + s1 + "</p>");
                    }
                }

                list.add("</td></tr>");
            }

            list.add("</table></body></html>");
            LMFileUtils.save(new File(LMUtils.folderLocal, "ftbu/all_permissions.html"), list);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        List<IRankConfig> sortedRankConfigs = new ArrayList<>(RankConfigAPI.getRegistredRankConfigs().values());
        Collections.sort(sortedRankConfigs, LMStringUtils.ID_COMPARATOR);

        try
        {
            list.clear();
            list.add("<html><head><title>Rank Configs</title>");
            list.add("<style>table{font-family: arial, sans-serif;border-collapse: collapse;}td,th{border:1px solid #666666;text-align: left;padding:8px;}</style>");
            list.add("</head><body><h1>Rank Configs</h1><table>");
            list.add("<tr><th>Rank Config</th><th>Def Value</th><th>Def OP Value</th><th>Info</th></tr>");

            for(IRankConfig p : sortedRankConfigs)
            {
                IConfigValue value = p.getDefValue();
                list.add("<tr><td>" + p.getName() + "</td><td>");

                String min = value.getMinValueString();
                String max = value.getMaxValueString();
                List<String> variants = value.getVariants();

                if(min != null || max != null || variants != null)
                {
                    list.add("<ul><li>Default: ");
                    list.add(value.getSerializableElement() + "</li>");

                    if(min != null)
                    {
                        list.add("<li>Min: " + min + "</li>");
                    }

                    if(max != null)
                    {
                        list.add("<li>Max: " + max + "</li>");
                    }

                    if(variants != null)
                    {
                        list.add("<li>Variants:<ul>");
                        variants = new ArrayList<>(variants);
                        Collections.sort(variants, LMStringUtils.IGNORE_CASE_COMPARATOR);

                        for(String s : variants)
                        {
                            list.add("<li>" + s + "</li>");
                        }

                        list.add("</ul></li>");
                    }

                    list.add("</ul>");
                }
                else
                {
                    list.add("Default: " + value.getSerializableElement());
                }

                list.add("</td><td>" + p.getDefOPValue().getSerializableElement() + "</td><td>");

                if(!p.getInfo().isEmpty())
                {
                    String[] info = p.getInfo().split("\n");

                    if(info.length > 1)
                    {
                        Arrays.sort(info);
                        list.add("<ul>");
                        for(String s1 : info)
                        {
                            list.add("<li>" + s1 + "</li>");
                        }
                        list.add("</ul>");
                    }
                    else
                    {
                        list.add(info[0]);
                    }
                }

                list.add("</td></tr>");
            }

            list.add("</table></body></html>");
            LMFileUtils.save(new File(LMUtils.folderLocal, "ftbu/all_configs.html"), list);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        try
        {
            Rank player = new DefaultPlayerRank();

            for(IRankConfig key : sortedRankConfigs)
            {
                player.config.add(key, key.getDefValue());
            }

            Rank op = new DefaultOPRank();
            op.permissions.put("*", true);

            for(IRankConfig key : sortedRankConfigs)
            {
                if(!key.getDefValue().equalsValue(key.getDefOPValue()))
                {
                    op.config.add(key, key.getDefOPValue());
                }
            }

            JsonObject r = new JsonObject();
            r.add(player.getName(), player.getSerializableElement());
            r.add(op.getName(), op.getSerializableElement());
            JsonObject o = new JsonObject();
            o.add("default_rank", new JsonPrimitive(player.getName()));
            o.add("ranks", r);
            LMJsonUtils.toJson(new File(LMUtils.folderLocal, "ftbu/ranks_example.json"), o);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
}