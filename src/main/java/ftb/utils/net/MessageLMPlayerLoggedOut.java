package ftb.utils.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ftb.lib.api.net.LMNetworkWrapper;
import ftb.lib.api.net.MessageLM;
import ftb.utils.api.EventLMPlayerClient;
import ftb.utils.world.LMPlayer;
import ftb.utils.world.LMPlayerClient;
import ftb.utils.world.LMWorldClient;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class MessageLMPlayerLoggedOut extends MessageLM<MessageLMPlayerLoggedOut>
{
	public UUID playerID;
	
	public MessageLMPlayerLoggedOut() { }
	
	public MessageLMPlayerLoggedOut(LMPlayer p)
	{
		playerID = p.getProfile().getId();
	}
	
	@Override
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET; }
	
	@Override
	public void fromBytes(ByteBuf io)
	{
		playerID = readUUID(io);
	}
	
	@Override
	public void toBytes(ByteBuf io)
	{
		writeUUID(io, playerID);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageLMPlayerLoggedOut m, MessageContext ctx)
	{
		LMPlayerClient p = LMWorldClient.inst.getPlayer(m.playerID);
		new EventLMPlayerClient.LoggedOut(p).post();
		p.isOnline = false;
		return null;
	}
}