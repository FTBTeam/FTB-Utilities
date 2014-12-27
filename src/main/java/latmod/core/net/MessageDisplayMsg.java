package latmod.core.net;
import latmod.core.mod.LC;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.*;

public class MessageDisplayMsg extends MessageLM implements IMessageHandler<MessageDisplayMsg, IMessage>
{
	public MessageDisplayMsg() { }
	
	public MessageDisplayMsg(String title, String desc, ItemStack item, long d)
	{
		data = new NBTTagCompound();
		data.setString("T", title);
		data.setString("D", desc);
		
		NBTTagCompound tag = new NBTTagCompound();
		item.writeToNBT(tag);
		data.setTag("I", tag);
		
		data.setLong("S", d);
	}
	
	public IMessage onMessage(MessageDisplayMsg m, MessageContext ctx)
	{
		LC.proxy.displayMessage(m.data.getString("T"), m.data.getString("D"), ItemStack.loadItemStackFromNBT(m.data.getCompoundTag("I")), m.data.getLong("S"));
		return null;
	}
}