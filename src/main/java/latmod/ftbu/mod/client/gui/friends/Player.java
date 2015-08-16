package latmod.ftbu.mod.client.gui.friends;

import latmod.ftbu.core.world.LMPlayerClient;

public class Player
{
	public final GuiFriends gui;
	public final LMPlayerClient player;
	public final boolean isOwner;
	
	public Player(GuiFriends g, LMPlayerClient p)
	{
		gui = g;
		player = p;
		isOwner = player.equalsPlayer(gui.owner);
	}
	
	public boolean equals(Object o)
	{
		if(o instanceof Player)
			return player.equalsPlayer(((Player)o).player);
		return player.equals(o);
	}
	
	public boolean isOwner()
	{ return gui.owner.equalsPlayer(player); }
}