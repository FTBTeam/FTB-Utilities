package latmod.core.net;
import io.netty.buffer.ByteBuf;
import latmod.core.event.CustomAction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.*;

public class MessageCustomClientAction extends MessageLM<MessageCustomClientAction>
{
	public String channel;
	public NBTTagCompound data;
	
	public MessageCustomClientAction() { }
	
	public MessageCustomClientAction(EntityPlayer ep, String s)
	{
		channel = s;
		
		CustomAction.FromClient h = CustomAction.cHandlers.get(channel);
		
		if(h != null)
		{
			data = new NBTTagCompound();
			h.sendToServer(ep, data);
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
	
	public IMessage onMessage(MessageCustomClientAction m, MessageContext ctx)
	{
		CustomAction.FromClient h = CustomAction.cHandlers.get(m.channel);
		h.readFromClient(ctx.getServerHandler().playerEntity, m.data);
		return null;
	}
}