package com.feed_the_beast.ftbu.integration;

import com.feed_the_beast.ftbu.gui.ClientClaimedChunks;
import net.minecraft.util.math.ChunkPos;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public interface IJMIntegration
{
	void clearData();

	void chunkChanged(ChunkPos pos, @Nullable ClientClaimedChunks.ChunkData chunk);
}