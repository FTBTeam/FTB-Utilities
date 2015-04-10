package latmod.core.net;
import latmod.core.item.IClientActionItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageClientItemAction extends MessageLM<MessageClientItemAction>
{
	public MessageClientItemAction() { }
	
	public MessageClientItemAction(String s, NBTTagCompound tag)
	{
		data = new NBTTagCompound();
		data.setString("A", s);
		if(tag != null) data.setTag("T", tag);
	}
	
	public void onMessage(MessageContext ctx)
	{
		EntityPlayer ep = ctx.getServerHandler().playerEntity;
		
		ItemStack is = ep.inventory.mainInventory[ep.inventory.currentItem];
		
		if(is != null && is.getItem() instanceof IClientActionItem)
			is = ((IClientActionItem)is.getItem()).onClientAction(is, ep, data.getString("A"), (NBTTagCompound)data.getTag("T"));
		
		if(is != null && is.stackSize <= 0) is = null;
		
		ep.inventory.mainInventory[ep.inventory.currentItem] = is;
		ep.inventory.markDirty();
	}
}