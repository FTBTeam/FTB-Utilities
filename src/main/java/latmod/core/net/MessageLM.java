package latmod.core.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.*;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class MessageLM implements IMessage
{
	public NBTTagCompound data = null;
	
	public final void fromBytes(ByteBuf bb)
	{
		data = null;
		
		short s = bb.readShort();
		if (s >= 0)
		{
			byte[] b = new byte[s]; bb.readBytes(b);
			try { data = CompressedStreamTools.func_152457_a(b, new NBTSizeTracker(2097152L)); }
			catch(Exception e) { }
		}
	}
	
	public final void toBytes(ByteBuf bb)
	{
		if (data == null) bb.writeShort(-1);
		else
		{
			try
			{
				byte[] b = CompressedStreamTools.compress(data);
				bb.writeShort((short)b.length);
				bb.writeBytes(b);
			}
			catch(Exception e) { bb.writeByte(-1); }
		}
	}
}