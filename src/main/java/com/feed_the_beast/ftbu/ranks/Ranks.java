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
import com.feed_the_beast.ftbu.FTBUPermissions;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
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

    private static class NodeEntry
    {
        private static final Comparator<NodeEntry> COMPARATOR = (o1, o2) -> o1.node.compareToIgnoreCase(o2.node);
        private static final Map<DefaultPermissionLevel, String> COLOR_MAP = new EnumMap<>(DefaultPermissionLevel.class);

        static
        {
            COLOR_MAP.put(DefaultPermissionLevel.ALL, "bgcolor=\"#72FF85\"");
            COLOR_MAP.put(DefaultPermissionLevel.OP, "bgcolor=\"#42A3FF\"");
            COLOR_MAP.put(DefaultPermissionLevel.NONE, "bgcolor=\"#FF4242\"");
        }

        private String node;
        private DefaultPermissionLevel level;
        private String desc;
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

        List<NodeEntry> nodes = new ArrayList<>();
        boolean addedBreakPerm = false;
        boolean addedDimWLPerm = false;

        for(String s : FTBUtilitiesAPI_Impl.INSTANCE.getRegisteredNodes())
        {
            DefaultPermissionLevel level = DefaultPermissionHandler.INSTANCE.getDefaultPermissionLevel(s);
            String desc = FTBUtilitiesAPI_Impl.INSTANCE.getNodeDescription(s);

            if(s.startsWith(FTBUPermissions.CLAIMS_BLOCK_BREAK_PREFIX))
            {
                if(!addedBreakPerm)
                {
                    NodeEntry entry = new NodeEntry();
                    entry.node = FTBUPermissions.CLAIMS_BLOCK_BREAK_PREFIX + "*";
                    entry.level = DefaultPermissionLevel.OP;
                    entry.desc = "Permission for blocks that players can break in claimed chunks";
                    nodes.add(entry);
                    addedBreakPerm = true;
                }

                if(level == DefaultPermissionLevel.OP && desc.isEmpty())
                {
                    continue;
                }
            }

            if(s.startsWith(FTBUPermissions.CLAIMS_DIMENSION_ALLOWED_PREFIX))
            {
                if(!addedDimWLPerm)
                {
                    NodeEntry entry = new NodeEntry();
                    entry.node = FTBUPermissions.CLAIMS_DIMENSION_ALLOWED_PREFIX + "*";
                    entry.level = DefaultPermissionLevel.ALL;
                    entry.desc = "Permission for dimensions where claiming chunks is allowed";
                    nodes.add(entry);
                    addedDimWLPerm = true;
                }

                if(level == DefaultPermissionLevel.ALL && desc.isEmpty())
                {
                    continue;
                }
            }

            NodeEntry entry = new NodeEntry();
            entry.node = s;
            entry.level = level;
            entry.desc = desc;
            nodes.add(entry);
        }

        Collections.sort(nodes, NodeEntry.COMPARATOR);

        try
        {
            list.clear();
            list.add("<html><head><title>Permissions</title>");
            list.add("<style>table{font-family: arial, sans-serif;border-collapse: collapse;}td,th{border:1px solid #666666;text-align: left;padding:8px;}</style>");
            list.add("</head><body><h1>Permissions</h1><table>");
            list.add("<tr><th>Permission Node</th><th></th><th>Info</th></tr>");

            for(NodeEntry entry : nodes)
            {
                list.add("<tr><td>" + entry.node + "</td><td " + NodeEntry.COLOR_MAP.get(entry.level) + ">" + entry.level + "</td><td>");

                if(entry.desc != null)
                {
                    for(String s1 : entry.desc.split("\n"))
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
            list.add("# RankConfigs");
            list.add("");
            list.add("> ConfigID | Default Player Value | Default OP Value");
            list.add("");

            for(IRankConfig p : sortedRankConfigs)
            {
                IConfigValue value = p.getDefValue();
                list.add("> " + p.getName() + " | " + value.getSerializableElement() + " | " + p.getDefOPValue().getSerializableElement());

                if(!p.getInfo().isEmpty())
                {
                    for(String s : p.getInfo().split("\n"))
                    {
                        list.add("| " + s);
                    }
                }

                list.add(": Type: " + value.getID());

                String s = value.getMinValueString();

                if(s != null)
                {
                    list.add(": Min: " + s);
                }

                s = value.getMaxValueString();

                if(s != null)
                {
                    list.add(": Max: " + s);
                }

                if(value.getVariants() != null)
                {
                    list.add(": Variants: " + value.getVariants());
                }

                list.add("");
            }

            LMFileUtils.save(new File(LMUtils.folderLocal, "ftbu/all_configs.txt"), list);
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