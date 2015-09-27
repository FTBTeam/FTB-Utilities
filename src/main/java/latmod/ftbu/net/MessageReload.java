package latmod.ftbu.net;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.Side;
import latmod.core.util.ByteIOStream;
import latmod.ftbu.api.EventFTBUReload;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.client.FTBUClient;
import latmod.ftbu.util.LatCoreMC;

public class MessageReload extends MessageLM<MessageReload>
{
	public MessageReload() { }
	
	public void readData(ByteIOStream io) throws Exception
	{
	}
	
	public void writeData(ByteIOStream io) throws Exception
	{
	}
	
	public IMessage onMessage(MessageReload m, MessageContext ctx)
	{
		FTBUClient.onReloaded();
		new EventFTBUReload(Side.CLIENT, FTBU.proxy.getClientPlayer()).post();
		LatCoreMC.printChat(FTBU.proxy.getClientPlayer(), "FTBU reloaded (Client)");
		return null;
	}
}