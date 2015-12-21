package latmod.ftbu.world.ranks;

import latmod.ftbu.world.LMPlayerServer;

public class Ranks
{
	public static final Rank PLAYER = new Rank("Player");
	public static final Rank ADMIN = new Rank("Admin");
	
	public static Rank getRank(LMPlayerServer p)
	{
		//TODO: Remove me after rank system is done
		return p.isOP() ? ADMIN : PLAYER;
	}
}