package latmod.core.mod.net;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.Side;

public class MessageCustomClientAction implements IMessage, IMessageHandler<MessageCustomClientAction, IMessage>
{
	public String channel;
	public String action;
	public NBTTagCompound extraData;
	
	public MessageCustomClientAction() { }
	
	public MessageCustomClientAction(String c, String s, NBTTagCompound tag)
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
	
	public IMessage onMessage(MessageCustomClientAction message, MessageContext ctx)
	{
		EntityPlayer ep = ctx.getServerHandler().playerEntity;
		MinecraftForge.EVENT_BUS.post(new CustomActionEvent(ep, message.channel, message.action, message.extraData, Side.SERVER));
		return null;
	}
}