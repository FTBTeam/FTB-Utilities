package latmod.ftbu.core.net;
import io.netty.buffer.ByteBuf;
import latmod.ftbu.core.Notification;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.client.FTBURenderHandler;
import latmod.ftbu.mod.client.gui.GuiNotification;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;

public class MessageNotifyPlayer extends MessageLM<MessageNotifyPlayer> implements IClientMessageLM<MessageNotifyPlayer>
{
	public String title, desc;
	public int timer;
	public ItemStack item;
	
	public MessageNotifyPlayer() { }
	
	public MessageNotifyPlayer(Notification n)
	{
		title = n.title;
		desc = n.desc;
		timer = n.timer;
		item = n.item;
	}
	
	public void fromBytes(ByteBuf bb)
	{
		title = LMNetHelper.readString(bb);
		desc = LMNetHelper.readString(bb);
		timer = bb.readShort();
		item = null;
		
		NBTTagCompound itemTag = LMNetHelper.readTagCompound(bb);
		if(itemTag != null) item = ItemStack.loadItemStackFromNBT(itemTag);
	}
	
	public void toBytes(ByteBuf bb)
	{
		LMNetHelper.writeString(bb, title);
		LMNetHelper.writeString(bb, desc);
		bb.writeShort(timer);
		
		NBTTagCompound itemTag = null;
		
		if(item != null)
		{
			itemTag = new NBTTagCompound();
			item.writeToNBT(itemTag);
		}
		
		LMNetHelper.writeTagCompound(bb, itemTag);
	}
	
	public IMessage onMessage(MessageNotifyPlayer m, MessageContext ctx)
	{ FTBU.proxy.handleClientMessage(m, ctx); return null; }
	
	@SideOnly(Side.CLIENT)
	public void onMessageClient(MessageNotifyPlayer m, MessageContext ctx)
	{
		if(m.timer <= 0) return;
		Notification n = new Notification(m.title, m.desc, m.item, m.timer);
		FTBURenderHandler.messages.add(new GuiNotification(n));
	}
}