package latmod.ftbu.world.ranks;

import latmod.ftbu.world.LMPlayerServer;

public class Ranks
{
	private static final Rank DEFAULT = new Rank("default");
	
	public static Rank getRank(LMPlayerServer p)
	{
		//TODO: Remove me after rank system is done
		return DEFAULT;
	}
}