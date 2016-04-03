package ftb.utils.api.guide.repos;

import latmod.lib.net.Response;

import java.util.Map;

/**
 * Created by LatvianModder on 03.04.2016.
 */
public interface IGuide
{
	GuideInfo getInfo();
	Map<String, GuideMode> getModes();
	Response getFile(String path) throws Exception;
}
