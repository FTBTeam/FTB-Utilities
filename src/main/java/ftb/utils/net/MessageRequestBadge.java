package ftb.utils.net;

import ftb.lib.api.net.*;
import ftb.lib.api.players.LMWorldMP;
import ftb.utils.badges.*;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.*;

import java.util.UUID;

public class MessageRequestBadge extends MessageLM<MessageRequestBadge>
{
	public UUID playerID;
	
	public MessageRequestBadge() { }
	
	public MessageRequestBadge(UUID player)
	{
		playerID = player;
	}
	
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET; }
	
	public void fromBytes(ByteBuf io)
	{
		playerID = readUUID(io);
	}
	
	public void toBytes(ByteBuf io)
	{
		writeUUID(io, playerID);
	}
	
	public IMessage onMessage(MessageRequestBadge m, MessageContext ctx)
	{
		Badge b = ServerBadges.getServerBadge(LMWorldMP.inst.getPlayer(m.playerID));
		if(b != Badge.emptyBadge) return new MessageSendBadge(m.playerID, b.ID);
		return null;
	}
}