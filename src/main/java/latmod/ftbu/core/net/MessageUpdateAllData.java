package latmod.ftbu.core.net;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

import latmod.ftbu.core.*;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.*;

public class MessageUpdateAllData extends MessageLM<MessageUpdateAllData>
{
	public UUID worldID;
	public NBTTagCompound players;
	public NBTTagCompound config;
	
	public MessageUpdateAllData()
	{
		worldID = LMWorld.getID();
		
		players = new NBTTagCompound();
		LMDataLoader.writePlayersToNBT(players, false);
		
		config = new NBTTagCompound();
		IServerConfig.Registry.writeToNBT(config);
	}
	
	public void fromBytes(ByteBuf bb)
	{
		worldID = new UUID(bb.readLong(), bb.readLong());
		players = readTagCompound(bb);
		config = readTagCompound(bb);
	}
	
	public void toBytes(ByteBuf bb)
	{
		bb.writeLong(worldID.getMostSignificantBits());
		bb.writeLong(worldID.getLeastSignificantBits());
		writeTagCompound(bb, players);
		writeTagCompound(bb, config);
	}
	
	public IMessage onMessage(MessageUpdateAllData m, MessageContext ctx)
	{
		LMWorld.setID(m.worldID);
		LMDataLoader.readPlayersFromNBT(m.players, false);
		IServerConfig.Registry.readFromNBT(m.config);
		return null;
	}
}