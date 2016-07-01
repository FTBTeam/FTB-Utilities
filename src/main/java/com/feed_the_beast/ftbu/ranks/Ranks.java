package com.feed_the_beast.ftbu.ranks;

import com.feed_the_beast.ftbl.api.permissions.Context;
import com.feed_the_beast.ftbl.api.permissions.PermissionHandler;
import com.feed_the_beast.ftbl.api.permissions.rankconfig.RankConfig;
import com.feed_the_beast.ftbl.api.permissions.rankconfig.RankConfigAPI;
import com.feed_the_beast.ftbl.util.FTBLib;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.latmod.lib.json.LMJsonUtils;
import com.latmod.lib.util.LMFileUtils;
import com.latmod.lib.util.LMUtils;
import com.mojang.authlib.GameProfile;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Ranks implements PermissionHandler, RankConfigAPI.Handler
{
    public static final Rank PLAYER = new Rank("Player");
    public static final Rank ADMIN = new Rank("Admin");
    private static Ranks instance;
    public final File fileRanks, filePlayers;
    public final Map<String, Rank> ranks = new LinkedHashMap<>();
    public final Map<UUID, Rank> playerMap = new HashMap<>();
    public Rank defaultRank;

    private Ranks()
    {
        fileRanks = new File(FTBLib.folderLocal, "ftbu/ranks.json");
        filePlayers = new File(FTBLib.folderLocal, "ftbu/player_ranks.json");
        ADMIN.color = TextFormatting.DARK_GREEN;
        PLAYER.color = TextFormatting.WHITE;
        ADMIN.parent = PLAYER;
    }

    public static Ranks instance()
    {
        if(instance == null)
        {
            instance = new Ranks();
        }
        return instance;
    }

    public void reload()
    {
        ranks.clear();
        playerMap.clear();
        defaultRank = null;

        try
        {
            JsonElement e = LMJsonUtils.fromJson(fileRanks);

            if(e.isJsonObject())
            {
                JsonObject o = e.getAsJsonObject();

                for(Map.Entry<String, JsonElement> entry : o.get("ranks").getAsJsonObject().entrySet())
                {
                    ranks.put(entry.getKey(), new Rank(entry.getKey()));
                }

                for(Map.Entry<String, JsonElement> entry : o.get("ranks").getAsJsonObject().entrySet())
                {
                    ranks.get(entry.getKey()).fromJson(entry.getValue().getAsJsonObject());
                }

                defaultRank = ranks.get(o.get("default_rank").getAsString());
            }
            else
            {
                JsonObject o = new JsonObject();
                o.add("default_rank", new JsonPrimitive(PLAYER.getID()));
                JsonObject o1 = new JsonObject();
                o1.add(PLAYER.getID(), PLAYER.getSerializableElement());
                o1.add(ADMIN.getID(), ADMIN.getSerializableElement());
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
                    UUID id = LMUtils.fromString(entry.getKey());
                    if(id != null)
                    {
                        String s = entry.getValue().getAsString();

                        if(ranks.containsKey(s))
                        {
                            playerMap.put(id, ranks.get(s));
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
            o.add("default_rank", new JsonPrimitive(defaultRank.getID()));
            JsonObject o1 = new JsonObject();

            for(Rank r : ranks.values())
            {
                o1.add(r.getID(), r.getSerializableElement());
            }

            o.add("ranks", o1);
            LMJsonUtils.toJson(fileRanks, o);

            o = new JsonObject();
            for(Map.Entry<UUID, Rank> entry : playerMap.entrySet())
            {
                o.add(LMUtils.fromUUID(entry.getKey()), new JsonPrimitive(entry.getValue().getID()));
            }
            LMJsonUtils.toJson(filePlayers, o);
        }
    }

    public void generateExampleFiles()
    {
        List<RankConfig> sortedRankConfigs = new ArrayList<>();
        sortedRankConfigs.addAll(RankConfigAPI.getRankConfigValues());
        Collections.sort(sortedRankConfigs, LMUtils.ID_COMPARATOR);

        try
        {
            List<String> list = new ArrayList<>();

            list.add("Modifying this file won't do anything, it just shows all available permission IDs. See ranks_example.json");
            list.add("");

            list.add("-- Permissions --");
            list.add("");

            list.add("-- Config --");
            list.add("");

            String[] info;

            for(RankConfig p : sortedRankConfigs)
            {
                list.add(p.getID());
                /*
                info = p.getInfo();
				
				if(info != null && info.length > 0)
				{
					for(String s : info)
					{
						list.add("  " + s);
					}
				}
				
				FIXME: if(!PrimitiveType.isNull(p.configData.type))
				{
					list.add("  Type: " + p.configData.type);
				}
				
				if(p.getMin() != Double.NEGATIVE_INFINITY)
				{
					if(p.configData.type == PrimitiveType.DOUBLE || p.configData.type == PrimitiveType.FLOAT)
						list.add("  Min: " + p.getMin());
					else list.add("  Min: " + (long) p.getMin());
				}
				
				if(p.getMax() != Double.POSITIVE_INFINITY)
				{
					if(p.configData.type == PrimitiveType.DOUBLE || p.configData.type == PrimitiveType.FLOAT)
						list.add("  Max: " + p.getMax());
					else list.add("  Max: " + (long) p.getMax());
				}*/

                list.add("");
            }

            LMFileUtils.save(new File(FTBLib.folderLocal, "ftbu/all_permissions.txt"), list);
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

            Rank rankPlayer = new Rank(PLAYER.getID());
            rankPlayer.fromJson(PLAYER.getSerializableElement());

            for(RankConfig p : sortedRankConfigs)
            {
                rankPlayer.config.put(p, p.getDefaultValue(false));
            }

            rankPlayer.permissions.clear();
            o1.add(rankPlayer.getID(), rankPlayer.getSerializableElement());

            Rank rankAdmin = new Rank(ADMIN.getID());
            rankAdmin.parent = rankPlayer;
            rankAdmin.fromJson(ADMIN.getSerializableElement());

            for(RankConfig p : sortedRankConfigs)
            {
                if(!p.getDefaultValue(false).toString().equals(p.getDefaultValue(true).toString()))
                {
                    rankAdmin.config.put(p, p.getDefaultValue(true));
                }
            }

            rankAdmin.permissions.put("*", true);
            o1.add(rankAdmin.getID(), rankAdmin.getSerializableElement());

            o.add("ranks", o1);

            LMJsonUtils.toJson(new File(FTBLib.folderLocal, "ftbu/ranks_example.json"), o);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public Rank getRank(String s)
    {
        return ranks.get(s);
    }

    public Rank getRankOf(GameProfile profile)
    {
        if(defaultRank != null)
        {
            Rank r = playerMap.get(profile.getId());
            return (r == null) ? defaultRank : r;
        }

        return FTBLib.isOP(profile) ? ADMIN : PLAYER;
    }

    public void setRank(UUID player, Rank rank)
    {
        if(defaultRank != null)
        {
            playerMap.put(player, rank);
        }
    }

    @Nonnull
    @Override
    public Event.Result hasPermission(@Nonnull GameProfile profile, @Nonnull String permission, @Nonnull Context context)
    {
        return getRankOf(profile).handlePermission(permission);
    }

    @Override
    public JsonElement getRankConfig(@Nonnull GameProfile profile, @Nonnull RankConfig config)
    {
        return getRankOf(profile).handleRankConfig(config);
    }
}