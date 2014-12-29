package latmod.core.net;
import latmod.core.Notification;
import latmod.core.mod.LC;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.*;

public class MessageNotifyPlayer extends MessageLM implements IMessageHandler<MessageNotifyPlayer, IMessage>
{
	public MessageNotifyPlayer() { }
	
	public MessageNotifyPlayer(Notification n)
	{
		data = new NBTTagCompound();
		n.writeToNBT(data);
	}
	
	public IMessage onMessage(MessageNotifyPlayer m, MessageContext ctx)
	{
		LC.proxy.notifyPlayer(Notification.readFromNBT(m.data));
		return null;
	}
}