package ftb.utils.world.ranks;

import ftb.lib.FTBLib;
import ftb.lib.api.config.*;
import latmod.lib.IntList;
import latmod.lib.util.FinalIDObject;
import net.minecraft.util.EnumChatFormatting;

public class Rank extends FinalIDObject
{
	public final ConfigEntryString parent = new ConfigEntryString("parent", "-");
	public final ConfigEntryEnum<EnumChatFormatting> color = new ConfigEntryEnum<>("color", FTBLib.chatColors, EnumChatFormatting.WHITE, false);
	public final ConfigEntryString prefix = new ConfigEntryString("prefix", "");
	//public final ConfigEntryStringArray allowed_commands = new ConfigEntryStringArray("allowed_commands", "*");
	public final RankConfig config;
	
	public Rank(String id)
	{
		super(id);
		config = new RankConfig();
	}
	
	public Rank getParentRank()
	{
		String s = parent.getAsString();
		if(s.isEmpty() || s.equals("-")) return null;
		return Ranks.getRankFor(s);
	}
	
	public void setDefaults()
	{
		if(this == Ranks.ADMIN)
		{
			config.max_claims.set(1000);
			config.max_homes.set(100);
			config.allow_creative_interact_secure.set(true);
			config.allow_creative_interact_secure.defValue = true;
		}
		else if(this == Ranks.PLAYER)
		{
			config.dimension_blacklist.set(IntList.asList(1));
			config.dimension_blacklist.defValue.clear();
			config.dimension_blacklist.defValue.addAll(config.dimension_blacklist.getAsIntList());
		}
	}
}