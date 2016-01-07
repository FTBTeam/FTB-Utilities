package latmod.ftbu.badges;

import java.util.HashMap;

/**
 * Created by LatvianModder on 07.01.2016.
 */
public class ClientBadges
{
	public static final HashMap<String, Badge> loadedBadges = new HashMap<>();
	public static final HashMap<Integer, Badge> playerBadges = new HashMap<>();

	public static void clear()
	{
		loadedBadges.clear();
		playerBadges.clear();
	}
}