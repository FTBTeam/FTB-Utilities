package latmod.core.mod.net;
import io.netty.buffer.ByteBuf;
import latmod.core.mod.item.IClientActionItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.*;

public class MessageClientItemAction implements IMessage, IMessageHandler<MessageClientItemAction, IMessage>
{
	public ItemStack item;
	public String action;
	public NBTTagCompound extraData;
	
	public MessageClientItemAction() { }
	
	public MessageClientItemAction(ItemStack is, String s, NBTTagCompound tag)
	{
		item = is;
		action = s;
		extraData = tag;
	}
	
	public void fromBytes(ByteBuf data)
	{
		item = LMNetHandler.readItemStack(data);
		action = LMNetHandler.readString(data);
		extraData = LMNetHandler.readNBTTagCompound(data);
	}
	
	public void toBytes(ByteBuf data)
	{
		LMNetHandler.writeItemStack(data, item);
		LMNetHandler.writeString(data, action);
		LMNetHandler.writeNBTTagCompound(data, extraData);
	}
	
	public IMessage onMessage(MessageClientItemAction message, MessageContext ctx)
	{
		EntityPlayer ep = ctx.getServerHandler().playerEntity;
		
		if(message.item != null && message.item.getItem() instanceof IClientActionItem)
			((IClientActionItem)message.item.getItem()).onClientAction(message.item, ep, message.action, message.extraData);
		
		return null;
	}
}