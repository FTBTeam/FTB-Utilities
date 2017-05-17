package com.feed_the_beast.ftbu.api;

import com.feed_the_beast.ftbl.api.config.IConfigValue;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunkStorage;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Collection;

/**
 * @author LatvianModder
 */
public interface FTBUtilitiesAPI
{
    Collection<IFTBUtilitiesPlugin> getAllPlugins();

    IClaimedChunkStorage getClaimedChunks();

    IRank getRank(GameProfile profile);

    IConfigValue getRankConfig(GameProfile profile, String id);

    default IConfigValue getRankConfig(EntityPlayer player, String id)
    {
        return getRankConfig(player.getGameProfile(), id);
    }
}