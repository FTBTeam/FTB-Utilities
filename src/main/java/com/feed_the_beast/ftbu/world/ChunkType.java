package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.api.ForgePlayer;
import com.feed_the_beast.ftbl.api.ForgePlayerMP;
import com.feed_the_beast.ftbl.api.LangKey;
import com.feed_the_beast.ftbl.util.ChunkDimPos;
import com.feed_the_beast.ftbl.util.PrivacyLevel;
import com.feed_the_beast.ftbu.FTBUPermissions;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.UUID;

public class ChunkType
{
    public static final ChunkType UNLOADED = new ChunkType(0, "unloaded", TextFormatting.DARK_GRAY, 0x000000);
    public static final ChunkType SPAWN = new ChunkType(1, "spawn", TextFormatting.AQUA, 0x00EFDF);
    public static final ChunkType WILDERNESS = new ChunkType(2, "wilderness", TextFormatting.DARK_GREEN, 0x2F9E00);
    public static final ChunkType WORLD_BORDER = new ChunkType(3, "world_border", TextFormatting.RED, 0xFF0000);

    public static final ChunkType[] VALUES = new ChunkType[] {UNLOADED, SPAWN, WILDERNESS, WORLD_BORDER};

    public static final class PlayerClaimed extends ChunkType
    {
        public ClaimedChunk chunk;

        public PlayerClaimed()
        {
            super(99, "claimed", TextFormatting.DARK_GRAY, 0xFF000000);
        }

        @Override
        public PlayerClaimed asClaimed()
        {
            return this;
        }

        public boolean isFriendly(ForgePlayer p)
        {
            return p != null && (isChunkOwner(p) || p.getWorld().getPlayer(chunk.ownerID).isFriend(p));
        }

        @Override
        public boolean isChunkOwner(ForgePlayer p)
        {
            return p != null && p.equalsPlayer(chunk.getOwner());
        }

        @Override
        public TextFormatting getChatColor(ForgePlayer p)
        {
            return isFriendly(p) ? TextFormatting.GREEN : TextFormatting.BLUE;
        }

        @Override
        public int getAreaColor(ForgePlayer p)
        {
            return isFriendly(p) ? (chunk.getFlag(ClaimedChunk.CHUNKLOADED) ? 0xFFBE00 : 0x00FF21) : 0x0094FF;
        }

        @Override
        public boolean canInteract(ForgePlayerMP p, boolean leftClick)
        {
            ForgePlayerMP chunkOwner = chunk.getOwner();

            if(chunkOwner.equals(p))
            {
                return true;
            }
            else if(p.isFake())
            {
                return FTBUPlayerData.get(chunkOwner).getFlag(FTBUPlayerData.FAKE_PLAYERS);
            }

            PrivacyLevel level = FTBUPermissions.claims_forced_security.getEnum(p.getProfile());
            if(level == null)
            {
                level = FTBUPlayerData.get(chunkOwner).blocks;
            }

            return level.canInteract(chunkOwner, p);
        }

        @Override
        public boolean equals(Object o)
        {
            return super.equals(o) && ((PlayerClaimed) o).chunk.ownerID.equals(chunk.ownerID);
        }

        @Override
        public ITextComponent getTitleComponent()
        {
            return new TextComponentString(String.valueOf(chunk.getOwner()));
        }

        @Override
        public void writeToNBT(NBTTagCompound tag, ForgePlayerMP to)
        {
            tag.setLong("OM", chunk.ownerID.getMostSignificantBits());
            tag.setLong("OL", chunk.ownerID.getLeastSignificantBits());

            if(chunk.getFlag(ClaimedChunk.CHUNKLOADED) && isFriendly(to))
            {
                tag.setBoolean("L", true);
            }
        }

        @Override
        public void readFromNBT(NBTTagCompound tag, ChunkDimPos pos)
        {
            chunk = new ClaimedChunk(new UUID(tag.getLong("OM"), tag.getLong("OL")), pos);
            chunk.setFlag(ClaimedChunk.CHUNKLOADED, tag.getBoolean("L"));
        }
    }

    public final int ID;
    public final LangKey langKey;
    private final TextFormatting chatColor;
    private final int areaColor;

    public ChunkType(int id, String s, TextFormatting c, int col)
    {
        ID = id;
        langKey = new LangKey("ftbu.chunktype." + s);
        chatColor = c;
        areaColor = col;
    }

    public ITextComponent getTitleComponent()
    {
        return langKey.textComponent();
    }

    public PlayerClaimed asClaimed()
    {
        return null;
    }

    public boolean isChunkOwner(ForgePlayer p)
    {
        return this == WILDERNESS;
    }

    public boolean drawGrid()
    {
        return this != WILDERNESS && this != UNLOADED;
    }

    @Override
    public int hashCode()
    {
        return ID;
    }

    @Override
    public boolean equals(Object o)
    {
        return o == this || ID == o.hashCode();
    }

    public TextFormatting getChatColor(ForgePlayer p)
    {
        return chatColor;
    }

    public int getAreaColor(ForgePlayer p)
    {
        return areaColor;
    }

    public boolean canInteract(ForgePlayerMP p, boolean leftClick)
    {
        return this == WILDERNESS || this == SPAWN;
    }

    public void writeToNBT(NBTTagCompound tag, ForgePlayerMP to)
    {
    }

    public void readFromNBT(NBTTagCompound tag, ChunkDimPos pos)
    {
    }
}