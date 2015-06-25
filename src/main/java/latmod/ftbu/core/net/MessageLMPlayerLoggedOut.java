package latmod.ftbu.core.net;
import io.netty.buffer.ByteBuf;
import latmod.ftbu.core.LMPlayer;
import latmod.ftbu.core.event.LMPlayerEvent;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.Side;

public class MessageLMPlayerLoggedOut extends MessageLM<MessageLMPlayerLoggedOut>
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
	{
		LMPlayer p = LMPlayer.getPlayer(m.playerID);
		new LMPlayerEvent.LoggedOut(p, Side.CLIENT, p.getPlayerMP()).post();
		return null;
	}
}