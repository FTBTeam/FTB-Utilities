package com.feed_the_beast.ftbutilities.integration.kubejs;

import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftbutilities.events.chunks.ChunkModifiedEvent;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.player.PlayerJS;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.world.WorldJS;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class ChunkModifiedEventJS extends EventJS
{
	public final ChunkModifiedEvent event;

	public ChunkModifiedEventJS(ChunkModifiedEvent e)
	{
		event = e;
	}

	@Nullable
	public ForgePlayer getForgePlayer()
	{
		return event.getPlayer();
	}

	public int getDimension()
	{
		return event.getChunk().getPos().dim;
	}

	public int getX()
	{
		return event.getChunk().getPos().posX;
	}

	public int getZ()
	{
		return event.getChunk().getPos().posZ;
	}

	public ServerJS getServer()
	{
		return ServerJS.instance;
	}

	public WorldJS getWorld()
	{
		return getServer().getWorld(getDimension());
	}

	@Nullable
	public PlayerJS getPlayer()
	{
		return event.getPlayer() == null ? null : getServer().getPlayer(event.getPlayer().getPlayer());
	}
}