package latmod.ftbu.core.net;
import io.netty.buffer.ByteBuf;
import latmod.ftbu.core.Notification;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.client.FTBURenderHandler;
import latmod.ftbu.mod.client.gui.GuiNotification;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;

public class MessageNotifyPlayer extends MessageLM<MessageNotifyPlayer> implements IClientMessageLM<MessageNotifyPlayer>
{
	public String data;
	
	public MessageNotifyPlayer() { }
	
	public MessageNotifyPlayer(Notification n)
	{
		data = n.toString();
	}
	
	public void fromBytes(ByteBuf bb)
	{
		data = LMNetHelper.readString(bb);
	}
	
	public void toBytes(ByteBuf bb)
	{
		LMNetHelper.writeString(bb, data);
	}
	
	public IMessage onMessage(MessageNotifyPlayer m, MessageContext ctx)
	{ FTBU.proxy.handleClientMessage(m, ctx); return null; }
	
	@SideOnly(Side.CLIENT)
	public void onMessageClient(MessageNotifyPlayer m, MessageContext ctx)
	{
		Notification n = Notification.getFromJson(m.data);
		if(n != null) FTBURenderHandler.messages.add(new GuiNotification(n));
	}
}