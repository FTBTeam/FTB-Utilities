package latmod.ftbu.net;

import cpw.mods.fml.common.network.simpleimpl.*;
import io.netty.buffer.ByteBuf;
import latmod.lib.ByteIOStream;

public class MessageByteArray<E extends MessageByteArray<?>> extends MessageLM<E>
{
	public final ByteIOStream io = new ByteIOStream();
	
	public final void fromBytes(ByteBuf bb)
	{
		byte[] b = new byte[bb.readInt()];
		bb.readBytes(b, 0, b.length);
		io.setCompressedData(b);
	}
	
	public final void toBytes(ByteBuf bb)
	{
		byte[] b = io.toCompressedByteArray();
		bb.writeInt(b.length);
		bb.writeBytes(b, 0, b.length);
	}
	
	public IMessage onMessage(E m, MessageContext ctx)
	{ return null; }
}