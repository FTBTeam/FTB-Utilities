package latmod.ftbu.net;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import io.netty.buffer.ByteBuf;
import latmod.ftbu.api.EventLMPlayerClient;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.world.*;

public class MessageLMPlayerDied extends MessageLM<MessageLMPlayerDied> implements IClientMessageLM<MessageLMPlayerDied>
{
	public int playerID;
	
	public MessageLMPlayerDied() { }
	
	public MessageLMPlayerDied(LMPlayer p)
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
	
	public IMessage onMessage(MessageLMPlayerDied m, MessageContext ctx)
	{ FTBU.proxy.handleClientMessage(m, ctx); return null; }
	
	@SideOnly(Side.CLIENT)
	public void onMessageClient(MessageLMPlayerDied m, MessageContext ctx)
	{
		LMPlayerClient p = LMWorldClient.inst.getPlayer(m.playerID);
		if(p != null) new EventLMPlayerClient.PlayerDied(p).post();
	}
}