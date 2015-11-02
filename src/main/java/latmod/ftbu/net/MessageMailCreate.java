package latmod.ftbu.net;

import cpw.mods.fml.common.network.simpleimpl.*;
import ftb.lib.api.LMNetworkWrapper;
import latmod.ftbu.world.*;

public class MessageMailCreate extends MessageFTBU
{
	public MessageMailCreate() { super(DATA_LONG); }
	
	public MessageMailCreate(LMPlayer to)
	{
		this();
		io.writeInt(to.playerID);
	}
	
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_MAIL; }
	
	public IMessage onMessage(MessageContext ctx)
	{
		LMPlayerServer to = LMWorldServer.inst.getPlayer(io.readInt());
		
		if(to != null)
		{
			Mail mail = new Mail(++LMWorldServer.inst.lastMailID, to);
			mail.sender = LMWorldServer.inst.getPlayer(ctx.getServerHandler().playerEntity);
			LMWorldServer.inst.tempMail.add(mail);
			return new MessageMailUpdate(mail);
		}
		
		return null;
	}
}