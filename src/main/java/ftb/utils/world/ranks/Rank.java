package ftb.utils.world.ranks;

import ftb.lib.FTBLib;
import latmod.lib.*;
import latmod.lib.config.*;
import latmod.lib.util.*;
import net.minecraft.util.EnumChatFormatting;

public class Rank extends FinalIDObject
{
	public final ConfigEntryString parent = new ConfigEntryString("parent", "-");
	public final ConfigEntryEnum<EnumChatFormatting> color = new ConfigEntryEnum<>("color", EnumChatFormatting.class, FTBLib.chatColors, EnumChatFormatting.WHITE, false);
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
		String s = parent.get();
		if(s.isEmpty() || s.equals("-")) return null;
		return Ranks.getRankFor(s);
	}
	
	public void setDefaults()
	{
		if(this == Ranks.ADMIN)
		{
			config.max_claims.set(1000);
			config.max_homes.set(100);
			config.admin_server_info.set(true);
			config.allow_creative_interact_secure.set(true);
			
			config.max_claims.bounds = new IntBounds(config.max_claims.get(), config.max_claims.bounds.minValue, config.max_claims.bounds.maxValue);
			config.max_homes.bounds = new IntBounds(config.max_homes.get(), config.max_homes.bounds.minValue, config.max_homes.bounds.maxValue);
			config.admin_server_info.defValue = true;
			config.allow_creative_interact_secure.defValue = true;
		}
		else if(this == Ranks.PLAYER)
		{
			config.dimension_blacklist.set(IntList.asList(1));
			config.dimension_blacklist.defValue.clear();
			config.dimension_blacklist.defValue.addAll(config.dimension_blacklist.get());
		}
	}
	
	public void writeToIO(ByteIOStream io)
	{
		io.writeByte(color.get().ordinal());
		io.writeUTF(prefix.get());
		config.getAsGroup(null, false).generateSynced(true).write(io);
	}
	
	public void readFromIO(ByteIOStream io)
	{
		color.set(EnumChatFormatting.values()[io.readUnsignedByte()]);
		prefix.set(io.readUTF());
		
		ConfigGroup group = new ConfigGroup(null);
		group.read(io);
		config.getAsGroup(null, false).loadFromGroup(group);
	}
}