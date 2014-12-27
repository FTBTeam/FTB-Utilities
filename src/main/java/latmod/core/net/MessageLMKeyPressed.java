package latmod.core.net;
import latmod.core.FastList;
import latmod.core.event.*;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.Side;

public class MessageLMKeyPressed extends MessageLM implements IMessageHandler<MessageLMKeyPressed, IMessage>
{
	public MessageLMKeyPressed() { }
	
	public MessageLMKeyPressed(FastList<Key> l)
	{
		data = new NBTTagCompound();
		data.setIntArray("K", Key.fromList(l));
	}
	
	public IMessage onMessage(MessageLMKeyPressed m, MessageContext ctx)
	{
		new LMKeyEvent(Side.SERVER, Key.toList(m.data.getIntArray("K")), ctx.getServerHandler().playerEntity).post();
		return null;
	}
}