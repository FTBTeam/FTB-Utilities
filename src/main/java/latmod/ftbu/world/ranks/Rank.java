package latmod.ftbu.world.ranks;

import ftb.lib.FTBLib;
import latmod.lib.ByteIOStream;
import latmod.lib.config.*;
import latmod.lib.util.FinalIDObject;
import net.minecraft.util.EnumChatFormatting;

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

	public void writeToIO(ByteIOStream io)
	{
		io.writeByte(color.get().ordinal());
		io.writeUTF(prefix.get());
		config_group.generateSynced(true).write(io);
	}

	public void readFromIO(ByteIOStream io)
	{
		color.set(EnumChatFormatting.values()[io.readUnsignedByte()]);
		prefix.set(io.readUTF());

		ConfigGroup group = new ConfigGroup(null);
		group.read(io);
		config_group.loadFromGroup(group);
	}
}