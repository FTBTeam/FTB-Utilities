package latmod.ftbu.api.readme;

import java.io.File;

import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.config.FTBUConfig;
import latmod.ftbu.util.LatCoreMC;
import latmod.lib.LMFileUtils;

public class ReadmeSaveHandler
{
	public static void saveReadme() throws Exception
	{
		ReadmeFile file = new ReadmeFile();
		FTBUConfig.saveReadme(file);
		FTBU.proxy.onReadmeEvent(file);
		
		new EventSaveReadme(file).post();
		
		StringBuilder sb = new StringBuilder();
		
		for(int j = 0; j < file.map.size(); j++)
		{
			ReadmeCategory c = file.map.values.get(j);
			
			sb.append('[');
			sb.append(c.name);
			sb.append(']');
			sb.append('\n');
			
			for(int i = 0; i < c.lines.size(); i++)
			{
				sb.append(c.lines.get(i));
				sb.append('\n');
			}
			
			sb.append('\n');
		}
		
		LMFileUtils.save(new File(LatCoreMC.latmodFolder, "readme.txt"), sb.toString().trim());
	}
}
