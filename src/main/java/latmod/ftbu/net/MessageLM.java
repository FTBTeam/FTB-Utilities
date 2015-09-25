package latmod.ftbu.net;

import cpw.mods.fml.common.network.simpleimpl.*;
import io.netty.buffer.ByteBuf;

public abstract class MessageLM<E extends MessageLM<?>> implements IMessage, IMessageHandler<E, IMessage>
{
	public abstract void fromBytes(ByteBuf bb);
	public abstract void toBytes(ByteBuf bb);
	public abstract IMessage onMessage(E m, MessageContext ctx);
}