package latmod.ftbu.core.net;
import io.netty.buffer.ByteBuf;
import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.api.FTBUApi;
import latmod.ftbu.mod.FTBU;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.Side;

public class MessageReload extends MessageLM<MessageReload>
{
	public MessageReload() { }
	
	public void fromBytes(ByteBuf bb)
	{
	}
	
	public void toBytes(ByteBuf bb)
	{
	}
	
	public IMessage onMessage(MessageReload m, MessageContext ctx)
	{
		FTBUApi.reload(Side.CLIENT, FTBU.proxy.getClientPlayer());
		LatCoreMC.printChat(FTBU.proxy.getClientPlayer(), "FTBU reloaded (Client)");
		return null;
	}
}