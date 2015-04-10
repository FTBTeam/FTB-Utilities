package latmod.core.net;
import latmod.core.Notification;
import latmod.core.mod.LC;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageNotifyPlayer extends MessageLM<MessageNotifyPlayer>
{
	public MessageNotifyPlayer() { }
	
	public MessageNotifyPlayer(Notification n)
	{
		data = new NBTTagCompound();
		n.writeToNBT(data);
	}
	
	public void onMessage(MessageContext ctx)
	{
		LC.proxy.notifyPlayer(Notification.readFromNBT(data));
	}
}