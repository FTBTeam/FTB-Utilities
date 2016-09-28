package com.feed_the_beast.ftbu.client;

import com.feed_the_beast.ftbl.api.EnumTeamColor;
import com.feed_the_beast.ftbl.api.IForgeTeam;
import com.feed_the_beast.ftbl.lib.io.LMConnection;
import com.feed_the_beast.ftbl.lib.io.RequestMethod;
import com.feed_the_beast.ftbu.JourneyMapIntegration;
import com.feed_the_beast.ftbu.badges.Badge;
import com.feed_the_beast.ftbu.badges.BadgeStorage;
import com.feed_the_beast.ftbu.net.MessageRequestBadge;
import net.minecraft.util.math.ChunkPos;

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
        public UUID ownerID;
        public EnumTeamColor color;
        public String formattedName;

        public TeamData(IForgeTeam team)
        {
            ownerID = team.getOwner().getProfile().getId();
            color = team.getColor();
            formattedName = team.getTitle();
        }

        public TeamData()
        {
        }
    }

    public static class ChunkData
    {
        public static final byte NULL = 1;
        public static final byte OWNER = 2;
        public static final byte LOADED = 4;

        public TeamData team;
        public byte flags;

        public ChunkData(TeamData t, byte f)
        {
            team = t;
            flags = f;
        }
    }

    private static int currentDim = 0;
    public static final Map<UUID, TeamData> TEAMS = new HashMap<>();
    public static final Map<ChunkPos, ChunkData> CHUNKS = new HashMap<>();
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

    public static void updateChunkData(int dim, Map<UUID, TeamData> teamData, Map<ChunkPos, ChunkData> chunkData)
    {
        if(currentDim != dim)
        {
            currentDim = dim;

            TEAMS.clear();
            CHUNKS.clear();

            if(FTBUClient.HAS_JM)
            {
                JourneyMapIntegration.INST.clearData();
            }
        }

        TEAMS.putAll(teamData);
        CHUNKS.putAll(chunkData);

        if(FTBUClient.HAS_JM)
        {
            chunkData.forEach((key, value) -> JourneyMapIntegration.INST.chunkChanged(key, value));
        }
    }
}