package latmod.ftbu.core.net;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import io.netty.buffer.ByteBuf;
import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.api.EventFTBUReload;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.client.badges.ThreadLoadBadges;

public class MessageReload extends MessageLM<MessageReload> implements IClientMessageLM<MessageReload>
{
	public MessageReload() { }
	
	public void fromBytes(ByteBuf bb)
	{
	}
	
	public void toBytes(ByteBuf bb)
	{
	}
	
	public IMessage onMessage(MessageReload m, MessageContext ctx)
	{ FTBU.proxy.handleClientMessage(m, ctx); return null; }
	
	@SideOnly(Side.CLIENT)
	public void onMessageClient(MessageReload m, MessageContext ctx)
	{
		ThreadLoadBadges.init();
		new EventFTBUReload(Side.CLIENT, FTBU.proxy.getClientPlayer()).post();
		LatCoreMC.printChat(FTBU.proxy.getClientPlayer(), "FTBU reloaded (Client)");
	}
}