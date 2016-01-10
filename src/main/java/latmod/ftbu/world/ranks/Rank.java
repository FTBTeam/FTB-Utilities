package latmod.ftbu.world.ranks;

import ftb.lib.FTBLib;
import latmod.lib.IntList;
import latmod.lib.config.*;
import latmod.lib.util.*;
import net.minecraft.util.EnumChatFormatting;

import java.io.*;

public class Rank extends FinalIDObject
{
	public final ConfigEntryString parent = new ConfigEntryString("parent", "-");
	public final ConfigEntryEnum<EnumChatFormatting> color = new ConfigEntryEnum<>("color", EnumChatFormatting.class, FTBLib.chatColors, EnumChatFormatting.WHITE, false);
	public final ConfigEntryString prefix = new ConfigEntryString("prefix", "");
	public final ConfigEntryStringArray allowed_commands = new ConfigEntryStringArray("allowed_commands", "*");
	public final ConfigGroup config_group = new ConfigGroup("config");
	public final RankConfig config;
	
	public Rank(String id)
	{
		super(id);
		config = new RankConfig();
		config_group.addAll(RankConfig.class, config, false);
	}
	
	public Rank getParentRank()
	{
		String s = parent.get();
		if(s.isEmpty() || s.equals("-")) return null;
		return Ranks.ranks.get(s);
	}
	
	public void setDefaults()
	{
		if(this == Ranks.ADMIN)
		{
			config.max_claims.set(1000);
			config.max_homes.set(100);
			config.admin_server_info.set(true);
			config.allow_creative_interact_secure.set(true);
			
			config.max_claims.bounds = new IntBounds(1000, config.max_claims.bounds.minValue, config.max_claims.bounds.maxValue);
			config.max_homes.bounds = new IntBounds(100, config.max_homes.bounds.minValue, config.max_homes.bounds.maxValue);
			config.admin_server_info.defValue = true;
			config.allow_creative_interact_secure.defValue = true;
		}
		else
		{
			config.dimension_blacklist.set(IntList.asList(1));
			config.dimension_blacklist.defValue.clear();
			config.dimension_blacklist.defValue.add(1);
			
		}
	}
	
	public void writeToIO(DataOutput io) throws Exception
	{
		io.writeByte(color.get().ordinal());
		io.writeUTF(prefix.get());
		config_group.generateSynced(true).write(io);
	}
	
	public void readFromIO(DataInput io) throws Exception
	{
		color.set(EnumChatFormatting.values()[io.readUnsignedByte()]);
		prefix.set(io.readUTF());
		
		ConfigGroup group = new ConfigGroup(null);
		group.read(io);
		config_group.loadFromGroup(group);
	}
}