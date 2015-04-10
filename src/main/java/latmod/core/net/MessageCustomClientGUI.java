package latmod.core.net;
import latmod.core.mod.LC;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageCustomClientGUI extends MessageLM<MessageCustomClientGUI>
{
	public MessageCustomClientGUI() { }
	
	public MessageCustomClientGUI(String s, NBTTagCompound tag)
	{
		data = new NBTTagCompound();
		data.setString("ID", s);
		if(tag != null) data.setTag("T", tag);
	}
	
	public void onMessage(MessageContext ctx)
	{
		LC.proxy.openClientGui(data.getString("ID"), (NBTTagCompound)data.getTag("T"));
	}
}