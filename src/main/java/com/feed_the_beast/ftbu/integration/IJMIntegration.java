package com.feed_the_beast.ftbu.integration;

import com.feed_the_beast.ftbu.gui.ClaimedChunks;
import net.minecraft.util.math.ChunkPos;

/**
 * Created by LatvianModder on 02.03.2017.
 */
public interface IJMIntegration
{
    void clearData();

    void chunkChanged(ChunkPos pos, ClaimedChunks.Data chunk);
}