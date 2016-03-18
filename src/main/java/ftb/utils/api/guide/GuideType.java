package ftb.utils.api.guide;

/**
 * Created by LatvianModder on 18.03.2016.
 */
public enum GuideType
{
	MOD,
	MODPACK,
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
			default:
				return CUSTOM;
		}
	}
}