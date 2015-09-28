package latmod.ftbu.net;
import java.util.UUID;

import cpw.mods.fml.common.network.simpleimpl.*;
import latmod.core.util.*;
import latmod.ftbu.api.EventLMWorldClient;
import latmod.ftbu.mod.client.FTBUClient;
import latmod.ftbu.util.LatCoreMC;
import latmod.ftbu.world.*;
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
	
	public void readData(ByteIOStream io) throws Exception
	{
		worldID = io.readUUID();
		clientPlayerID = io.readInt();
		data = LMNetHelper.readTagCompound(io);
	}
	
	public void writeData(ByteIOStream io) throws Exception
	{
		io.writeUUID(worldID);
		io.writeInt(clientPlayerID);
		LMNetHelper.writeTagCompound(io, data);
	}
	
	public IMessage onMessage(MessageLMWorldJoined m, MessageContext ctx)
	{
		LMWorldClient.inst = new LMWorldClient(m.worldID, LMStringUtils.fromUUID(m.worldID), m.clientPlayerID);
		LMWorldClient.inst.readDataFromNet(m.data, true);
		LatCoreMC.logger.info("Joined the server with PlayerID " + LMWorldClient.inst.clientPlayer.playerID + " on world " + LMWorldClient.inst.worldIDS);
		FTBUClient.onWorldJoined();
		new EventLMWorldClient.Joined(LMWorldClient.inst).post();
		return null;
	}
}