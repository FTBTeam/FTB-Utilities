package latmod.core.net;
import io.netty.buffer.ByteBuf;
import latmod.core.Notification;
import latmod.core.mod.LC;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.*;

public class MessageNotifyPlayer extends MessageLM<MessageNotifyPlayer>
{
	public NBTTagCompound data;
	
	public MessageNotifyPlayer() { }
	
	public MessageNotifyPlayer(Notification n)
	{
		data = new NBTTagCompound();
		n.writeToNBT(data);
	}
	
	public void fromBytes(ByteBuf bb)
	{
		data = readTagCompound(bb);
	}
	
	public void toBytes(ByteBuf bb)
	{
		writeTagCompound(bb, data);
	}
	
	public IMessage onMessage(MessageNotifyPlayer m, MessageContext ctx)
	{
		LC.proxy.notifyPlayer(Notification.readFromNBT(m.data));
		return null;
	}
}