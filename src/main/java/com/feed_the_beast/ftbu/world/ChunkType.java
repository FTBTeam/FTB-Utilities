package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.api.ForgePlayer;
import com.feed_the_beast.ftbl.api.ForgePlayerMP;
import com.feed_the_beast.ftbl.api.ForgePlayerSP;
import com.feed_the_beast.ftbl.api.ForgeWorldSP;
import com.feed_the_beast.ftbl.api.LangKey;
import com.feed_the_beast.ftbl.api.net.MessageLM;
import com.feed_the_beast.ftbl.util.ChunkDimPos;
import com.feed_the_beast.ftbl.util.PrivacyLevel;
import com.feed_the_beast.ftbu.FTBUPermissions;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.UUID;

public class ChunkType
{
    public static final ChunkType UNLOADED = new ChunkType(0, "unloaded", TextFormatting.DARK_GRAY, 0xFF000000);
    public static final ChunkType SPAWN = new ChunkType(1, "spawn", TextFormatting.AQUA, 0xFF00EFDF);
    public static final ChunkType WILDERNESS = new ChunkType(2, "wilderness", TextFormatting.DARK_GREEN, 0xFF2F9E00);
    public static final ChunkType WORLD_BORDER = new ChunkType(3, "world_border", TextFormatting.RED, 0xFFFF0000);

    public static final ChunkType[] UNCLAIMED_VALUES = new ChunkType[] {UNLOADED, SPAWN, WILDERNESS, WORLD_BORDER};

    public static final class PlayerClaimed extends ChunkType
    {
        public final ClaimedChunk chunk;

        public PlayerClaimed(ClaimedChunk o)
        {
            super(4, "claimed", null, 0);
            chunk = o;
        }

        @Override
        public PlayerClaimed asClaimed()
        { return this; }

        @Override
        public boolean isChunkOwner(ForgePlayerMP p)
        { return chunk.getOwner().equalsPlayer(p); }

        @Override
        public TextFormatting getChatColor(ForgePlayer p)
        { return p.getWorld().getPlayer(chunk.ownerID).isFriend(p) ? TextFormatting.GREEN : TextFormatting.BLUE; }

        @Override
        public int getAreaColor(ForgePlayer p)
        { return p.getWorld().getPlayer(chunk.ownerID).isFriend(p) ? 0xFF00FF21 : 0xFF0094FF; }

        @Override
        public boolean canInteract(ForgePlayerMP p, boolean leftClick)
        {
            ForgePlayerMP chunkOwner = chunk.getOwner();

            if(chunkOwner.equals(p)) { return true; }
            else if(p.isFake()) { return FTBUPlayerDataMP.get(chunkOwner).getFlag(FTBUPlayerData.FAKE_PLAYERS); }

            PrivacyLevel level = FTBUPermissions.claims_forced_security.getEnum(p.getProfile());
            if(level == null) { level = FTBUPlayerDataMP.get(chunkOwner).blocks; }
            return level.canInteract(chunkOwner, p);
        }

        @Override
        public void write(ByteBuf io)
        {
            super.write(io);
            MessageLM.writeUUID(io, chunk.ownerID);
            io.writeBoolean(chunk.isChunkloaded);
        }

        @Override
        public boolean equals(Object o)
        {
            return super.equals(o) && ((PlayerClaimed) o).chunk.ownerID.equals(chunk.ownerID);
        }

        @Override
        public ITextComponent getTitleComponent()
        { return new TextComponentString(String.valueOf(chunk.getOwner())); }

        @Override
        @SideOnly(Side.CLIENT)
        public void getMessage(List<String> l, boolean shift)
        {
            ForgePlayerSP owner = ForgeWorldSP.inst.getPlayer(chunk.ownerID);
            if(owner != null)
            {
                l.add(getChatColor(owner) + owner.getProfile().getName());
                if(chunk.isChunkloaded) { l.add(I18n.format("ftbu.chunktype.chunkloaded")); }
            }
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

    public static ChunkType read(ChunkDimPos pos, ByteBuf io)
    {
        byte id = io.readByte();

        if(id >= 0 && id <= UNCLAIMED_VALUES.length) { return UNCLAIMED_VALUES[id]; }
        else if(id == 99)
        {
            UUID owner = MessageLM.readUUID(io);
            ClaimedChunk chunk = new ClaimedChunk(owner, pos);
            chunk.isChunkloaded = io.readBoolean();
            return new PlayerClaimed(chunk);
        }

        return UNLOADED;
    }

    public void write(ByteBuf io)
    {
        io.writeByte(ID);
    }

    @SideOnly(Side.CLIENT)
    public void getMessage(List<String> l, boolean shift)
    {
        if(this != ChunkType.UNLOADED)
        {
            l.add(getChatColor(null) + langKey.translate());
        }
    }

    public ITextComponent getTitleComponent()
    { return langKey.textComponent(); }

    public PlayerClaimed asClaimed()
    { return null; }

    public boolean isChunkOwner(ForgePlayerMP p)
    { return this == WILDERNESS; }

    public boolean drawGrid()
    { return this != WILDERNESS && this != UNLOADED; }

    @Override
    public int hashCode()
    { return ID; }

    @Override
    public boolean equals(Object o)
    { return o == this || ID == o.hashCode(); }

    public TextFormatting getChatColor(ForgePlayer p)
    { return chatColor; }

    public int getAreaColor(ForgePlayer p)
    { return areaColor; }

    public boolean canInteract(ForgePlayerMP p, boolean leftClick)
    { return this == WILDERNESS || this == SPAWN; }
}