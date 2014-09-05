package latmod.core.mod.net;
import io.netty.buffer.ByteBuf;
import latmod.core.mod.LC;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.Side;

public class MessageCustomServerAction implements IMessage, IMessageHandler<MessageCustomServerAction, IMessage>
{
	public String action;
	public NBTTagCompound extraData;
	
	public MessageCustomServerAction() { }
	
	public MessageCustomServerAction(String s, NBTTagCompound tag)
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
	
	public IMessage onMessage(MessageCustomServerAction message, MessageContext ctx)
	{
		EntityPlayer ep = LC.proxy.getClientPlayer();
		new CustomActionEvent(ep, message.action, message.extraData, Side.CLIENT).post();
		return null;
	}
}