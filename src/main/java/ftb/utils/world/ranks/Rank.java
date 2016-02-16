package ftb.utils.world.ranks;

import ftb.lib.FTBLib;
import latmod.lib.ByteIOStream;
import latmod.lib.config.*;
import latmod.lib.util.FinalIDObject;
import net.minecraft.util.EnumChatFormatting;

public class Rank extends FinalIDObject
{
	public final ConfigEntryString parent = new ConfigEntryString("parent", "-");
	public final ConfigEntryEnum<EnumChatFormatting> color = new ConfigEntryEnum<>("color", FTBLib.chatColors, EnumChatFormatting.WHITE, false);
	public final ConfigEntryString prefix = new ConfigEntryString("prefix", "");
	public final ConfigEntryStringArray permissions = new ConfigEntryStringArray("permissions");
	
	public Rank(String id)
	{
		super(id);
	}
	
	public Rank getParentRank()
	{
		String s = parent.get();
		if(s.isEmpty() || s.equals("-")) return null;
		return Ranks.getRankFor(s);
	}
	
	public void writeToIO(ByteIOStream io)
	{
		io.writeByte(color.get().ordinal());
		io.writeUTF(prefix.get());
	}
	
	public void readFromIO(ByteIOStream io)
	{
		color.set(EnumChatFormatting.values()[io.readUnsignedByte()]);
		prefix.set(io.readUTF());
	}
}