package latmod.core.mod.net;
import io.netty.buffer.ByteBuf;
import latmod.core.mod.LC;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.Side;

public class MessageCustomServerAction implements IMessage, IMessageHandler<MessageCustomServerAction, IMessage>
{
	public String channel;
	public String action;
	public NBTTagCompound extraData;
	
	public MessageCustomServerAction() { }
	
	public MessageCustomServerAction(String c, String s, NBTTagCompound tag)
	{
		channel = c;
		action = s;
		extraData = tag;
	}
	
	public void fromBytes(ByteBuf data)
	{
		channel = LMNetHandler.readString(data);
		action = LMNetHandler.readString(data);
		extraData = LMNetHandler.readNBTTagCompound(data);
	}
	
	public void toBytes(ByteBuf data)
	{
		LMNetHandler.writeString(data, channel);
		LMNetHandler.writeString(data, action);
		LMNetHandler.writeNBTTagCompound(data, extraData);
	}
	
	public IMessage onMessage(MessageCustomServerAction message, MessageContext ctx)
	{
		EntityPlayer ep = LC.proxy.getClientPlayer();
		MinecraftForge.EVENT_BUS.post(new CustomActionEvent(ep, message.channel, message.action, message.extraData, Side.CLIENT));
		return null;
	}
}