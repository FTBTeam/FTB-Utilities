package ftb.utils.badges;

import ftb.utils.net.MessageRequestBadge;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by LatvianModder on 07.01.2016.
 */
public class ClientBadges
{
	private static final HashMap<String, Badge> map = new HashMap<>();
	private static final HashMap<UUID, Badge> playerBadges = new HashMap<>();
	
	public static void clear()
	{
		map.clear();
		playerBadges.clear();
	}
	
	public static Badge getClientBadge(UUID playerID)
	{
		Badge b = playerBadges.get(playerID);
		if(b == null)
		{
			b = Badge.emptyBadge;
			playerBadges.put(playerID, b);
			new MessageRequestBadge(playerID).sendToServer();
		}
		
		return b;
	}
	
	public static void addBadge(Badge b)
	{ if(b != null && !b.equals(Badge.emptyBadge)) { map.put(b.getID(), b); } }
	
	public static void setClientBadge(UUID playerID, String badge)
	{
		if(playerID == null || badge == null || badge.isEmpty() || badge.equalsIgnoreCase(Badge.emptyBadge.getID()))
		{
			return;
		}
		
		Badge b = map.get(badge);
		if(b != null) { playerBadges.put(playerID, b); }
		else { playerBadges.put(playerID, Badge.emptyBadge); }
	}
}