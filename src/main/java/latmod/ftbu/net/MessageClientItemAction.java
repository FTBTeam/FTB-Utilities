package latmod.ftbu.net;
import cpw.mods.fml.common.network.simpleimpl.*;
import latmod.ftbu.api.item.IClientActionItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class MessageClientItemAction extends MessageFTBU
{
	public MessageClientItemAction() { super(DATA_LONG); }
	
	public MessageClientItemAction(String s, NBTTagCompound tag)
	{
		this();
		io.writeString(s);
		writeTag(tag);
	}
	
	public IMessage onMessage(MessageContext ctx)
	{
		String action = io.readString();
		
		EntityPlayerMP ep = ctx.getServerHandler().playerEntity;
		
		ItemStack is = ep.inventory.mainInventory[ep.inventory.currentItem];
		
		if(is != null && is.getItem() instanceof IClientActionItem)
			is = ((IClientActionItem)is.getItem()).onClientAction(is, ep, action, readTag());
		
		if(is != null && is.stackSize <= 0) is = null;
		
		ep.inventory.mainInventory[ep.inventory.currentItem] = (is == null) ? null : is.copy();
		ep.inventory.markDirty();
		ep.openContainer.detectAndSendChanges();
		return null;
	}
}