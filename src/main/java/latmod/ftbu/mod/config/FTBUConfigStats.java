package latmod.ftbu.mod.config;

import latmod.ftbu.api.guide.GuideInfo;
import latmod.lib.config.*;

public class FTBUConfigStats
{
	public static final ConfigGroup group = new ConfigGroup("statistics");
	
	@GuideInfo(info = "Google Analytics tracking ID. Example: UA-12345678-2. Blank - modpack stats disabled", def = "Blank")
	public static final ConfigEntryString trackingID = new ConfigEntryString("trackingID", "");
	
	@GuideInfo(info = "Modpack name. Recommened not to have whitespaces. Example: MyPack.", def = "Blank")
	public static final ConfigEntryString modpack = new ConfigEntryString("modpack", "");
	
	public static void load(ConfigFile f)
	{
		group.addAll(FTBUConfigStats.class);
		f.add(group);
	}
}