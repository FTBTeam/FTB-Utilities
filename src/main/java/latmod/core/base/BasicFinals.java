package latmod.core.base;
import net.minecraft.util.*;

public class BasicFinals
{
	public final String modID;
	public final String assets;
	
	public BasicFinals(String s)
	{
		modID = s;
		assets = s.toLowerCase() + ":";
	}
	
	public final ResourceLocation getLocation(String s)
	{ return new ResourceLocation(modID, s); }
	
	public final String getBlockName(String s)
	{ return assets + "tile." + s; }
	
	public final String getItemName(String s)
	{ return assets + "item." + s; }
	
	public final String translate(String s, Object... args)
	{ if(args == null || args.length == 0) return StatCollector.translateToLocal(assets + s);
	else return StatCollector.translateToLocalFormatted(assets + s, args); }
}