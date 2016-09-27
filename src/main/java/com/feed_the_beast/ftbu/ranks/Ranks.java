package com.feed_the_beast.ftbu.ranks;

import com.feed_the_beast.ftbl.api.config.IConfigValue;
import com.feed_the_beast.ftbl.api.rankconfig.IRankConfig;
import com.feed_the_beast.ftbl.api.rankconfig.IRankConfigHandler;
import com.feed_the_beast.ftbl.api.rankconfig.RankConfigAPI;
import com.feed_the_beast.ftbl.lib.util.LMFileUtils;
import com.feed_the_beast.ftbl.lib.util.LMJsonUtils;
import com.feed_the_beast.ftbl.lib.util.LMServerUtils;
import com.feed_the_beast.ftbl.lib.util.LMStringUtils;
import com.feed_the_beast.ftbl.lib.util.LMUtils;
import com.feed_the_beast.ftbu.api.IRank;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.authlib.GameProfile;
import net.minecraftforge.server.permission.DefaultPermissionHandler;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.IPermissionHandler;
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

public enum Ranks implements IPermissionHandler, IRankConfigHandler
{
    INSTANCE;

    public final File fileRanks, filePlayers;
    public final Map<String, IRank> RANKS = new LinkedHashMap<>();
    public final Map<UUID, IRank> PLAYER_MAP = new HashMap<>();
    public IRank defaultRank;

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
            JsonElement e = LMJsonUtils.fromJson(LMFileUtils.newFile(filePlayers));

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

        List<String> nodes = new ArrayList<>(getRegisteredNodes());
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
                String desc = getNodeDescription(s);

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
                IConfigValue value = p.getDefaultValue();
                list.add("> " + p.getName() + " | Default Player Value: " + value + " | Default OP Value: " + p.getDefaultOPValue());

                if(!p.getDescription().isEmpty())
                {
                    list.add("| " + p.getDescription());
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
    }

    public IRank getRank(String s)
    {
        return RANKS.get(s);
    }

    public IRank getRankOf(GameProfile profile)
    {
        if(defaultRank != null)
        {
            IRank r = PLAYER_MAP.get(profile.getId());
            return (r == null) ? defaultRank : r;
        }

        return LMServerUtils.isOP(profile) ? DefaultOPRank.INSTANCE : DefaultPlayerRank.INSTANCE;
    }

    public void setRank(UUID player, IRank rank)
    {
        if(defaultRank != null)
        {
            PLAYER_MAP.put(player, rank);
        }
    }

    @Override
    public void registerNode(String s, DefaultPermissionLevel defaultPermissionLevel, String s1)
    {
        DefaultPermissionHandler.INSTANCE.registerNode(s, defaultPermissionLevel, s1);
    }

    @Override
    public Collection<String> getRegisteredNodes()
    {
        return DefaultPermissionHandler.INSTANCE.getRegisteredNodes();
    }

    @Override
    public String getNodeDescription(String s)
    {
        return DefaultPermissionHandler.INSTANCE.getNodeDescription(s);
    }

    @Override
    public boolean hasPermission(GameProfile profile, String permission, @Nullable IContext context)
    {
        switch(getRankOf(profile).hasPermission(permission))
        {
            case ALLOW:
                return true;
            case DENY:
                return false;
            default:
                return DefaultPermissionHandler.INSTANCE.hasPermission(profile, permission, context);
        }
    }

    @Override
    public IConfigValue getRankConfig(GameProfile profile, IRankConfig id)
    {
        return getRankOf(profile).getConfig(id);
    }
}