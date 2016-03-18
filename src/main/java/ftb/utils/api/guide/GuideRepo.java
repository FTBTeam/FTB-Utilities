package ftb.utils.api.guide;

import com.google.gson.JsonObject;
import ftb.lib.api.client.FTBLibClient;
import ftb.utils.FTBU;
import latmod.lib.*;
import latmod.lib.github.GitHubAPI;
import latmod.lib.net.*;
import latmod.lib.util.FinalIDObject;
import net.minecraft.util.ResourceLocation;

import java.io.*;
import java.util.zip.*;

/**
 * Created by LatvianModder on 18.03.2016.
 */
public class GuideRepo extends FinalIDObject
{
	public final String owner;
	public final String repoID;
	public final String branchID;
	public final GuideInfo info;
	private ResourceLocation icon;
	
	public GuideRepo(String oID, String rID, String bID) throws Exception
	{
		super(oID + '/' + rID + '/' + bID);
		owner = oID;
		repoID = rID;
		branchID = bID;
		
		JsonObject o = new LMURLConnection(RequestMethod.SIMPLE_GET, GitHubAPI.RAW_CONTENT + getID() + "/guide.json").connect().asJson().getAsJsonObject();
		
		info = new GuideInfo(o);
	}
	
	public String toString()
	{ return info.name; }
	
	public void downloadPack() throws Exception
	{
		long start = LMUtils.millis();
		LMURLConnection c = new LMURLConnection(RequestMethod.SIMPLE_GET, "https://github.com/" + owner + "/" + repoID + "/archive/" + branchID + ".zip");
		
		ZipInputStream in = new ZipInputStream(new BufferedInputStream(c.connect().stream));
		File file = new File(GuideRepoList.getFolder(), info.file_name);
		LMFileUtils.delete(file);
		
		ZipEntry e;
		
		int len;
		byte[] buffer = new byte[2048];
		int startIndex = (repoID + '-' + branchID).length() + 1;
		
		while((e = in.getNextEntry()) != null)
		{
			if(!e.isDirectory())
			{
				String fileName = e.getName().substring(startIndex);
				
				if(!fileName.isEmpty())
				{
					OutputStream out = new BufferedOutputStream(new FileOutputStream(LMFileUtils.newFile(new File(file, fileName))));
					while((len = in.read(buffer)) > 0) out.write(buffer, 0, len);
					out.flush();
					out.close();
				}
			}
			
			in.closeEntry();
		}
		
		in.close();
		
		FTBU.logger.info("Done in " + (LMUtils.millis() - start) + " ms!");
	}
	
	public ResourceLocation getIcon()
	{
		if(icon == null)
		{
			String imageURL = GitHubAPI.RAW_CONTENT + getID() + "/icon.png";
			icon = new ResourceLocation("ftbu_guide", getID() + ".png");
			FTBLibClient.getDownloadImage(icon, imageURL, new ResourceLocation("textures/misc/unknown_pack.png"), null);
		}
		
		return icon;
	}
}
