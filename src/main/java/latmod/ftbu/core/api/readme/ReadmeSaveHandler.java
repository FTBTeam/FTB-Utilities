package latmod.ftbu.core.api.readme;

import java.io.File;

import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.util.LMFileUtils;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.config.FTBUConfig;

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
				String k = c.lines.keys.get(i); 
				
				if(!k.isEmpty())
				{
					sb.append(k);
					sb.append(" - ");
				}
				
				sb.append(c.lines.values.get(i));
				sb.append('\n');
			}
			
			sb.append('\n');
		}
		
		LMFileUtils.save(new File(LatCoreMC.latmodFolder, "readme.txt"), sb.toString().trim());
	}
}
