package latmod.ftbu.net;

import cpw.mods.fml.common.network.simpleimpl.*;
import io.netty.buffer.ByteBuf;
import latmod.lib.ByteIOStream;
import net.minecraft.entity.player.EntityPlayerMP;

public abstract class MessageLM<E extends MessageLM<?>> implements IMessage, IMessageHandler<E, IMessage>
{
	public final void readData(ByteIOStream io) throws Exception { }
	public final void writeData(ByteIOStream io) throws Exception { }
	
	public abstract void fromBytes(ByteBuf io);
	public abstract void toBytes(ByteBuf io);
	public IMessage onMessage(E m, MessageContext ctx) { return null; }
	
	public SimpleNetworkWrapper getWrapper()
	{ return LMNetHelper.NET; }
	
	public final void sendTo(EntityPlayerMP ep)
	{
		if(ep != null) getWrapper().sendTo(this, ep);
		else getWrapper().sendToAll(this);
	}
	
	public final void sendToServer()
	{ getWrapper().sendToServer(this); }
}