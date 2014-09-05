package latmod.core.mod.net;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.Side;

public class MessageCustomClientAction implements IMessage, IMessageHandler<MessageCustomClientAction, IMessage>
{
	public String action;
	public NBTTagCompound extraData;
	
	public MessageCustomClientAction() { }
	
	public MessageCustomClientAction(String s, NBTTagCompound tag)
	{
		action = s;
		extraData = tag;
	}
	
	public void fromBytes(ByteBuf data)
	{
		action = LMNetHandler.readString(data);
		extraData = LMNetHandler.readNBTTagCompound(data);
	}
	
	public void toBytes(ByteBuf data)
	{
		LMNetHandler.writeString(data, action);
		LMNetHandler.writeNBTTagCompound(data, extraData);
	}
	
	public IMessage onMessage(MessageCustomClientAction message, MessageContext ctx)
	{
		EntityPlayer ep = ctx.getServerHandler().playerEntity;
		new CustomActionEvent(ep, message.action, message.extraData, Side.SERVER).post();
		return null;
	}
}