package ftb.utils.world;

import ftb.lib.PrivacyLevel;
import latmod.lib.*;
import net.minecraft.nbt.NBTTagCompound;

public class PersonalSettings
{
	public static final byte CHAT_LINKS = 0;
	public static final byte EXPLOSIONS = 1;
	public static final byte FAKE_PLAYERS = 2;
	
	public byte flags = 0;
	public PrivacyLevel blocks;
	
	public PersonalSettings()
	{
		blocks = PrivacyLevel.FRIENDS;
		setDefaultFlags();
	}
	
	private void setDefaultFlags()
	{
		set(CHAT_LINKS, true);
		set(EXPLOSIONS, true);
		set(FAKE_PLAYERS, true);
	}
	
	public boolean set(byte flag, boolean v)
	{
		if(get(flag) != v)
		{
			flags = Bits.setBit(flags, flag, v);
			return true;
		}
		return false;
	}
	
	public boolean get(byte flag)
	{ return Bits.getBit(flags, flag); }
	
	public void readFromServer(NBTTagCompound tag)
	{
		if(!tag.hasKey("Flags"))
		{
			flags = 0;
			setDefaultFlags();
		}
		else flags = tag.getByte("Flags");
		blocks = tag.hasKey("Blocks") ? blocks = PrivacyLevel.VALUES_3[tag.getByte("Blocks")] : PrivacyLevel.FRIENDS;
	}
	
	public void writeToServer(NBTTagCompound tag)
	{
		tag.setByte("Flags", flags);
		tag.setByte("Blocks", (byte) blocks.ID);
	}
	
	public void readFromNet(ByteIOStream io)
	{
		flags = io.readByte();
		blocks = PrivacyLevel.VALUES_3[io.readUnsignedByte()];
	}
	
	public void writeToNet(ByteIOStream io)
	{
		io.writeByte(flags);
		io.writeByte(blocks.ID);
	}
}