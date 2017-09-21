package com.feed_the_beast.ftbu.api_impl;

import com.feed_the_beast.ftbl.api.EventHandler;
import com.feed_the_beast.ftbl.lib.FinalIDObject;
import com.feed_the_beast.ftbl.lib.LangKey;
import com.feed_the_beast.ftbu.api.chunks.IChunkUpgrade;
import com.feed_the_beast.ftbu.api.events.registry.RegisterChunkUpgradesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author LatvianModder
 */
@EventHandler
public class ChunkUpgrade extends FinalIDObject implements IChunkUpgrade
{
	public static final IChunkUpgrade LOADED = new ChunkUpgrade("loaded", false);
	public static final IChunkUpgrade NO_EXPLOSIONS = new ChunkUpgrade("no_explosions", true); //TODO: Make real
	public static final IChunkUpgrade NO_PVP = new ChunkUpgrade("no_pvp", true); //TODO: Make real

	public static final IChunkUpgrade CAN_CLAIM = new ChunkUpgrade("can_claim", true);
	public static final IChunkUpgrade CLAIMED = new ChunkUpgrade("claimed", true);
	public static final IChunkUpgrade WILDERNESS = new ChunkUpgrade("wilderness", true);

	@SubscribeEvent
	public static void addUpgrades(RegisterChunkUpgradesEvent event)
	{
		event.register(LOADED);
		event.register(NO_EXPLOSIONS);
		event.register(NO_PVP);

		event.register(CAN_CLAIM);
		event.register(CLAIMED);
		event.register(WILDERNESS);
	}

	private final LangKey langKey;
	private final boolean internal;

	public ChunkUpgrade(String s, boolean i)
	{
		super(s);
		internal = i;
		langKey = LangKey.of("ftbu.lang.chunks.upgrade." + getName());
	}

	@Override
	public LangKey getLangKey()
	{
		return langKey;
	}

	@Override
	public boolean isInternal()
	{
		return internal;
	}
}