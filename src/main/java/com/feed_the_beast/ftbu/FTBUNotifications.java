package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbl.api.notification.NotificationID;
import net.minecraft.util.ResourceLocation;

/**
 * Created by LatvianModder on 31.07.2016.
 */
public class FTBUNotifications
{
    public static final int NO_TEAM = get("no_team");
    public static final int MODIFY_CHUNK = get("modify_chunk");
    public static final int UNCLAIMED_ALL = get("unclaimed_all");
    public static final int CHUNK_CHANGED = get("chunk_changed");

    private static int get(String id)
    {
        return NotificationID.get(new ResourceLocation(FTBUFinals.MOD_ID, id));
    }
}
