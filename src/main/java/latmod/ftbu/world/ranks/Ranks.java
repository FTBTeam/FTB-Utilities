package latmod.ftbu.world.ranks;

import latmod.ftbu.world.LMPlayerServer;

public class Ranks
{
	private static final Rank DEFAULT = new Rank("-");
	
	public static Rank getRank(LMPlayerServer p)
	{
		//TODO: Remove me after rank system is done
		RankConfig.currentPlayer = p;
		return DEFAULT;
	}
}