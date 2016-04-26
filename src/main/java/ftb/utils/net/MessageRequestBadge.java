package ftb.utils.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import ftb.lib.api.net.LMNetworkWrapper;
import ftb.lib.api.net.MessageLM_IO;
import ftb.utils.badges.Badge;
import ftb.utils.badges.ServerBadges;
import latmod.lib.ByteCount;

import java.util.UUID;

public class MessageRequestBadge extends MessageLM_IO
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