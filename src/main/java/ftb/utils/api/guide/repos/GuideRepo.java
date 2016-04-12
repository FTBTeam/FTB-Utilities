package ftb.utils.api.guide.repos;

import ftb.lib.api.info.InfoPage;
import latmod.lib.net.Response;
import latmod.lib.util.FinalIDObject;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

/**
 * Created by LatvianModder on 08.04.2016.
 */
public abstract class GuideRepo extends FinalIDObject
{
	public GuideRepo(String id)
	{
		super(id);
	}
	
	public abstract GuideInfo getInfo();
	public abstract Map<String, GuideMode> getModes();
	public abstract Response getFile(String path) throws Exception;
	public abstract ResourceLocation getIcon();
	public abstract boolean isLocal();
	
	public String toString()
	{ return getInfo().name; }
	
	public InfoPage getInfoPage(String id)
	{
		InfoPage page = new InfoPage(getID());
		InfoPage page1;
		
		GuideMode mode = getModes().get("common");
		if(mode != null)
		{
			page1 = mode.getInfoPage();
			page.copyFrom(page1);
		}
		
		mode = getModes().get(id);
		
		if(mode != null)
		{
			page1 = mode.getInfoPage();
			page.copyFrom(page1);
		}
		
		return page;
	}
}