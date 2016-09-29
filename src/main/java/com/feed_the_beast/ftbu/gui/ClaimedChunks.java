package com.feed_the_beast.ftbu.gui;

import com.feed_the_beast.ftbl.lib.client.TextureCoords;
import com.feed_the_beast.ftbu.FTBUFinals;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

/**
 * Created by LatvianModder on 29.09.2016.
 */
public class ClaimedChunks
{
    public static final int TILES_TEX = 16;
    public static final int TILES_GUI = 15;
    public static final int TILES_GUI_HALF = TILES_GUI / 2;
    public static final double UV = (double) ClaimedChunks.TILES_GUI / (double) ClaimedChunks.TILES_TEX;

    public static final ResourceLocation TEX_ENTITY = new ResourceLocation(FTBUFinals.MOD_ID, "textures/gui/entity.png");
    public static final ResourceLocation TEX_CHUNK_CLAIMING = new ResourceLocation(FTBUFinals.MOD_ID, "textures/gui/chunk_claiming.png");
    public static final TextureCoords TEX_FILLED = TextureCoords.fromUV(TEX_CHUNK_CLAIMING, 0D, 0D, 0.5D, 1D);
    public static final TextureCoords TEX_BORDER = TextureCoords.fromUV(TEX_CHUNK_CLAIMING, 0.5D, 0D, 1D, 1D);

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
