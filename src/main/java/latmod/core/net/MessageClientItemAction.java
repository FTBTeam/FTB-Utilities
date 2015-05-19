package latmod.core.net;
import io.netty.buffer.ByteBuf;
import latmod.core.item.IClientActionItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.*;

public class MessageClientItemAction extends MessageLM<MessageClientItemAction>
{
	public String action;
	public NBTTagCompound data;
	
	public MessageClientItemAction() { }
	
	public MessageClientItemAction(String s, NBTTagCompound tag)
	{
		action = s;
		data = tag;
	}
	
	public void fromBytes(ByteBuf bb)
	{
		action = readString(bb);
		data = readTagCompound(bb);
	}
	
	public void toBytes(ByteBuf bb)
	{
		writeString(bb, action);
		writeTagCompound(bb, data);
	}
	
	public IMessage onMessage(MessageClientItemAction m, MessageContext ctx)
	{
		EntityPlayerMP ep = ctx.getServerHandler().playerEntity;
		
		ItemStack is = ep.inventory.mainInventory[ep.inventory.currentItem];
		
		if(is != null && is.getItem() instanceof IClientActionItem)
			is = ((IClientActionItem)is.getItem()).onClientAction(is, ep, m.action, m.data);
		
		if(is != null && is.stackSize <= 0) is = null;
		
		ep.inventory.mainInventory[ep.inventory.currentItem] = (is == null) ? null : is.copy();
		ep.inventory.markDirty();
		ep.openContainer.detectAndSendChanges();
		return null;
	}
}