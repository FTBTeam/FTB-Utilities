package ftb.utils.net;

import ftb.lib.api.friends.*;
import ftb.lib.api.net.*;
import ftb.lib.mod.net.MessageLMPlayerInfo;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.*;

import java.util.UUID;

public class MessagePlayerInfo extends MessageLM<MessagePlayerInfo>
{
	public UUID playerID;
	
	public MessagePlayerInfo() { }
	
	public MessagePlayerInfo(UUID player)
	{
		playerID = player;
	}
	
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_INFO; }
	
	public void fromBytes(ByteBuf io)
	{
		playerID = readUUID(io);
	}
	
	public void toBytes(ByteBuf io)
	{
		writeUUID(io, playerID);
	}
	
	public IMessage onMessage(MessagePlayerInfo m, MessageContext ctx)
	{
		LMPlayerMP owner = LMWorldMP.inst.getPlayer(ctx.getServerHandler().playerEntity);
		LMPlayerMP p = LMWorldMP.inst.getPlayer(m.playerID);
		if(p != null) new MessageLMPlayerInfo(owner, p).sendTo(owner.getPlayer());
		return null;
	}
}