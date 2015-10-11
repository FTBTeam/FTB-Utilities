package latmod.ftbu.net;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import io.netty.buffer.ByteBuf;
import latmod.ftbu.notification.Notification;
import latmod.ftbu.util.client.ClientNotifications;

public class MessageNotifyPlayer extends MessageLM<MessageNotifyPlayer>
{
	public String data;
	
	public MessageNotifyPlayer() { }
	
	public MessageNotifyPlayer(Notification n)
	{ data = n.toJson(); }
	
	public void fromBytes(ByteBuf io)
	{
		data = ByteBufUtils.readUTF8String(io);
	}
	
	public void toBytes(ByteBuf io)
	{
		ByteBufUtils.writeUTF8String(io, data);
	}
	
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageNotifyPlayer m, MessageContext ctx)
	{
		ClientNotifications.add(Notification.fromJson(m.data));
		return null;
	}
}