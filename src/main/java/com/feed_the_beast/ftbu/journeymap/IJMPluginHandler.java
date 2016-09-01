package com.feed_the_beast.ftbu.journeymap;

import com.feed_the_beast.ftbu.client.CachedClientData;
import com.latmod.lib.math.ChunkDimPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * Created by LatvianModder on 07.02.2016.
 */
@SideOnly(Side.CLIENT)
public interface IJMPluginHandler
{
    void mappingStarted();

    void mappingStopped();

    void chunkChanged(ChunkDimPos pos, @Nullable CachedClientData.ChunkData chunk);
}
