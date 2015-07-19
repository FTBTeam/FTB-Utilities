package latmod.ftbu.mod.config;

import java.io.File;

import latmod.ftbu.core.*;
import latmod.ftbu.core.event.FTBUReadmeEvent;
import latmod.ftbu.core.util.*;

import com.google.gson.annotations.Expose;

public class ConfigWorldBorder
{
	private static File saveFile;
	
	@Expose public Boolean enabled;
	@Expose public Integer radius;
	@Expose public IntMap custom;
	
	public static void load()
	{
		saveFile = new File(LatCoreMC.latmodFolder, "ftbu/world_border.txt");
		FTBUConfig.world_border = LatCore.fromJsonFile(saveFile, ConfigWorldBorder.class);
		if(FTBUConfig.world_border == null) FTBUConfig.world_border = new ConfigWorldBorder();
		FTBUConfig.world_border.loadDefaults();
		save();
	}
	
	public void loadDefaults()
	{
		if(enabled == null) enabled = false;
		if(radius == null) radius = 10000;
		radius = MathHelperLM.clampInt(radius, 20, 20000000);
		
		if(custom == null) custom = new IntMap();
		custom.setDefVal(-1);
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
		world_border.add("radius", "Radius of the world border, starting at 0, 0. Its a square, so if radius is 5000, then 10000x10000 blocks are available of the world. Other dimensions without a custom size, will scale depending on dimension scale (in nether, size is 8 times smaller)", 5000);
		world_border.add("custom", "'DimensionID:Size' map. Example: \"custom\": { \"7\":1000, \"27\":3000 }", "Blank");
	}
	
	public void setWorldBorder(int dim, int rad)
	{
		if(dim == 0) radius = rad;
		else custom.put(dim, rad);
		save();
	}
	
	public int getWorldBorder(int dim)
	{
		if(!enabled) return 0;
		return MathHelperLM.clampInt(getWorldBorder0(dim), 20, 20000000);
	}
	
	private int getWorldBorder0(int dim)
	{
		if(dim == 0) return radius;
		int r = custom.get(dim);
		if(r != -1) return r;
		return (int)(radius * LMDimUtils.getWorldScale(LMDimUtils.getWorld(dim)));
	}
}