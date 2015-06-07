package latmod.ftbu.core.net;
import io.netty.buffer.ByteBuf;
import latmod.ftbu.FTBU;
import latmod.ftbu.core.event.CustomAction;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.*;

public class MessageCustomServerAction extends MessageLM<MessageCustomServerAction>
{
	public String channel;
	public NBTTagCompound data;
	
	public MessageCustomServerAction() { }
	
	public MessageCustomServerAction(EntityPlayerMP ep, String s)
	{
		channel = s;
		
		CustomAction.FromServer h = CustomAction.sHandlers.get(channel);
		
		if(h != null)
		{
			data = new NBTTagCompound();
			h.sendToClient(ep, data);
		}
	}
	
	public void fromBytes(ByteBuf bb)
	{
		channel = readString(bb);
		data = readTagCompound(bb);
	}
	
	public void toBytes(ByteBuf bb)
	{
		writeString(bb, channel);
		writeTagCompound(bb, data);
	}
	
	public IMessage onMessage(MessageCustomServerAction m, MessageContext ctx)
	{
		CustomAction.FromServer h = CustomAction.sHandlers.get(m.channel);
		h.readFromServer(FTBU.proxy.getClientPlayer(), m.data);
		return null;
	}
}