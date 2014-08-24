package latmod.core.mod.net;
import io.netty.buffer.ByteBuf;
import latmod.core.mod.item.IClientActionItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.*;

public class MessageClientItemAction implements IMessage, IMessageHandler<MessageClientItemAction, IMessage>
{
	public Item item;
	public String action;
	public NBTTagCompound extraData;
	
	public MessageClientItemAction() { }
	
	public MessageClientItemAction(Item i, String s, NBTTagCompound tag)
	{
		item = i;
		action = s;
		extraData = tag;
	}
	
	public void fromBytes(ByteBuf data)
	{
		item = Item.getItemById(data.readShort());
		action = LMNetHandler.readString(data);
		extraData = LMNetHandler.readNBTTagCompound(data);
	}
	
	public void toBytes(ByteBuf data)
	{
		data.writeShort((item == null) ? -1 : Item.getIdFromItem(item));
		LMNetHandler.writeString(data, action);
		LMNetHandler.writeNBTTagCompound(data, extraData);
	}
	
	public IMessage onMessage(MessageClientItemAction message, MessageContext ctx)
	{
		EntityPlayer ep = ctx.getServerHandler().playerEntity;
		
		if(message.item != null && message.item instanceof IClientActionItem)
		{
			ItemStack is = ep.getHeldItem();
			((IClientActionItem)message.item).onClientAction(is, ep, message.action, message.extraData);
			if(is != null && is.stackSize <= 0) is = null;
			ep.inventory.setInventorySlotContents(ep.inventory.currentItem, is);
		}
		
		return null;
	}
}