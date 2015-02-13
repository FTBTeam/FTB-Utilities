package latmod.core.net;
import latmod.core.mod.*;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.*;

public class MessageUpdateLMData extends MessageLM implements IMessageHandler<MessageUpdateLMData, IMessage>
{
	public MessageUpdateLMData()
	{
		data = new NBTTagCompound();
		LMDataLoader.writeToNBT(data);
	}
	
	public IMessage onMessage(MessageUpdateLMData m, MessageContext ctx)
	{
		LMDataLoader.readFromNBT(m.data);
		return null;
	}
}