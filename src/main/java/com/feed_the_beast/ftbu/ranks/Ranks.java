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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraftforge.server.permission.DefaultPermissionHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public enum Ranks
{
    INSTANCE;

    public final File fileRanks, filePlayers;
    public final Map<String, IRank> RANKS = new LinkedHashMap<>();
    public final Map<UUID, IRank> PLAYER_MAP = new HashMap<>();
    public IRank defaultRank;
    public IConfigTree ranksConfigTree;

    Ranks()
    {
        fileRanks = new File(LMUtils.folderLocal, "ftbu/ranks.json");
        filePlayers = new File(LMUtils.folderLocal, "ftbu/player_ranks.json");
    }

    public void reload()
    {
        RANKS.clear();
        PLAYER_MAP.clear();
        defaultRank = null;
        ranksConfigTree = new ConfigTree();

        try
        {
            JsonElement e = LMJsonUtils.fromJson(fileRanks);

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
            else
            {
                JsonObject o = new JsonObject();
                o.add("default_rank", new JsonPrimitive(DefaultPlayerRank.INSTANCE.getName()));
                JsonObject o1 = new JsonObject();
                o1.add(DefaultPlayerRank.INSTANCE.getName(), DefaultPlayerRank.INSTANCE.getSerializableElement());
                o1.add(DefaultOPRank.INSTANCE.getName(), DefaultOPRank.INSTANCE.getSerializableElement());
                o.add("ranks", o1);
                LMJsonUtils.toJson(fileRanks, o);
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            defaultRank = null;
        }

        try
        {
            JsonElement e = LMJsonUtils.fromJson(filePlayers);

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
            else
            {
                JsonObject o = new JsonObject();
                o.add(new UUID(0L, 0L).toString(), new JsonPrimitive("ExampleRank"));
                LMJsonUtils.toJson(filePlayers, o);
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
            LMJsonUtils.toJson(fileRanks, o);

            o = new JsonObject();

            for(Map.Entry<UUID, IRank> entry : PLAYER_MAP.entrySet())
            {
                o.add(LMStringUtils.fromUUID(entry.getKey()), new JsonPrimitive(entry.getValue().getName()));
            }

            LMJsonUtils.toJson(filePlayers, o);
        }
    }

    public void generateExampleFiles()
    {
        List<IRankConfig> sortedRankConfigs = new ArrayList<>(RankConfigAPI.getRegistredRankConfigs().values());
        Collections.sort(sortedRankConfigs, LMStringUtils.ID_COMPARATOR);

        List<String> nodes = new ArrayList<>(FTBUtilitiesAPI_Impl.INSTANCE.getRegisteredNodes());
        Collections.sort(nodes, LMStringUtils.IGNORE_CASE_COMPARATOR);

        try
        {
            List<String> list = new ArrayList<>();

            list.add("# Permissions and RankConfigs");
            list.add("");
            list.add("Modifying this file won't do anything, it just shows all available permission IDs. See ranks_example.json");
            list.add("");

            list.add("## Permissions");
            list.add("");
            list.add("> permission.node | Default Permission Level");
            list.add("");

            for(String s : nodes)
            {
                list.add("> " + s + " | " + DefaultPermissionHandler.INSTANCE.getDefaultPermissionLevel(s));
                String desc = FTBUtilitiesAPI_Impl.INSTANCE.getNodeDescription(s);

                if(!desc.isEmpty())
                {
                    for(String s1 : desc.split("\n"))
                    {
                        list.add("| " + s1);
                    }

                    list.add("");
                }
            }

            list.add("");
            list.add("## RankConfigs");
            list.add("");

            for(IRankConfig p : sortedRankConfigs)
            {
                IConfigValue value = p.getDefValue();
                list.add("> " + p.getName() + " | Default Player Value: " + value + " | Default OP Value: " + p.getDefOPValue());

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

                list.add("");
            }

            LMFileUtils.save(new File(LMUtils.folderLocal, "ftbu/all_permissions.txt"), list);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        try
        {
            Rank player = new DefaultPlayerRank();

            Rank op = new DefaultOPRank();
            op.permissions.put("*", true);

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