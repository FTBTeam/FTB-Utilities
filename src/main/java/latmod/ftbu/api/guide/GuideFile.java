package latmod.ftbu.api.guide;

import latmod.lib.FastList;

public class GuideFile
{
	public final FastList<GuideCategory> categories;
	
	public GuideFile()
	{
		categories = new FastList<GuideCategory>();
	}

	public void add(GuideCategory c)
	{
		categories.add(c);
	}
}