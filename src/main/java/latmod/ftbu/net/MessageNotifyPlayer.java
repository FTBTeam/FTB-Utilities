package latmod.ftbu.net;
import cpw.mods.fml.common.network.simpleimpl.*;
import latmod.core.util.ByteIOStream;
import latmod.ftbu.util.Notification;
import latmod.ftbu.util.client.ClientNotifications;

public class MessageNotifyPlayer extends MessageLM<MessageNotifyPlayer>
{
	public String data;
	
	public MessageNotifyPlayer() { }
	
	public MessageNotifyPlayer(Notification n)
	{ data = n.toJson(); }
	
	public void readData(ByteIOStream io) throws Exception
	{
		data = io.readUTF();
	}
	
	public void writeData(ByteIOStream io) throws Exception
	{
		io.writeUTF(data);
	}
	
	public IMessage onMessage(MessageNotifyPlayer m, MessageContext ctx)
	{
		ClientNotifications.add(Notification.fromJson(m.data));
		return null;
	}
}