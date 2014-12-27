package latmod.core.net;
import latmod.core.mod.LCEventHandler;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.*;

public class MessageUpdateLMData extends MessageLM implements IMessageHandler<MessageUpdateLMData, IMessage>
{
	public MessageUpdateLMData()
	{
		data = new NBTTagCompound();
		LCEventHandler.instance.saveAllData(data);
	}
	
	public IMessage onMessage(MessageUpdateLMData m, MessageContext ctx)
	{
		LCEventHandler.instance.loadAllData(m.data);
		return null;
	}
}