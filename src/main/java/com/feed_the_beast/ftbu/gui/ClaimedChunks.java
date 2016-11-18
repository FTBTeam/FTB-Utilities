package com.feed_the_beast.ftbu.gui;

import java.util.UUID;

/**
 * Created by LatvianModder on 29.09.2016.
 */
public class ClaimedChunks
{
    public static final byte DATA_IS_CLAIMED = 1;
    public static final byte DATA_OWNER = 2;
    public static final byte DATA_LOADED = 4;
    public static final byte DATA_CAN_CLAIM = 8;
    public static final byte DATA_CAN_LOAD = 16;

    public static class Team
    {
        public UUID ownerID;
        public byte colorID;
        public String formattedName;
        public boolean isAlly;
    }

    public static class Data
    {
        public byte flags;
        public Team team;
        public String owner;

        public boolean isClaimed()
        {
            return (flags & DATA_IS_CLAIMED) != 0;
        }

        public boolean isOwner()
        {
            return (flags & DATA_OWNER) != 0;
        }

        public boolean isLoaded()
        {
            return (flags & DATA_LOADED) != 0;
        }

        public boolean canClaim()
        {
            return (flags & DATA_CAN_CLAIM) != 0;
        }

        public boolean canLoad()
        {
            return (flags & DATA_CAN_LOAD) != 0;
        }
    }
}
