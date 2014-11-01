package latmod.core.net;
import latmod.core.event.CustomActionEvent;
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
		NBTTagCompound tag = LMNetHandler.readNBTTagCompound(data);
		
		action = tag.getString("Action");
		extraData = (NBTTagCompound)tag.getTag("Data");
	}
	
	public void toBytes(ByteBuf data)
	{
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("Action", action);
		if(extraData != null) tag.setTag("Data", extraData);
		
		LMNetHandler.writeNBTTagCompound(data, tag);
	}
	
	public IMessage onMessage(MessageCustomClientAction message, MessageContext ctx)
	{
		EntityPlayer ep = ctx.getServerHandler().playerEntity;
		new CustomActionEvent(ep, message.action, message.extraData, Side.SERVER).post();
		return null;
	}
}