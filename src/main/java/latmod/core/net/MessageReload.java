package latmod.core.net;
import latmod.core.LatCoreMC;
import latmod.core.event.ReloadEvent;
import latmod.core.mod.LC;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.Side;

public class MessageReload extends MessageLM implements IMessageHandler<MessageReload, IMessage>
{
	public MessageReload() { }
	
	public IMessage onMessage(MessageReload m, MessageContext ctx)
	{
		new ReloadEvent(Side.CLIENT, LC.proxy.getClientPlayer()).post();
		LatCoreMC.printChat(LC.proxy.getClientPlayer(), "LatvianModders's mods reloaded (Client)");
		return null;
	}
}