package latmod.ftbu.core.net;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

import latmod.ftbu.core.*;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.*;

public class MessageLMWorldUpdate extends MessageLM<MessageLMWorldUpdate>
{
	public UUID worldID;
	public NBTTagCompound players;
	
	public MessageLMWorldUpdate()
	{
		worldID = LMWorld.getID();
		
		players = new NBTTagCompound();
		LMDataLoader.writePlayersToNet(players);
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
	
	public IMessage onMessage(MessageLMWorldUpdate m, MessageContext ctx)
	{
		LMWorld.setID(m.worldID);
		LMDataLoader.readPlayersFromNet(m.players);
		return null;
	}
}