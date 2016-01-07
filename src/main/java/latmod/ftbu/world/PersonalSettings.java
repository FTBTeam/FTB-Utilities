package latmod.ftbu.world;

import latmod.ftbu.net.MessageLMPlayerUpdateSettings;
import latmod.ftbu.util.LMSecurityLevel;
import latmod.lib.*;
import net.minecraft.nbt.NBTTagCompound;

public class PersonalSettings
{
	public final LMPlayer owner;
	
	public boolean chatLinks;
	public boolean explosions;
	public LMSecurityLevel blocks;
	public boolean fakePlayers;
	
	public PersonalSettings(LMPlayer p)
	{
		owner = p;
		chatLinks = true;
		explosions = true;
		blocks = LMSecurityLevel.FRIENDS;
		fakePlayers = true;
	}
	
	public void readFromServer(NBTTagCompound tag)
	{
		chatLinks = tag.hasKey("ChatLinks") ? tag.getBoolean("ChatLinks") : true;
		explosions = tag.hasKey("Explosions") ? tag.getBoolean("Explosions") : true;
		blocks = tag.hasKey("Blocks") ? blocks = LMSecurityLevel.VALUES_3[tag.getByte("Blocks")] : LMSecurityLevel.FRIENDS;
		fakePlayers = tag.hasKey("FakePlayers") ? tag.getBoolean("FakePlayers") : true;
	}
	
	public void writeToServer(NBTTagCompound tag)
	{
		tag.setBoolean("ChatLinks", chatLinks);
		tag.setBoolean("Explosions", explosions);
		tag.setByte("Blocks", (byte)blocks.ID);
		tag.setBoolean("FakePlayers", fakePlayers);
	}
	
	public void readFromNet(ByteIOStream io)
	{
		boolean[] flags = new boolean[8];
		Bits.fromBits(flags, io.readUnsignedByte());
		chatLinks = flags[0];
		explosions = flags[1];
		fakePlayers = flags[2];
		blocks = LMSecurityLevel.VALUES_3[io.readUnsignedByte()];
	}
	
	public void writeToNet(ByteIOStream io)
	{
		boolean[] flags = new boolean[8];
		flags[0] = chatLinks;
		flags[1] = explosions;
		flags[2] = fakePlayers;
		io.writeByte(Bits.toBits(flags));
		io.writeByte(blocks.ID);
	}
	
	public void update()
	{
		if(owner.getSide().isServer()) owner.toPlayerMP().sendUpdate();
		else new MessageLMPlayerUpdateSettings(owner).sendToServer();
	}
}