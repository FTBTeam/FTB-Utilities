package ftb.utils.api.guide;

/**
 * Created by LatvianModder on 18.03.2016.
 */
public enum GuideFormat
{
	TXT;
	
	public static GuideFormat get(String name)
	{
		switch(name.toLowerCase())
		{
			case "txt":
				return TXT;
			//case "md":
			//	return MD;
			default:
				return TXT;
		}
	}
}