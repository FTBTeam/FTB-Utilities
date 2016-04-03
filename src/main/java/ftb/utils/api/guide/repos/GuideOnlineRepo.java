package ftb.utils.api.guide.repos;

import ftb.lib.FTBLib;
import latmod.lib.LMFileUtils;
import latmod.lib.github.GitHubAPI;
import latmod.lib.net.*;
import latmod.lib.util.FinalIDObject;

import java.io.File;
import java.util.*;

/**
 * Created by LatvianModder on 03.04.2016.
 */
public class GuideOnlineRepo extends FinalIDObject implements IGuide
{
	private final GuideInfo info;
	private Map<String, GuideMode> modes;
	
	public GuideOnlineRepo(String id) throws Exception
	{
		super(id);
		info = new GuideInfo(getFile("guide.json").asJson().getAsJsonObject());
	}
	
	public void download() throws Exception
	{
		File file = new File(FTBLib.folderLocal, "guidepacks/" + info.file_name);
		if(file.exists()) LMFileUtils.delete(file);
		file.mkdirs();
		
		//TODO: Finish me
	}
	
	public GuideInfo getInfo()
	{ return info; }
	
	public Map<String, GuideMode> getModes()
	{
		if(modes == null)
		{
			Map<String, GuideMode> map = new HashMap<>();
			
			for(String s : info.modes)
			{
				try
				{
					map.put(s, new GuideMode(this, s));
				}
				catch(Exception ex)
				{
					//ex.printStackTrace();
				}
			}
			
			modes = Collections.unmodifiableMap(map);
		}
		
		return modes;
	}
	
	public Response getFile(String path) throws Exception
	{ return new LMURLConnection(RequestMethod.SIMPLE_GET, GitHubAPI.RAW_CONTENT + getID() + '/' + path).connect(); }
	
	public String toString()
	{ return info.name; }
}