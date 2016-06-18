package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.util.ChunkDimPos;
import com.feed_the_beast.ftbu.badges.Badge;
import com.feed_the_beast.ftbu.badges.BadgeStorage;
import com.feed_the_beast.ftbu.client.FTBUClient;
import com.feed_the_beast.ftbu.net.MessageRequestBadge;
import com.latmod.lib.io.LMConnection;
import com.latmod.lib.io.RequestMethod;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

/**
 * Created by LatvianModder on 23.02.2016.
 */
public class FTBUWorldDataSP extends FTBUWorldData
{
    public static final BadgeStorage globalBadges = new BadgeStorage();
    public static final BadgeStorage localBadges = new BadgeStorage();
    public static ClaimedChunkStorage chunks;

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

    @Nullable
    public static ClaimedChunk getChunk(ChunkDimPos pos)
    {
        return (pos != null && chunks != null) ? chunks.getChunk(pos) : null;
    }

    @SideOnly(Side.CLIENT)
    public static void setTypes(Map<ChunkDimPos, ClaimedChunk> types)
    {
        if(chunks == null)
        {
            return;
        }

        for(Map.Entry<ChunkDimPos, ClaimedChunk> e : types.entrySet())
        {
            ChunkDimPos pos = e.getKey();
            ClaimedChunk chunk = e.getValue();

            chunks.put(pos, chunk);

            if(FTBUClient.journeyMapHandler != null)
            {
                FTBUClient.journeyMapHandler.chunkChanged(pos, chunk);
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
        chunks = new ClaimedChunkStorage();
    }

    @Override
    public void onClosed()
    {
        chunks = null;
        localBadges.clear();
    }
}
