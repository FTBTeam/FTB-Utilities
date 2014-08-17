package latmod.core.mod.net;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.*;

public class MessageClientCustomAction implements IMessage, IMessageHandler<MessageClientCustomAction, IMessage>
{
	public String channel;
	public String action;
	public NBTTagCompound extraData;
	
	public MessageClientCustomAction() { }
	
	public MessageClientCustomAction(String c, String s, NBTTagCompound tag)
	{
		channel = c;
		action = s;
		extraData = tag;
	}
	
	public void fromBytes(ByteBuf data)
	{
		channel = LMNetHandler.readString(data);
		action = LMNetHandler.readString(data);
		extraData = LMNetHandler.readNBTTagCompound(data);
	}
	
	public void toBytes(ByteBuf data)
	{
		LMNetHandler.writeString(data, channel);
		LMNetHandler.writeString(data, action);
		LMNetHandler.writeNBTTagCompound(data, extraData);
	}
	
	public IMessage onMessage(MessageClientCustomAction message, MessageContext ctx)
	{
		EntityPlayer ep = ctx.getServerHandler().playerEntity;
		
		ICustomClientActionHandler h = LMNetHandler.customHandlers.get(message.channel);
		
		if(h != null) h.onClientAction(ep, message.channel, message.action, message.extraData);
		
		return null;
	}
}