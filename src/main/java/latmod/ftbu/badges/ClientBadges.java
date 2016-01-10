package latmod.ftbu.badges;

import latmod.ftbu.net.ClientAction;

import java.util.HashMap;

/**
 * Created by LatvianModder on 07.01.2016.
 */
public class ClientBadges
{
	private static final HashMap<String, Badge> map = new HashMap<>();
	private static final HashMap<Integer, Badge> playerBadges = new HashMap<>();
	
	public static void clear()
	{
		map.clear();
		playerBadges.clear();
	}
	
	public static Badge getClientBadge(int playerID)
	{
		Badge b = playerBadges.get(playerID);
		if(b == null)
		{
			b = Badge.emptyBadge;
			playerBadges.put(playerID, b);
			ClientAction.REQUEST_BADGE.send(playerID);
		}
		
		return b;
	}
	
	public static void addBadge(Badge b)
	{
		if(b != null && !b.equals(Badge.emptyBadge)) map.put(b.ID, b);
	}
	
	public static void setClientBadge(int playerID, String badge)
	{
		Badge b = map.get(badge);
		if(b != null) playerBadges.put(playerID, b);
	}
}