package com.feed_the_beast.ftbu.badges;

import com.feed_the_beast.ftbu.net.MessageRequestBadge;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by LatvianModder on 07.01.2016.
 */
public class ClientBadges
{
    private static final Map<String, Badge> map = new HashMap<>();
    private static final Map<UUID, Badge> playerBadges = new HashMap<>();
    
    public static void clear()
    {
        map.clear();
        playerBadges.clear();
    }
    
    public static Badge getClientBadge(UUID playerID)
    {
        Badge b = playerBadges.get(playerID);
        if(b == null)
        {
            b = Badge.emptyBadge;
            playerBadges.put(playerID, b);
            new MessageRequestBadge(playerID).sendToServer();
        }
        
        return b;
    }
    
    public static void addBadge(Badge b)
    {
        if(b != null && !b.equals(Badge.emptyBadge))
        {
            map.put(b.getID(), b);
        }
    }
    
    public static void setClientBadge(UUID playerID, String badge)
    {
        if(playerID != null && badge != null && !badge.isEmpty() && !badge.equalsIgnoreCase(Badge.emptyBadge.getID()))
        {
            Badge b = map.get(badge);
            playerBadges.put(playerID, (b != null) ? b : Badge.emptyBadge);
        }
    }
}