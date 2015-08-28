package latmod.ftbu.core.net;
import io.netty.buffer.ByteBuf;
import latmod.ftbu.core.api.*;
import latmod.ftbu.mod.FTBU;
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
		ICustomActionFromServer h = FTBUApi.getCustomActionServerHandler(channel);
		if(h != null) data = h.sendToClient(ep);
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
	
	public IMessage onMessage(MessageCustomServerAction m, MessageContext ctx)
	{
		ICustomActionFromServer h = FTBUApi.getCustomActionServerHandler(m.channel);
		h.readFromServer(FTBU.proxy.getClientPlayer(), m.data);
		return null;
	}
}