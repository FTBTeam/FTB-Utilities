package ftb.utils.api.guide;

/**
 * Created by LatvianModder on 18.03.2016.
 */
public enum GuideType
{
	MOD,
	MODPACK,
	SERVER,
	WORLD,
	CUSTOM;
	
	public static GuideType get(String name)
	{
		switch(name.toLowerCase())
		{
			case "mod":
				return MOD;
			case "modpack":
				return MODPACK;
			case "pack":
				return MODPACK;
			case "server":
				return SERVER;
			case "world":
				return WORLD;
			case "map":
				return WORLD;
			default:
				return CUSTOM;
		}
	}
}