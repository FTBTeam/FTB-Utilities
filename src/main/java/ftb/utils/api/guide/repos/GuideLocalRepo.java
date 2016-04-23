package ftb.utils.api.guide.repos;

import ftb.lib.api.client.FTBLibClient;
import latmod.lib.net.Response;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LatvianModder on 03.04.2016.
 */
public class GuideLocalRepo extends GuideRepo
{
	public final File folder;
	private final GuideInfo info;
	public final Map<String, GuideMode> modes;
	private ResourceLocation icon;
	
	public GuideLocalRepo(File f) throws Exception
	{
		super(f.getName());
		folder = f;
		
		info = new GuideInfo(getFile("guide.json").asJson().getAsJsonObject());
		
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
	
	@Override
	public GuideInfo getInfo()
	{ return info; }
	
	@Override
	public Map<String, GuideMode> getModes()
	{ return modes; }
	
	@Override
	public Response getFile(String path) throws Exception
	{ return new Response(new FileInputStream(new File(folder, path))); }
	
	@Override
	public ResourceLocation getIcon()
	{
		if(icon == null)
		{
			icon = new ResourceLocation("ftbu_guidepacks", getID());
			FTBLibClient.getLocalImage(icon, new File(folder, "icon.png"), new ResourceLocation("textures/misc/unknown_pack.png"), null);
		}
		
		return icon;
	}
	
	@Override
	public boolean isLocal()
	{ return true; }
	
	public boolean needsUpdate()
	{
		GuideOnlineRepo r = getOnlineRepo();
		return r != null && !r.getInfo().version.equals(getInfo().version);
	}
	
	public GuideOnlineRepo getOnlineRepo()
	{ return GuideRepoList.onlineRepos.get(getID()); }
}