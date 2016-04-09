package ftb.utils.api.guide.repos;

import ftb.lib.api.info.InfoPage;
import latmod.lib.util.FinalIDObject;

/**
 * Created by LatvianModder on 03.04.2016.
 */
public class GuideMode extends FinalIDObject
{
	public final GuideRepo guide;
	public final GuideRepoPage page;
	private InfoPage infoPage;
	
	public GuideMode(GuideRepo g, String id) throws Exception
	{
		super(id);
		guide = g;
		page = new GuideRepoPage(null, id, g.getFile("guide/" + id + "/pages.json").asJson().getAsJsonObject());
	}
}