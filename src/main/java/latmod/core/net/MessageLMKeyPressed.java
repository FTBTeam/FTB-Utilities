package latmod.core.net;
import latmod.core.event.LMKeyEvent;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.Side;

public class MessageLMKeyPressed extends MessageLM implements IMessageHandler<MessageLMKeyPressed, IMessage>
{
	public MessageLMKeyPressed() { }
	
	public MessageLMKeyPressed(boolean shift, boolean ctrl)
	{
		data = new NBTTagCompound();
		data.setBoolean("S", shift);
		data.setBoolean("C", ctrl);
	}
	
	public IMessage onMessage(MessageLMKeyPressed m, MessageContext ctx)
	{
		boolean s = m.data.getBoolean("S");
		boolean c = m.data.getBoolean("C");
		new LMKeyEvent(Side.SERVER, s, c, ctx.getServerHandler().playerEntity).post();
		return null;
	}
}