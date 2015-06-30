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
	
	public MessageUpdateAllData()
	{
		worldID = LMWorld.getID();
		
		players = new NBTTagCompound();
		LMDataLoader.writePlayersToNBT(players, false);
	}
	
	public void fromBytes(ByteBuf bb)
	{
		worldID = new UUID(bb.readLong(), bb.readLong());
		players = readTagCompound(bb);
	}
	
	public void toBytes(ByteBuf bb)
	{
		bb.writeLong(worldID.getMostSignificantBits());
		bb.writeLong(worldID.getLeastSignificantBits());
		writeTagCompound(bb, players);
	}
	
	public IMessage onMessage(MessageUpdateAllData m, MessageContext ctx)
	{
		LMWorld.setID(m.worldID);
		LMDataLoader.readPlayersFromNBT(m.players, false);
		return null;
	}
}