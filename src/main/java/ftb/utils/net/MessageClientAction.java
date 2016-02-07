package ftb.utils.net;

import ftb.lib.api.net.*;
import ftb.utils.world.*;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.*;

public class MessageClientAction extends MessageLM<MessageClientAction>
{
	public byte actionID;
	public int extra;
	
	public MessageClientAction() { }
	
	MessageClientAction(ClientAction a, int e)
	{
		actionID = (a == null) ? ClientAction.NULL.getID() : a.getID();
		extra = e;
	}
	
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET; }
	
	public void fromBytes(ByteBuf io)
	{
		actionID = io.readByte();
		extra = io.readInt();
	}
	
	public void toBytes(ByteBuf io)
	{
		io.writeByte(actionID);
		io.writeInt(extra);
	}
	
	public IMessage onMessage(MessageClientAction m, MessageContext ctx)
	{
		ClientAction action = ClientAction.get(m.actionID);
		LMPlayerServer owner = LMWorldServer.inst.getPlayer(ctx.getServerHandler().playerEntity);
		if(action.onAction(m.extra, owner)) owner.sendUpdate();
		return null;
	}
}