package com.feed_the_beast.ftbu.ranks;

import com.feed_the_beast.ftbl.api.config.IConfigValue;
import com.feed_the_beast.ftbl.api.rankconfig.IRankConfig;
import com.feed_the_beast.ftbl.api.rankconfig.IRankConfigHandler;
import com.feed_the_beast.ftbl.api.rankconfig.RankConfigAPI;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.latmod.lib.util.LMFileUtils;
import com.latmod.lib.util.LMJsonUtils;
import com.latmod.lib.util.LMServerUtils;
import com.latmod.lib.util.LMStringUtils;
import com.latmod.lib.util.LMUtils;
import com.mojang.authlib.GameProfile;
import net.minecraft.util.text.TextFormatting;
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

    public final Rank PLAYER = new Rank("Player");
    public final Rank ADMIN = new Rank("Admin");
    public final File fileRanks, filePlayers;
    public final Map<String, Rank> RANKS = new LinkedHashMap<>();
    public final Map<UUID, Rank> PLAYER_MAP = new HashMap<>();
    public Rank defaultRank;

    Ranks()
    {
        fileRanks = new File(LMUtils.folderLocal, "ftbu/ranks.json");
        filePlayers = new File(LMUtils.folderLocal, "ftbu/player_ranks.json");
        ADMIN.color = TextFormatting.DARK_GREEN;
        PLAYER.color = TextFormatting.WHITE;
        ADMIN.parent = PLAYER;
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
                o.add("default_rank", new JsonPrimitive(PLAYER.getName()));
                JsonObject o1 = new JsonObject();
                o1.add(PLAYER.getName(), PLAYER.getSerializableElement());
                o1.add(ADMIN.getName(), ADMIN.getSerializableElement());
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

            for(Rank r : RANKS.values())
            {
                o1.add(r.getName(), r.getSerializableElement());
            }

            o.add("ranks", o1);
            LMJsonUtils.toJson(fileRanks, o);

            o = new JsonObject();
            for(Map.Entry<UUID, Rank> entry : PLAYER_MAP.entrySet())
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

            for(String s : nodes)
            {
                list.add("> " + s + " | Default Permission Level: " + DefaultPermissionHandler.INSTANCE.getDefaultPermissionLevel(s));
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

        try
        {
            JsonObject o = new JsonObject();

            o.add("default_rank", new JsonPrimitive("Player"));

            JsonObject o1 = new JsonObject();

            Rank rankPlayer = new Rank(PLAYER.getName());
            rankPlayer.fromJson(PLAYER.getSerializableElement());

            for(IRankConfig p : sortedRankConfigs)
            {
                rankPlayer.config.put(p, p.getDefaultValue());
            }

            rankPlayer.permissions.clear();
            o1.add(rankPlayer.getName(), rankPlayer.getSerializableElement());

            Rank rankAdmin = new Rank(ADMIN.getName());
            rankAdmin.parent = rankPlayer;
            rankAdmin.fromJson(ADMIN.getSerializableElement());

            for(IRankConfig p : sortedRankConfigs)
            {
                if(!p.getDefaultValue().equalsValue(p.getDefaultOPValue()))
                {
                    rankAdmin.config.put(p, p.getDefaultOPValue());
                }
            }

            rankAdmin.permissions.put("*", true);
            o1.add(rankAdmin.getName(), rankAdmin.getSerializableElement());

            o.add("ranks", o1);

            LMJsonUtils.toJson(new File(LMUtils.folderLocal, "ftbu/ranks_example.json"), o);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public Rank getRank(String s)
    {
        return RANKS.get(s);
    }

    public Rank getRankOf(GameProfile profile)
    {
        if(defaultRank != null)
        {
            Rank r = PLAYER_MAP.get(profile.getId());
            return (r == null) ? defaultRank : r;
        }

        return LMServerUtils.isOP(profile) ? ADMIN : PLAYER;
    }

    public void setRank(UUID player, Rank rank)
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
        switch(getRankOf(profile).handlePermission(permission))
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
        return getRankOf(profile).handleRankConfig(id);
    }
}