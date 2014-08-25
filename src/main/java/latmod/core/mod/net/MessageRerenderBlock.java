package latmod.core.mod.net;

import io.netty.buffer.ByteBuf;
import latmod.core.mod.LC;
import cpw.mods.fml.common.network.simpleimpl.*;

public class MessageRerenderBlock implements IMessage, IMessageHandler<MessageRerenderBlock, IMessage>
{
	public int posX, posY, posZ;
	
	public MessageRerenderBlock() { }
	
	public MessageRerenderBlock(int x, int y, int z)
	{ posX = x; posY = y; posZ = z; }
	
	public void fromBytes(ByteBuf data)
	{
		posX = data.readInt();
		posY = data.readInt();
		posZ = data.readInt();
	}
	
	public void toBytes(ByteBuf data)
	{
		data.writeInt(posX);
		data.writeInt(posY);
		data.writeInt(posZ);
	}
	
	public IMessage onMessage(MessageRerenderBlock message, MessageContext ctx)
	{
		LC.proxy.rerenderBlock(message.posX, message.posY, message.posZ);
		return null;
	}
	
	public static void send(int x, int y, int z, int dimID)
	{ LMNetHandler.INSTANCE.sendToDimension(new MessageRerenderBlock(x, y, z), dimID); }
}