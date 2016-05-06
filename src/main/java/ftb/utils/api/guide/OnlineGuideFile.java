package ftb.utils.api.guide;

import latmod.lib.util.FinalIDObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LatvianModder on 06.05.2016.
 */
public class OnlineGuideFile extends FinalIDObject
{
	public final OnlineGuideInfo info;
	public final Map<String, OnlineGuideImage> images;
	
	public OnlineGuideFile(OnlineGuideInfo i)
	{
		super(i.getID());
		info = i;
		
		Map<String, OnlineGuideImage> images0 = new HashMap<>();
		
		
		images = Collections.unmodifiableMap(images0);
	}
}