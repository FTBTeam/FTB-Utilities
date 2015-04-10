package latmod.core.net;
import latmod.core.event.CustomAction;
import latmod.core.mod.LC;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageCustomServerAction extends MessageLM<MessageCustomServerAction>
{
	public MessageCustomServerAction() { }
	
	public MessageCustomServerAction(EntityPlayerMP ep, String channel)
	{
		data = new NBTTagCompound();
		
		CustomAction.FromServer h = CustomAction.sHandlers.get(channel);
		
		if(h != null)
		{
			NBTTagCompound data1 = new NBTTagCompound();
			h.sendToClient(ep, data1);
			data.setString("C", channel);
			if(!data.hasNoTags()) data.setTag("D", data1);
		}
	}
	
	public void onMessage(MessageContext ctx)
	{
		CustomAction.FromServer h = CustomAction.sHandlers.get(data.getString("C"));
		h.readFromServer(LC.proxy.getClientPlayer(), (NBTTagCompound)data.getTag("D"));
	}
}