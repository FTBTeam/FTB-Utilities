package latmod.ftbu.net;
import cpw.mods.fml.common.network.simpleimpl.*;
import latmod.core.util.ByteIOStream;
import latmod.ftbu.world.*;
import net.minecraft.nbt.NBTTagCompound;

public class MessageLMWorldUpdate extends MessageLM<MessageLMWorldUpdate>
{
	public NBTTagCompound data;
	
	public MessageLMWorldUpdate() { }
	
	public MessageLMWorldUpdate(LMWorldServer w)
	{
		w.writeDataToNet(data, 0);
	}
	
	public void readData(ByteIOStream io) throws Exception
	{
		data = LMNetHelper.readTagCompound(io);
	}
	
	public void writeData(ByteIOStream io) throws Exception
	{
		LMNetHelper.writeTagCompound(io, data);
	}
	
	public IMessage onMessage(MessageLMWorldUpdate m, MessageContext ctx)
	{
		LMWorldClient.inst.readDataFromNet(m.data, false);
		return null;
	}
}