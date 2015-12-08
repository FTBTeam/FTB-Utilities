package latmod.ftbu.mod.config;

import latmod.ftbu.world.claims.ChunkloaderType;
import latmod.lib.config.*;
import latmod.lib.util.IntBounds;

public class FTBUConfigChunkloading
{
	public static final ConfigGroup group = new ConfigGroup("chunkloading");
	public static final ConfigEntryEnum<ChunkloaderType> type = new ConfigEntryEnum<ChunkloaderType>("type", ChunkloaderType.class, ChunkloaderType.values(), ChunkloaderType.DISABLED, false).sync().setInfo("disabled - Players won't be able to chunkload\nnormal - Chunks stay loaded when player loggs off\nplayers - Chunks only stay loaded while owner is online");
	public static final ConfigEntryInt max_loaded_chunks_player = new ConfigEntryInt("max_loaded_chunks_player", new IntBounds(64, 0, Integer.MAX_VALUE)).setInfo("Max amount of chunks that player can chunkload.\n0 - Disabled");
	public static final ConfigEntryInt max_loaded_chunks_admin = new ConfigEntryInt("max_loaded_chunks_admin", new IntBounds(1000, 0, Integer.MAX_VALUE)).setInfo("Max amount of chunks that admin can chunkload.\n0 - Disabled");
}