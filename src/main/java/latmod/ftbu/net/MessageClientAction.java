package latmod.ftbu.net;

import latmod.ftbu.world.*;
import latmod.lib.ByteCount;
import net.minecraftforge.fml.common.network.simpleimpl.*;

public class MessageClientAction extends MessageFTBU
{
	public MessageClientAction() { super(ByteCount.BYTE); }
	
	MessageClientAction(ClientAction a, int e)
	{
		this();
		io.writeByte((a == null) ? ClientAction.NULL.getID() : a.getID());
		io.writeInt(e);
	}
	
	public IMessage onMessage(MessageContext ctx)
	{
		ClientAction action = ClientAction.get(io.readByte());
		int extra = io.readInt();
		LMPlayerServer owner = LMWorldServer.inst.getPlayer(ctx.getServerHandler().playerEntity);
		if(action.onAction(extra, owner)) owner.sendUpdate();
		return null;
	}
}