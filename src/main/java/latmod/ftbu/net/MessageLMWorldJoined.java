package latmod.ftbu.net;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import ftb.lib.*;
import io.netty.buffer.ByteBuf;
import latmod.ftbu.api.EventLMWorldClient;
import latmod.ftbu.mod.client.FTBUClient;
import latmod.ftbu.world.*;
import net.minecraft.nbt.NBTTagCompound;

public class MessageLMWorldJoined extends MessageLM<MessageLMWorldJoined>
{
	public int clientPlayerID;
	public NBTTagCompound data;
	
	public MessageLMWorldJoined() { }
	
	public MessageLMWorldJoined(int p)
	{
		clientPlayerID = p;
		data = new NBTTagCompound();
		LMWorldServer.inst.writeDataToNet(data, p);
	}
	
	public void fromBytes(ByteBuf io)
	{
		clientPlayerID = io.readInt();
		data = ByteBufUtils.readTag(io);
	}
	
	public void toBytes(ByteBuf io)
	{
		io.writeInt(clientPlayerID);
		ByteBufUtils.writeTag(io, data);
	}
	
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageLMWorldJoined m, MessageContext ctx)
	{
		LMWorldClient.inst = new LMWorldClient(m.clientPlayerID);
		LMWorldClient.inst.readDataFromNet(m.data, true);
		FTBLib.logger.info("Joined the server with PlayerID " + LMWorldClient.inst.clientPlayer.playerID + " on world " + FTBWorld.client.getWorldIDS());
		FTBUClient.onWorldJoined();
		new EventLMWorldClient.Joined(LMWorldClient.inst).post();
		return null;
	}
}