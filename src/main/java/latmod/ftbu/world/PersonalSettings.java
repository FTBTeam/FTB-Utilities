package latmod.ftbu.world;

import latmod.ftbu.net.MessageLMPlayerUpdateSettings;
import latmod.ftbu.util.LMSecurityLevel;
import latmod.lib.ByteIOStream;
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
		chatLinks = io.readBoolean();
		explosions = io.readBoolean();
		blocks = LMSecurityLevel.VALUES_3[io.readUnsignedByte()];
		fakePlayers = io.readBoolean();
	}
	
	public void writeToNet(ByteIOStream io)
	{
		io.writeBoolean(chatLinks);
		io.writeBoolean(explosions);
		io.writeByte(blocks.ID);
		io.writeBoolean(fakePlayers);
	}
	
	public void update()
	{
		if(owner.getSide().isServer()) owner.toPlayerMP().sendUpdate();
		else new MessageLMPlayerUpdateSettings(owner).sendToServer();
	}
}