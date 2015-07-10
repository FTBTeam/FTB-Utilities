package latmod.ftbu.core.net;
import io.netty.buffer.ByteBuf;
import latmod.ftbu.core.event.LMPlayerClientEvent;
import latmod.ftbu.core.world.*;
import latmod.ftbu.mod.FTBU;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;

public class MessageLMPlayerLoggedOut extends MessageLM<MessageLMPlayerLoggedOut> implements IClientMessageLM<MessageLMPlayerLoggedOut>
{
	public int playerID;
	
	public MessageLMPlayerLoggedOut() { }
	
	public MessageLMPlayerLoggedOut(LMPlayer p)
	{
		playerID = p.playerID;
	}
	
	public void fromBytes(ByteBuf bb)
	{
		playerID = bb.readInt();
	}
	
	public void toBytes(ByteBuf bb)
	{
		bb.writeInt(playerID);
	}
	
	public IMessage onMessage(MessageLMPlayerLoggedOut m, MessageContext ctx)
	{ FTBU.proxy.handleClientMessage(m, ctx); return null; }
	
	@SideOnly(Side.CLIENT)
	public void onMessageClient(MessageLMPlayerLoggedOut m, MessageContext ctx)
	{
		LMPlayerClient p = LMWorldClient.inst.getPlayer(m.playerID);
		new LMPlayerClientEvent.LoggedOut(p).post();
	}
}