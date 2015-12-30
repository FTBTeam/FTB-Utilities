package latmod.ftbu.world.ranks;

import ftb.lib.FTBLib;
import latmod.ftbu.mod.*;
import latmod.ftbu.mod.config.FTBUConfigGeneral;
import latmod.ftbu.world.*;
import latmod.lib.FastMap;
import latmod.lib.config.*;

import java.io.File;
import java.util.UUID;

public class Ranks
{
	public static final Rank PLAYER = new Rank("Player");
	public static final Rank ADMIN = new Rank("Admin");

	private static ConfigFile file;
	private static Rank defaultRank;
	public static final FastMap<String, Rank> ranks = new FastMap<>();
	public static final FastMap<UUID, Rank> playerMap = new FastMap<>();

	public static Rank getRank(LMPlayerServer p)
	{
		if(FTBUConfigGeneral.ranks_enabled.get())
		{
			if(p == null || p.isFake()) return defaultRank;
			Rank r = playerMap.get(p.getUUID());
			return (r == null) ? defaultRank : r;
		}

		if(p == null || p.isFake()) return PLAYER;
		return p.isOP() ? ADMIN : PLAYER;
	}

	public static void reload()
	{
		ranks.clear();
		playerMap.clear();

		if(file == null) file = new ConfigFile("ranks", new File(FTBLib.folderLocal, "ftbu/ranks.json"));

		ConfigEntryString default_rank = new ConfigEntryString("default_rank", "Player");
		ConfigGroup ranksGroup = new ConfigGroup("ranks");
		file.add(default_rank);
		file.add(ranksGroup);
		file.load();

		if(ranksGroup.entryMap.isEmpty())
		{
			ConfigGroup def_player = new ConfigGroup("Player");
			def_player.addAll(Rank.class, PLAYER, true);
		}

		for(Rank r : ranks.values())
		{
			r.parentRank = r.parent.get().isEmpty() ? null : ranks.get(r.parent);
		}

		saveRanks();
	}

	public static void saveRanks()
	{

	}
}