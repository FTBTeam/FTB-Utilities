package latmod.ftbu.net;
import java.util.UUID;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import ftb.lib.FTBLib;
import io.netty.buffer.ByteBuf;
import latmod.ftbu.api.EventLMWorldClient;
import latmod.ftbu.mod.client.FTBUClient;
import latmod.ftbu.world.*;
import latmod.lib.LMStringUtils;
import net.minecraft.nbt.NBTTagCompound;

public class MessageLMWorldJoined extends MessageLM<MessageLMWorldJoined>
{
	public UUID worldID;
	public int clientPlayerID;
	public NBTTagCompound data;
	
	public MessageLMWorldJoined() { }
	
	public MessageLMWorldJoined(UUID id, int p)
	{
		worldID = id;
		clientPlayerID = p;
		data = new NBTTagCompound();
		LMWorldServer.inst.writeDataToNet(data, p);
	}
	
	public void fromBytes(ByteBuf io)
	{
		long msb = io.readLong();
		long lsb = io.readLong();
		worldID = new UUID(msb, lsb);
		clientPlayerID = io.readInt();
		data = ByteBufUtils.readTag(io);
	}
	
	public void toBytes(ByteBuf io)
	{
		io.writeLong(worldID.getMostSignificantBits());
		io.writeLong(worldID.getLeastSignificantBits());
		io.writeInt(clientPlayerID);
		ByteBufUtils.writeTag(io, data);
	}
	
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageLMWorldJoined m, MessageContext ctx)
	{
		LMWorldClient.inst = new LMWorldClient(m.worldID, LMStringUtils.fromUUID(m.worldID), m.clientPlayerID);
		LMWorldClient.inst.readDataFromNet(m.data, true);
		FTBLib.logger.info("Joined the server with PlayerID " + LMWorldClient.inst.clientPlayer.playerID + " on world " + LMWorldClient.inst.worldIDS);
		FTBUClient.onWorldJoined();
		new EventLMWorldClient.Joined(LMWorldClient.inst).post();
		return null;
	}
}