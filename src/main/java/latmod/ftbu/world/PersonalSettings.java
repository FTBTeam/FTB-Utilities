package latmod.ftbu.world;

import latmod.ftbu.util.LMSecurityLevel;
import latmod.lib.*;
import net.minecraft.nbt.NBTTagCompound;

public class PersonalSettings
{
	public final LMPlayer owner;
	
	public static final int CHAT_LINKS = 0;
	public static final int EXPLOSIONS = 1;
	public static final int FAKE_PLAYERS = 2;
	
	public byte flags = 0;
	public LMSecurityLevel blocks;
	
	public PersonalSettings(LMPlayer p)
	{
		owner = p;
		blocks = LMSecurityLevel.FRIENDS;
		setDefaultFlags();
	}
	
	private void setDefaultFlags()
	{
		set(CHAT_LINKS, true);
		set(EXPLOSIONS, true);
		set(FAKE_PLAYERS, true);
	}
	
	public boolean set(int flag, boolean v)
	{
		if(get(flag) != v)
		{
			flags = Bits.setBit(flags, flag, v);
			return true;
		}
		return false;
	}
	
	public boolean get(int flag)
	{ return Bits.getBit(flags, flag); }
	
	public void readFromServer(NBTTagCompound tag)
	{
		if(!tag.hasKey("Flags"))
		{
			flags = 0;
			setDefaultFlags();
		}
		else flags = tag.getByte("Flags");
		blocks = tag.hasKey("Blocks") ? blocks = LMSecurityLevel.VALUES_3[tag.getByte("Blocks")] : LMSecurityLevel.FRIENDS;
	}
	
	public void writeToServer(NBTTagCompound tag)
	{
		tag.setByte("Flags", flags);
		tag.setByte("Blocks", (byte) blocks.ID);
	}
	
	public void readFromNet(ByteIOStream io)
	{
		flags = io.readByte();
		blocks = LMSecurityLevel.VALUES_3[io.readUnsignedByte()];
	}
	
	public void writeToNet(ByteIOStream io)
	{
		io.writeByte(flags);
		io.writeByte(blocks.ID);
	}
	
	public void sendUpdate()
	{ if(owner.getSide().isServer()) owner.toPlayerMP().sendUpdate(); }
}