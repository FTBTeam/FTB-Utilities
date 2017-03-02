package com.feed_the_beast.ftbu.api_impl;

import com.feed_the_beast.ftbl.lib.FinalIDObject;
import com.feed_the_beast.ftbl.lib.LangKey;
import com.feed_the_beast.ftbu.api.IFTBUtilitiesRegistry;
import com.feed_the_beast.ftbu.api.chunks.IChunkUpgrade;

/**
 * Created by LatvianModder on 01.03.2017.
 */
public class ChunkUpgrade extends FinalIDObject implements IChunkUpgrade
{
    public static final IChunkUpgrade LOADED = new ChunkUpgrade("loaded", 0);
    public static final IChunkUpgrade NO_EXPLOSIONS = new ChunkUpgrade("no_explosions", 1);
    public static final IChunkUpgrade NO_PVP = new ChunkUpgrade("no_pvp", 2);

    // For internal use only upgrades/flags
    public static final IChunkUpgrade ACTUALLY_LOADED = new ChunkUpgrade("actually_loaded", 25);
    public static final IChunkUpgrade FORCED = new ChunkUpgrade("forced", 26);
    public static final IChunkUpgrade IS_OWNER = new ChunkUpgrade("is_owner", 27);
    public static final IChunkUpgrade CAN_CLAIM = new ChunkUpgrade("can_claim", 28);
    public static final IChunkUpgrade CAN_LOAD = new ChunkUpgrade("can_load", 29);
    public static final IChunkUpgrade CLAIMED = new ChunkUpgrade("claimed", 30);
    public static final IChunkUpgrade WILDERNESS = new ChunkUpgrade("wilderness", 31);

    public static void addUpgrades(IFTBUtilitiesRegistry reg)
    {
        reg.addChunkUpgrade(LOADED);
        reg.addChunkUpgrade(NO_EXPLOSIONS);
        reg.addChunkUpgrade(NO_PVP);
    }

    private final LangKey langKey;
    private final int id;

    public ChunkUpgrade(String s, int i)
    {
        super(s);
        langKey = new LangKey("ftbu.lang.chunk." + getName());
        id = i;
    }

    @Override
    public LangKey getLangKey()
    {
        return langKey;
    }

    @Override
    public int getId()
    {
        return id;
    }
}