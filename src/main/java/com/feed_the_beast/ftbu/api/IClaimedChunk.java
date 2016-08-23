package com.feed_the_beast.ftbu.api;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.latmod.lib.LangKey;
import com.latmod.lib.math.ChunkDimPos;
import net.minecraft.util.math.BlockPos;

/**
 * Created by LatvianModder on 23.08.2016.
 */
public interface IClaimedChunk
{
    LangKey LANG_WILDERNESS = new LangKey("ftbu.chunktype.wilderness");
    LangKey LANG_CLAIMED = new LangKey("ftbu.chunktype.claimed");
    LangKey LANG_LOADED = new LangKey("ftbu.chunktype.loaded");

    ChunkDimPos getPos();

    IForgePlayer getOwner();

    boolean isLoaded();

    void setLoaded(boolean flag);

    boolean isForced();

    void setForced(boolean flag);

    boolean isChunkOwner(IForgePlayer p);

    boolean canInteract(IForgePlayer p, boolean leftClick, BlockPos pos);
}