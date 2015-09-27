package latmod.ftbu.net;
import cpw.mods.fml.common.network.simpleimpl.*;
import latmod.core.util.ByteIOStream;
import latmod.ftbu.item.IClientActionItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

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
	
	public void readData(ByteIOStream io) throws Exception
	{
		action = io.readString();
		data = LMNetHelper.readTagCompound(io);
	}
	
	public void writeData(ByteIOStream io) throws Exception
	{
		io.writeString(action);
		LMNetHelper.writeTagCompound(io, data);
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