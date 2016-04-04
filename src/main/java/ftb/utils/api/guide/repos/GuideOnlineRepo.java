package ftb.utils.api.guide.repos;

import ftb.lib.*;
import ftb.lib.api.client.FTBLibClient;
import latmod.lib.LMFileUtils;
import latmod.lib.github.GitHubAPI;
import latmod.lib.net.*;
import latmod.lib.util.FinalIDObject;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.util.*;

/**
 * Created by LatvianModder on 03.04.2016.
 */
public class GuideOnlineRepo extends FinalIDObject implements IGuide
{
	private final GuideInfo info;
	private Map<String, GuideMode> modes;
	private TextureCoords icon;
	
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
	
	public GuideMode getMergedMode(String id)
	{
		GuideMode mode = getModes().get("default");
		if(mode == null) return getModes().get(id);
		else return mode.mergeWith(getModes().get(id));
	}
	
	public Response getFile(String path) throws Exception
	{ return new LMURLConnection(RequestMethod.SIMPLE_GET, GitHubAPI.RAW_CONTENT + getID() + '/' + path).connect(); }
	
	public String toString()
	{ return info.name; }
	
	public TextureCoords getIcon()
	{
		if(icon == null)
		{
			icon = new TextureCoords(new ResourceLocation("ftbu_guidepacks", getID()), 0, 0, 16, 16, 16, 16);
			FTBLibClient.getDownloadImage(icon.texture, GitHubAPI.RAW_CONTENT + getID() + "/icon.png", new ResourceLocation("textures/misc/unknown_pack.png"), null);
		}
		
		return icon;
	}
}