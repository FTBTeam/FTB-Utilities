package ftb.utils.api.guide.repos;

/**
 * Created by LatvianModder on 03.04.2016.
 */
public enum GuideFormat
{
	TXT,
	MD;
	
	public static GuideFormat getFromID(String s)
	{
		switch(s.toLowerCase())
		{
			case "md":
				return MD;
		}
		
		return TXT;
	}
}
