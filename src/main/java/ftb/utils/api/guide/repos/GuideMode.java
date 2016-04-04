package ftb.utils.api.guide.repos;

import latmod.lib.util.FinalIDObject;

/**
 * Created by LatvianModder on 03.04.2016.
 */
public class GuideMode extends FinalIDObject
{
	public final IGuide guide;
	public final GuideRepoPage page;
	
	public GuideMode(IGuide g, String id) throws Exception
	{
		super(id);
		guide = g;
		page = new GuideRepoPage(id, g.getFile("guide/" + id + "/pages.json").asJson().getAsJsonObject());
	}
	
	public GuideMode mergeWith(GuideMode mode)
	{
		return null;
	}
}