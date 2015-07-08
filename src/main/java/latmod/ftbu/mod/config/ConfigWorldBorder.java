package latmod.ftbu.mod.config;

import java.io.File;
import java.util.*;

import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.event.FTBUReadmeEvent;
import latmod.ftbu.core.util.LatCore;

import com.google.gson.annotations.Expose;

public class ConfigWorldBorder
{
	private static File saveFile;
	
	@Expose public Boolean enabled;
	@Expose private Integer radius;
	@Expose private Map<Integer, Integer> custom;
	
	public static void load()
	{
		saveFile = new File(LatCoreMC.latmodFolder, "ftbu/world_border.txt");
		FTBUConfig.world_border = LatCore.fromJsonFromFile(saveFile, ConfigWorldBorder.class);
		if(FTBUConfig.world_border == null) FTBUConfig.world_border = new ConfigWorldBorder();
		FTBUConfig.world_border.loadDefaults();
		save();
	}
	
	public void loadDefaults()
	{
		if(enabled == null) enabled = false;
		if(radius == null) radius = 10000;
		if(custom == null) custom = new HashMap<Integer, Integer>();
	}
	
	public static void save()
	{
		if(FTBUConfig.world_border == null) load();
		if(!LatCore.toJsonFile(saveFile, FTBUConfig.world_border))
			LatCoreMC.logger.warn(saveFile.getName() + " failed to save!");
	}
	
	public static void saveReadme(FTBUReadmeEvent e)
	{
		FTBUReadmeEvent.ReadmeFile.Category world_border = e.file.get("latmod/ftbu/world_border.txt");
		world_border.add("enabled", "true enables world border, false disables.", false);
		world_border.add("radius", "Radius of the world border, starting at 0, 0. Its a square, so radius 5000 = 10000x10000 blocks of available world.", 5000);
		world_border.add("custom", "'DimensionID:Size' map. Example: \"custom\": { \"7\":1000, \"27\":3000 }", "Blank");
	}
	
	public void setWorldBorder(int dim, int rad)
	{
		if(dim == 0) radius = rad;
		else custom.put(dim, rad);
	}
	
	public int getWorldBorder(int dim)
	{
		if(!enabled) return 0;
		if(dim == 0) return radius;
		
		if(custom != null && !custom.isEmpty())
		{
			Integer i = custom.get(dim);
			if(i != null) return i.intValue();
		}
		
		return (int)(radius * LatCoreMC.getWorldScale(dim));
	}
}