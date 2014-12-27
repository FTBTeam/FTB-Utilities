package latmod.core.net;
import latmod.core.event.CustomActionEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.Side;

public class MessageCustomClientAction extends MessageLM implements IMessageHandler<MessageCustomClientAction, IMessage>
{
	public MessageCustomClientAction() { }
	
	public MessageCustomClientAction(String action, NBTTagCompound extraData)
	{
		data = new NBTTagCompound();
		data.setString("A", action);
		if(extraData != null) data.setTag("D", extraData);
	}
	
	public IMessage onMessage(MessageCustomClientAction m, MessageContext ctx)
	{
		EntityPlayer ep = ctx.getServerHandler().playerEntity;
		new CustomActionEvent(ep, m.data.getString("A"), (NBTTagCompound)m.data.getTag("D"), Side.SERVER).post();
		return null;
	}
}