package latmod.ftbu.core.net;
import io.netty.buffer.ByteBuf;
import latmod.ftbu.core.api.*;
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
		ICustomActionFromClient h = FTBUApi.getCustomActionClientHandler(channel);
		if(h != null) data = h.sendToServer(FTBU.proxy.getClientPlayer());
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
		ICustomActionFromClient h = FTBUApi.getCustomActionClientHandler(m.channel);
		h.readFromClient(ctx.getServerHandler().playerEntity, m.data);
		return null;
	}
}