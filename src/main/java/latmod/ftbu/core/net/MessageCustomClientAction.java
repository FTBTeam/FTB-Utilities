package latmod.ftbu.core.net;
import io.netty.buffer.ByteBuf;
import latmod.ftbu.core.event.CustomAction;
import latmod.ftbu.mod.FTBU;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.*;

public class MessageCustomClientAction extends MessageLM<MessageCustomClientAction>
{
	public String channel;
	public NBTTagCompound data;
	
	public MessageCustomClientAction() { }
	
	public MessageCustomClientAction(String s)
	{
		channel = s;
		
		CustomActionFromClient h = CustomAction.cHandlers.get(channel);
		
		if(h != null)
		{
			data = new NBTTagCompound();
			h.sendToServer(FTBU.proxy.getClientPlayer(), data);
		}
	}
	
	public void fromBytes(ByteBuf bb)
	{
		channel = LMNetHelper.readString(bb);
		data = LMNetHelper.readTagCompound(bb);
	}
	
	public void toBytes(ByteBuf bb)
	{
		LMNetHelper.writeString(bb, channel);
		LMNetHelper.writeTagCompound(bb, data);
	}
	
	public IMessage onMessage(MessageCustomClientAction m, MessageContext ctx)
	{
		CustomActionFromClient h = CustomAction.cHandlers.get(m.channel);
		h.readFromClient(ctx.getServerHandler().playerEntity, m.data);
		return null;
	}
}