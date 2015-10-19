package latmod.ftbu.net;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import ftb.lib.mod.FTBLib;
import io.netty.buffer.ByteBuf;
import latmod.ftbu.api.EventFTBUReload;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.client.FTBUClient;
import latmod.ftbu.util.LatCoreMC;

public class MessageReload extends MessageLM<MessageReload>
{
	public MessageReload() { }
	
	public void fromBytes(ByteBuf io)
	{
	}
	
	public void toBytes(ByteBuf io)
	{
	}
	
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageReload m, MessageContext ctx)
	{
		FTBLib.reloadGameModes();
		FTBUClient.onReloaded();
		new EventFTBUReload(Side.CLIENT, FTBU.proxy.getClientPlayer()).post();
		LatCoreMC.printChat(FTBU.proxy.getClientPlayer(), "FTBU reloaded (Client)");
		return null;
	}
}