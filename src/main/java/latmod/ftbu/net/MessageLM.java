package latmod.ftbu.net;

import cpw.mods.fml.common.network.simpleimpl.*;
import io.netty.buffer.ByteBuf;
import latmod.core.util.ByteIOStream;
import latmod.ftbu.util.LatCoreMC;

public abstract class MessageLM<E extends MessageLM<?>> implements IMessage, IMessageHandler<E, IMessage>
{
	private static final ByteIOStream localIO = new ByteIOStream();
	
	public final void fromBytes(ByteBuf bb)
	{
		LatCoreMC.logger.info("[In] Message " + getClass() + " with " + bb.getClass());
		
		try
		{
			localIO.setData(new byte[bb.readShort() & 0xFFFF]);
			bb.readBytes(localIO.getRawBytes());
			readData(localIO);
		}
		catch(Exception e)
		{ e.printStackTrace(); }
	}
	
	public final void toBytes(ByteBuf bb)
	{
		LatCoreMC.logger.info("[Out] Message " + getClass() + " with " + bb.getClass());
		
		try
		{
			localIO.setData(new byte[0]);
			writeData(localIO);
			bb.writeShort(localIO.size());
			bb.writeBytes(localIO.getRawBytes(), 0, localIO.size());
		}
		catch(Exception e)
		{ e.printStackTrace(); }
	}
	
	public abstract void readData(ByteIOStream io) throws Exception;
	public abstract void writeData(ByteIOStream io) throws Exception;
	public abstract IMessage onMessage(E m, MessageContext ctx);
}