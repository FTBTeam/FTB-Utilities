package latmod.ftbu.net;
import cpw.mods.fml.common.network.simpleimpl.*;
import latmod.core.util.ByteIOStream;
import latmod.ftbu.api.EventLMPlayerClient;
import latmod.ftbu.util.client.LatCoreMCClient;
import latmod.ftbu.world.*;
import net.minecraft.nbt.NBTTagCompound;

public class MessageLMPlayerUpdate extends MessageLM<MessageLMPlayerUpdate>
{
	public int playerID;
	public NBTTagCompound data;
	
	public MessageLMPlayerUpdate() { }
	
	public MessageLMPlayerUpdate(LMPlayerServer p, boolean self)
	{
		playerID = p.playerID;
		
		data = new NBTTagCompound();
		p.writeToNet(data, self);
	}
	
	public void readData(ByteIOStream io) throws Exception
	{
		playerID = io.readInt();
		data = LMNetHelper.readTagCompound(io);
	}
	
	public void writeData(ByteIOStream io) throws Exception
	{
		io.writeInt(playerID);
		LMNetHelper.writeTagCompound(io, data);
	}
	
	public IMessage onMessage(MessageLMPlayerUpdate m, MessageContext ctx)
	{
		LMPlayerClient p = LMWorldClient.inst.getPlayer(m.playerID);
		p.readFromNet(m.data, p.getUUID().equals(LatCoreMCClient.getUUID()));
		new EventLMPlayerClient.DataChanged(p).post();
		LatCoreMCClient.onGuiClientAction();
		return null;
	}
}