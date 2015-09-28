package latmod.ftbu.net;

import cpw.mods.fml.common.network.simpleimpl.*;
import io.netty.buffer.ByteBuf;
import latmod.core.util.ByteIOStream;
import latmod.ftbu.mod.FTBUFinals;
import latmod.ftbu.util.LatCoreMC;

public abstract class MessageLM<E extends MessageLM<?>> implements IMessage, IMessageHandler<E, IMessage>
{
	private static final ByteIOStream localIO = new ByteIOStream();
	
	public final void fromBytes(ByteBuf bb)
	{
		if(FTBUFinals.DEV) LatCoreMC.logger.info("[In] Message " + getClass());
		
		try
		{
			byte[] b = new byte[bb.readShort() & 0xFFFF];
			bb.readBytes(b, 0, b.length);
			localIO.setData(b, true);
			readData(localIO);
		}
		catch(Exception e)
		{ e.printStackTrace(); }
	}
	
	public final void toBytes(ByteBuf bb)
	{
		if(FTBUFinals.DEV) LatCoreMC.logger.info("[Out] Message " + getClass());
		
		try
		{
			localIO.setData(new byte[16], true);
			writeData(localIO);
			byte[] b = localIO.toByteArray(true);
			bb.writeShort((short)b.length);
			bb.writeBytes(b, 0, b.length);
		}
		catch(Exception e)
		{ e.printStackTrace(); }
	}
	
	public abstract void readData(ByteIOStream io) throws Exception;
	public abstract void writeData(ByteIOStream io) throws Exception;
	public abstract IMessage onMessage(E m, MessageContext ctx);
}