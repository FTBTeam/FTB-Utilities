package latmod.ftbu.mod.config;

import java.io.File;

import latmod.core.util.*;
import latmod.ftbu.api.readme.ReadmeInfo;
import latmod.ftbu.util.*;

public class ConfigWorldBorder
{
	private static transient File saveFile;
	
	@ReadmeInfo(info = "true enables world border, false disables.", def = "false")
	public Boolean enabled;
	
	@ReadmeInfo(info = "Radius of the world border, starting at 0, 0. Its a square, so if radius is 5000, then 10000x10000 blocks are available of the world. Other dimensions without a custom size, will scale depending on dimension scale (in nether, size is 8 times smaller)", def = "5000")
	public Integer radius;
	
	@ReadmeInfo(info = "'DimensionID:Size' map. Example: \"custom\": { \"7\":1000, \"27\":3000 }", def = "Blank")
	public IntMap custom;
	
	public static void load()
	{
		saveFile = new File(LatCoreMC.latmodFolder, "ftbu/world_border.txt");
		FTBUConfig.world_border = LMJsonUtils.fromJsonFile(saveFile, ConfigWorldBorder.class);
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
		
		if(!LMJsonUtils.toJsonFile(saveFile, FTBUConfig.world_border))
			LatCoreMC.logger.warn(saveFile.getName() + " failed to save!");
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