package ftb.utils.api.guide.repos;

/**
 * Created by LatvianModder on 03.04.2016.
 */
public enum GuideType
{
	MOD,
	MODPACK,
	WORLD,
	SERVER,
	CUSTOM;
	
	public static GuideType getFromID(String s)
	{
		switch(s.toLowerCase())
		{
			case "mod":
				return MOD;
			case "modpack":
				return MODPACK;
			case "world":
				return WORLD;
			case "server":
				return SERVER;
		}
		
		return CUSTOM;
	}
}
