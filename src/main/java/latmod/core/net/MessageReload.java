package latmod.core.net;
import latmod.core.LatCoreMC;
import latmod.core.event.ReloadEvent;
import latmod.core.mod.LC;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

public class MessageReload extends MessageLM<MessageReload>
{
	public MessageReload() { }
	
	public void onMessage(MessageContext ctx)
	{
		new ReloadEvent(Side.CLIENT, LC.proxy.getClientPlayer()).post();
		LatCoreMC.printChat(LC.proxy.getClientPlayer(), "LatvianModders's mods reloaded (Client)");
	}
}