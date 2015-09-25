package latmod.ftbu.net;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import io.netty.buffer.ByteBuf;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.util.Notification;
import latmod.ftbu.util.client.ClientNotifications;
import net.minecraft.nbt.NBTTagCompound;

public class MessageNotifyPlayer extends MessageLM<MessageNotifyPlayer> implements IClientMessageLM<MessageNotifyPlayer>
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
		data = LMNetHelper.readTagCompound(bb);
	}
	
	public void toBytes(ByteBuf bb)
	{
		LMNetHelper.writeTagCompound(bb, data);
	}
	
	public IMessage onMessage(MessageNotifyPlayer m, MessageContext ctx)
	{ FTBU.proxy.handleClientMessage(m, ctx); return null; }
	
	@SideOnly(Side.CLIENT)
	public void onMessageClient(MessageNotifyPlayer m, MessageContext ctx)
	{ ClientNotifications.add(Notification.readFromNBT(m.data)); }
}