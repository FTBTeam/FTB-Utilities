package latmod.ftbu.net;

import cpw.mods.fml.common.network.simpleimpl.*;
import io.netty.buffer.ByteBuf;
import latmod.core.util.ByteIOStream;

public abstract class MessageLM<E extends MessageLM<?>> implements IMessage, IMessageHandler<E, IMessage>
{
	public final void readData(ByteIOStream io) throws Exception { }
	public final void writeData(ByteIOStream io) throws Exception { }
	
	public abstract void fromBytes(ByteBuf io);
	public abstract void toBytes(ByteBuf io);
	public IMessage onMessage(E m, MessageContext ctx) { return null; }
}