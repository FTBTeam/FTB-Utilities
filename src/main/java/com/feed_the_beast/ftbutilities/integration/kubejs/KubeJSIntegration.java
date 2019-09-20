package com.feed_the_beast.ftbutilities.integration.kubejs;


import com.feed_the_beast.ftbutilities.events.chunks.ChunkModifiedEvent;
import dev.latvian.kubejs.documentation.DocumentationEvent;
import dev.latvian.kubejs.event.EventsJS;
import dev.latvian.kubejs.player.AttachPlayerDataEvent;
import dev.latvian.kubejs.script.BindingsEvent;
import dev.latvian.kubejs.script.DataType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author LatvianModder
 */
public class KubeJSIntegration
{
	public static void init()
	{
		MinecraftForge.EVENT_BUS.register(KubeJSIntegration.class);
	}

	@SubscribeEvent
	public static void registerBindings(BindingsEvent event)
	{
		event.add("ftbutilities", new KubeJSFTBUtilitiesWrapper());
	}

	@SubscribeEvent
	public static void attachPlayerData(AttachPlayerDataEvent event)
	{
		event.add("ftbutilities", new KubeJSFTBUtilitiesPlayerData(event.getParent()));
	}

	@SubscribeEvent
	public static void registerDocumentation(DocumentationEvent event)
	{
		event.registerAttachedData(DataType.PLAYER, "ftbutilities", KubeJSFTBUtilitiesPlayerData.class);
		event.registerEvent("ftbutilities.chunk.claimed", ChunkModifiedEventJS.class).serverOnly();
		event.registerEvent("ftbutilities.chunk.unclaimed", ChunkModifiedEventJS.class).serverOnly();
		event.registerEvent("ftbutilities.chunk.loaded", ChunkModifiedEventJS.class).serverOnly();
		event.registerEvent("ftbutilities.chunk.unloaded", ChunkModifiedEventJS.class).serverOnly();
	}

	@SubscribeEvent
	public static void onChunkClaimed(ChunkModifiedEvent.Claimed event)
	{
		EventsJS.post("ftbutilities.chunk.claimed", new ChunkModifiedEventJS(event));
	}

	@SubscribeEvent
	public static void onChunkUnclaimed(ChunkModifiedEvent.Unclaimed event)
	{
		EventsJS.post("ftbutilities.chunk.unclaimed", new ChunkModifiedEventJS(event));
	}

	@SubscribeEvent
	public static void onChunkLoaded(ChunkModifiedEvent.Loaded event)
	{
		EventsJS.post("ftbutilities.chunk.loaded", new ChunkModifiedEventJS(event));
	}

	@SubscribeEvent
	public static void onChunkUnloaded(ChunkModifiedEvent.Unloaded event)
	{
		EventsJS.post("ftbutilities.chunk.unloaded", new ChunkModifiedEventJS(event));
	}
}