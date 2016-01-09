package latmod.ftbu.world.ranks;

import latmod.ftbu.world.LMPlayerServer;
import latmod.lib.config.ConfigFile;

import java.util.*;

public class Ranks
{
	public static final Rank PLAYER = new Rank("Player");
	public static final Rank ADMIN = new Rank("Admin");

	private static ConfigFile file;
	private static Rank defaultRank;
	public static final HashMap<String, Rank> ranks = new HashMap<>();
	public static final HashMap<UUID, Rank> playerMap = new HashMap<>();

	public static Rank getRank(LMPlayerServer p)
	{
		/*if(FTBUConfigGeneral.ranks_enabled.get())
		{
			if(p == null || p.isFake()) return defaultRank;
			Rank r = playerMap.get(p.getUUID());
			return (r == null) ? defaultRank : r;
		}
		else
		{*/
		if(p == null || p.isFake()) return PLAYER;
		return p.isOP() ? ADMIN : PLAYER;
		//}
	}

	public static void reload()
	{
		ranks.clear();
		playerMap.clear();

		/*

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
		*/
	}

	public static void saveRanks()
	{

	}
}