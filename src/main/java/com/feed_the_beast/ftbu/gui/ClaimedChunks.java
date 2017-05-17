package com.feed_the_beast.ftbu.gui;

import com.feed_the_beast.ftbl.api.EnumTeamColor;
import com.feed_the_beast.ftbl.lib.io.Bits;
import com.feed_the_beast.ftbu.api.chunks.IChunkUpgrade;

import java.util.UUID;

/**
 * @author LatvianModder
 */
public class ClaimedChunks
{
    public static class Team
    {
        public UUID ownerId;
        public EnumTeamColor color;
        public String formattedName;
        public boolean isAlly;
    }

    public static class Data
    {
        public int flags;
        public Team team;
        public String owner = "";

        public boolean hasUpgrade(IChunkUpgrade upgrade)
        {
            return Bits.getFlag(flags, 1 << upgrade.getId());
        }

        public void setHasUpgrade(IChunkUpgrade upgrade, boolean val)
        {
            flags = Bits.setFlag(flags, 1 << upgrade.getId(), val);
        }
    }
}