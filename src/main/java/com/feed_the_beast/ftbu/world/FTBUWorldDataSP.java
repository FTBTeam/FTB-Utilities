package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.util.ChunkDimPos;
import com.feed_the_beast.ftbu.badges.Badge;
import com.feed_the_beast.ftbu.badges.BadgeStorage;
import com.feed_the_beast.ftbu.client.FTBUClient;
import com.feed_the_beast.ftbu.net.MessageRequestBadge;
import latmod.lib.net.LMConnection;
import latmod.lib.net.RequestMethod;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by LatvianModder on 23.02.2016.
 */
public class FTBUWorldDataSP extends FTBUWorldData
{
    public static final BadgeStorage globalBadges = new BadgeStorage();
    public static final BadgeStorage localBadges = new BadgeStorage();
    public static Map<ChunkDimPos, ChunkType> chunks;

    public static void reloadGlobalBadges()
    {
        globalBadges.clear();

        Thread thread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    LMConnection connection = new LMConnection(RequestMethod.SIMPLE_GET, "http://pastebin.com/raw/Mu8McdDR");
                    globalBadges.loadBadges(connection.connect().asJson());
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

    public static ChunkType getType(ChunkDimPos pos)
    {
        return (pos != null && chunks != null && chunks.containsKey(pos)) ? chunks.get(pos) : ChunkType.UNLOADED;
    }

    @SideOnly(Side.CLIENT)
    public static void setTypes(Map<ChunkDimPos, ChunkType> types)
    {
        if(chunks == null)
        {
            return;
        }

        chunks.putAll(types);

        if(FTBUClient.journeyMapHandler != null)
        {
            for(Map.Entry<ChunkDimPos, ChunkType> e : types.entrySet())
            {
                FTBUClient.journeyMapHandler.chunkChanged(e.getKey(), e.getValue());
            }
        }
    }

    public static Badge getClientBadge(UUID playerID)
    {
        if(localBadges.badgePlayerMap.containsKey(playerID))
        {
            return localBadges.badgePlayerMap.get(playerID);
        }

        localBadges.badgePlayerMap.put(playerID, null);
        new MessageRequestBadge(playerID).sendToServer();
        return null;
    }

    @Override
    public void onLoaded()
    {
        chunks = new HashMap<>();
    }

    @Override
    public void onClosed()
    {
        chunks = null;
        localBadges.clear();
    }
}
