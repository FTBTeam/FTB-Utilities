package latmod.core.net;
import latmod.core.event.CustomAction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageCustomClientAction extends MessageLM<MessageCustomClientAction>
{
	public MessageCustomClientAction() { }
	
	public MessageCustomClientAction(EntityPlayer ep, String channel)
	{
		data = new NBTTagCompound();
		
		CustomAction.FromClient h = CustomAction.cHandlers.get(channel);
		
		if(h != null)
		{
			NBTTagCompound data1 = new NBTTagCompound();
			h.sendToServer(ep, data1);
			data.setString("C", channel);
			if(!data.hasNoTags()) data.setTag("D", data1);
		}
	}
	
	public void onMessage(MessageContext ctx)
	{
		CustomAction.FromClient h = CustomAction.cHandlers.get(data.getString("C"));
		h.readFromClient(ctx.getServerHandler().playerEntity, (NBTTagCompound)data.getTag("D"));
	}
}