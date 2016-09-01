package com.feed_the_beast.ftbu.client;

import com.feed_the_beast.ftbl.api.EnumTeamColor;
import com.feed_the_beast.ftbu.badges.Badge;
import com.feed_the_beast.ftbu.badges.BadgeStorage;
import com.feed_the_beast.ftbu.net.MessageRequestBadge;
import com.latmod.lib.io.LMConnection;
import com.latmod.lib.io.RequestMethod;
import com.latmod.lib.math.ChunkDimPos;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by LatvianModder on 30.08.2016.
 */
public class CachedClientData
{
    public static class TeamData
    {
        public EnumTeamColor color;
        public String formattedName;
    }

    public static class ChunkData
    {
        public static final int OWNER = 1;
        public static final int LOADED = 2;

        public TeamData team;
        public byte flags;
    }

    public static final Map<String, TeamData> TEAMS = new HashMap<>();
    public static final Map<ChunkDimPos, ChunkData> CHUNKS = new HashMap<>();
    public static final BadgeStorage GLOBAL_BADGES = new BadgeStorage();
    public static final BadgeStorage LOCAL_BADGES = new BadgeStorage();

    public static void clear()
    {
        TEAMS.clear();
        CHUNKS.clear();
        LOCAL_BADGES.clear();
    }

    public static void reloadGlobalBadges()
    {
        GLOBAL_BADGES.clear();

        Thread thread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    LMConnection connection = new LMConnection(RequestMethod.GET, "http://pastebin.com/raw/Mu8McdDR");
                    GLOBAL_BADGES.loadBadges(connection.connect().asJson());
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        };

        thread.setDaemon(true);
        thread.start();
    }

    public static void setTypes(Map<ChunkDimPos, ChunkData> types)
    {
        for(Map.Entry<ChunkDimPos, ChunkData> e : types.entrySet())
        {
            ChunkDimPos pos = e.getKey();
            ChunkData chunk = e.getValue();

            CHUNKS.put(pos, chunk);

            if(FTBUClient.journeyMapHandler != null)
            {
                FTBUClient.journeyMapHandler.chunkChanged(pos, chunk);
            }
        }
    }

    public static Badge getClientBadge(UUID playerID)
    {
        if(LOCAL_BADGES.badgePlayerMap.containsKey(playerID))
        {
            return LOCAL_BADGES.badgePlayerMap.get(playerID);
        }

        LOCAL_BADGES.badgePlayerMap.put(playerID, null);
        new MessageRequestBadge(playerID).sendToServer();
        return null;
    }
}