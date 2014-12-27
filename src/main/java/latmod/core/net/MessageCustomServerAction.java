package latmod.core.net;
import latmod.core.event.CustomActionEvent;
import latmod.core.mod.LC;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.Side;

public class MessageCustomServerAction extends MessageLM implements IMessageHandler<MessageCustomServerAction, IMessage>
{
	public MessageCustomServerAction() { }
	
	public MessageCustomServerAction(String action, NBTTagCompound extraData)
	{
		data = new NBTTagCompound();
		data.setString("A", action);
		if(extraData != null) data.setTag("D", extraData);
	}
	
	public IMessage onMessage(MessageCustomServerAction m, MessageContext ctx)
	{
		EntityPlayer ep = LC.proxy.getClientPlayer();
		new CustomActionEvent(ep, m.data.getString("A"), (NBTTagCompound)m.data.getTag("D"), Side.CLIENT).post();
		return null;
	}
}