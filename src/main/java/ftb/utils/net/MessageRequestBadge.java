package ftb.utils.net;

import cpw.mods.fml.common.network.simpleimpl.*;
import ftb.lib.api.net.LMNetworkWrapper;
import ftb.utils.badges.*;
import latmod.lib.ByteCount;

import java.util.UUID;

public class MessageRequestBadge extends MessageFTBU
{
	public MessageRequestBadge() { super(ByteCount.BYTE); }
	
	public MessageRequestBadge(UUID id)
	{
		this();
		io.writeUUID(id);
	}
	
	@Override
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_INFO; }
	
	@Override
	public IMessage onMessage(MessageContext ctx)
	{
		UUID id = io.readUUID();
		Badge b = ServerBadges.getServerBadge(id);
		return (b == null || b == Badge.emptyBadge) ? null : new MessageSendBadge(id, b.getID());
	}
}