package latmod.ftbu.net;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import latmod.ftbu.notification.Notification;
import latmod.ftbu.util.client.ClientNotifications;

public class MessageNotifyPlayer extends MessageFTBU
{
	public MessageNotifyPlayer() { super(DATA_SHORT); }
	
	public MessageNotifyPlayer(Notification n)
	{
		this();
		io.writeString(n.toJson());
	}
	
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageContext ctx)
	{
		ClientNotifications.add(Notification.fromJson(io.readString()));
		return null;
	}
}